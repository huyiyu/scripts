# 使用 Ansible 搭建 Kubernetes 1.35 集群

本文档介绍如何使用 Ansible 在 Ubuntu 24.04 上自动化部署 Kubernetes 1.35 集群。

参考文档：https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/

## 架构概述

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           Kubernetes 1.35 集群                           │
├─────────────────────────────────┬───────────────────────────────────────┤
│      Control Plane Node         │           Worker Nodes                │
│    (k8s-master01, 192.168.1.10) │   (k8s-worker01~03, 192.168.1.11-13)  │
├─────────────────────────────────┼───────────────────────────────────────┤
│  • kube-apiserver               │   • kubelet                           │
│  • kube-controller-manager      │   • kube-proxy                        │
│  • kube-scheduler               │   • containerd (CRI)                  │
│  • etcd                         │                                       │
│  • kubelet                      │                                       │
│  • containerd (CRI)             │                                       │
│  • Calico CNI                   │   • Calico CNI                        │
└─────────────────────────────────┴───────────────────────────────────────┘
```

## 环境要求

### 服务器配置

| 角色 | 数量 | CPU | 内存 | 磁盘 | OS |
|------|------|-----|------|------|-----|
| Control Plane | 1+ | 2核+ | 4GB+ | 20GB+ | Ubuntu 24.04 LTS |
| Worker | 1+ | 2核+ | 4GB+ | 20GB+ | Ubuntu 24.04 LTS |

### 网络要求

| 协议 | 端口 | 源 | 用途 |
|------|------|-----|------|
| TCP | 6443 | All | Kubernetes API Server |
| TCP | 2379-2380 | Control Plane | etcd server client API |
| TCP | 10250 | All | Kubelet API |
| TCP | 10259 | Control Plane | kube-scheduler |
| TCP | 10257 | Control Plane | kube-controller-manager |
| UDP | 8472 | All | VXLAN (Calico/Flannel) |
| TCP | 179 | All | BGP (Calico) |

## 项目结构

```
k8s-ansible/
├── ansible.cfg
├── inventory/
│   └── hosts.ini
├── group_vars/
│   └── all.yml
├── roles/
│   ├── common/
│   │   ├── tasks/
│   │   │   └── main.yml
│   │   └── handlers/
│   │       └── main.yml
│   ├── containerd/
│   │   ├── tasks/
│   │   │   └── main.yml
│   │   └── templates/
│   │       └── containerd.service.j2
│   ├── k8s-packages/
│   │   └── tasks/
│   │       └── main.yml
│   ├── master/
│   │   ├── tasks/
│   │   │   └── main.yml
│   │   └── templates/
│   │       └── kubeadm-config.yaml.j2
│   └── worker/
│       └── tasks/
│           └── main.yml
├── playbooks/
│   └── site.yml
└── README.md
```

## 详细配置

### 1. Ansible 配置文件

**`ansible.cfg`**

```ini
[defaults]
inventory = inventory/hosts.ini
remote_user = ubuntu
private_key_file = ~/.ssh/id_rsa
host_key_checking = False
retry_files_enabled = False
stdout_callback = yaml
gathering = smart
fact_caching = jsonfile
fact_caching_connection = /tmp/ansible_facts_cache
fact_caching_timeout = 86400

[privilege_escalation]
become = True
become_method = sudo
become_user = root
become_ask_pass = False

[ssh_connection]
pipelining = True
control_path = /tmp/ansible-ssh-%%h-%%p-%%r
```

### 2. 主机清单

**`inventory/hosts.ini`**

```ini
[masters]
k8s-master01 ansible_host=192.168.1.10

[workers]
k8s-worker01 ansible_host=192.168.1.11
k8s-worker02 ansible_host=192.168.1.12
k8s-worker03 ansible_host=192.168.1.13

[k8s:children]
masters
workers

[k8s:vars]
ansible_user=ubuntu
ansible_ssh_private_key_file=~/.ssh/id_rsa
ansible_python_interpreter=/usr/bin/python3
```

### 3. 变量配置

**`group_vars/all.yml`**

```yaml
---
# Kubernetes 版本
k8s_version: "1.35"

# 集群配置
cluster_name: "k8s-cluster"
control_plane_endpoint: "192.168.1.10:6443"
pod_network_cidr: "10.244.0.0/16"
service_cidr: "10.96.0.0/12"

# CNI 配置
cni_provider: "calico"
calico_version: "v3.28.0"

# Containerd 版本
containerd_version: "2.0.0"
runc_version: "1.1.12"
cni_plugins_version: "1.5.1"

# 网络接口（根据实际情况修改）
network_interface: "eth0"
```

### 4. Common 角色 - 系统准备

**`roles/common/tasks/main.yml`**

```yaml
---
# 系统基础配置
- name: Update apt cache
  apt:
    update_cache: yes
    cache_valid_time: 3600

- name: Install required packages
  apt:
    name:
      - apt-transport-https
      - ca-certificates
      - curl
      - gnupg
      - lsb-release
      - software-properties-common
      - python3-pip
      - jq
      - bash-completion
      - vim
      - net-tools
      - ipset
      - ipvsadm
    state: present

# 配置系统参数
- name: Load kernel modules for Kubernetes
  modprobe:
    name: "{{ item }}"
    state: present
  loop:
    - overlay
    - br_netfilter

- name: Ensure kernel modules persist
  lineinfile:
    path: /etc/modules-load.d/k8s.conf
    line: "{{ item }}"
    create: yes
  loop:
    - overlay
    - br_netfilter

# 配置 sysctl 参数
- name: Configure sysctl for Kubernetes
  sysctl:
    name: "{{ item.key }}"
    value: "{{ item.value }}"
    state: present
    reload: yes
    sysctl_file: /etc/sysctl.d/k8s.conf
  loop:
    - { key: "net.bridge.bridge-nf-call-iptables", value: "1" }
    - { key: "net.bridge.bridge-nf-call-ip6tables", value: "1" }
    - { key: "net.ipv4.ip_forward", value: "1" }
    - { key: "net.ipv6.conf.all.forwarding", value: "1" }
    - { key: "vm.swappiness", value: "0" }

# 禁用 Swap
- name: Disable swap immediately
  command: swapoff -a
  changed_when: true

- name: Remove swap from /etc/fstab
  replace:
    path: /etc/fstab
    regexp: '^([^#].*?\s+swap\s+.*)$'
    replace: '# \1'

# 配置时区
- name: Set timezone to Asia/Shanghai
  timezone:
    name: Asia/Shanghai

# 禁用防火墙（生产环境请谨慎）
- name: Disable UFW
  systemd:
    name: ufw
    state: stopped
    enabled: no
  ignore_errors: yes

# 配置 hosts 文件
- name: Add hosts entries
  lineinfile:
    path: /etc/hosts
    line: "{{ hostvars[item]['ansible_host'] }} {{ item }}"
    state: present
  loop: "{{ groups['k8s'] }}"
```

**`roles/common/handlers/main.yml`**

```yaml
---
- name: Reload sysctl
  command: sysctl --system
```

### 5. Containerd 角色 - 容器运行时

**`roles/containerd/tasks/main.yml`**

```yaml
---
# 安装 containerd 依赖
- name: Install containerd dependencies
  apt:
    name:
      - curl
      - gnupg
    state: present

# 创建安装目录
- name: Create containerd directories
  file:
    path: "{{ item }}"
    state: directory
    mode: '0755'
  loop:
    - /etc/containerd
    - /usr/local/lib/systemd/system
    - /opt/cni/bin

# 下载并安装 containerd
- name: Download containerd
  get_url:
    url: "https://github.com/containerd/containerd/releases/download/v{{ containerd_version }}/containerd-{{ containerd_version }}-linux-amd64.tar.gz"
    dest: "/tmp/containerd-{{ containerd_version }}-linux-amd64.tar.gz"
    mode: '0644'
  register: containerd_download

- name: Extract containerd
  unarchive:
    src: "/tmp/containerd-{{ containerd_version }}-linux-amd64.tar.gz"
    dest: /usr/local
    remote_src: yes
  when: containerd_download.changed

# 下载并安装 runc
- name: Download runc
  get_url:
    url: "https://github.com/opencontainers/runc/releases/download/v{{ runc_version }}/runc.amd64"
    dest: /tmp/runc.amd64
    mode: '0644'

- name: Install runc
  copy:
    src: /tmp/runc.amd64
    dest: /usr/local/sbin/runc
    mode: '0755'
    remote_src: yes

# 下载并安装 CNI 插件
- name: Download CNI plugins
  get_url:
    url: "https://github.com/containernetworking/plugins/releases/download/v{{ cni_plugins_version }}/cni-plugins-linux-amd64-v{{ cni_plugins_version }}.tgz"
    dest: "/tmp/cni-plugins-linux-amd64-v{{ cni_plugins_version }}.tgz"
    mode: '0644'

- name: Extract CNI plugins
  unarchive:
    src: "/tmp/cni-plugins-linux-amd64-v{{ cni_plugins_version }}.tgz"
    dest: /opt/cni/bin
    remote_src: yes

# 下载 containerd systemd 服务文件
- name: Download containerd service file
  get_url:
    url: https://raw.githubusercontent.com/containerd/containerd/main/containerd.service
    dest: /usr/local/lib/systemd/system/containerd.service
    mode: '0644'

# 生成 containerd 配置
- name: Generate default containerd config
  shell: containerd config default > /etc/containerd/config.toml
  args:
    creates: /etc/containerd/config.toml

# 配置 systemd cgroup driver (Containerd 2.x)
- name: Configure systemd cgroup driver for containerd 2.x
  lineinfile:
    path: /etc/containerd/config.toml
    regexp: 'SystemdCgroup = false'
    line: '            SystemdCgroup = true'
    state: present

# 配置 sandbox_image
- name: Configure sandbox image
  lineinfile:
    path: /etc/containerd/config.toml
    regexp: 'sandbox_image = "registry.k8s.io/pause:.*"'
    line: '    sandbox_image = "registry.k8s.io/pause:3.10"'
    state: present

# 启动 containerd
- name: Reload systemd daemon
  systemd:
    daemon_reload: yes

- name: Enable and start containerd
  systemd:
    name: containerd
    state: started
    enabled: yes

- name: Verify containerd is running
  shell: systemctl is-active containerd
  register: containerd_status
  changed_when: false

- name: Fail if containerd is not running
  fail:
    msg: "Containerd is not running!"
  when: containerd_status.stdout != "active"
```

### 6. Kubernetes 包角色

**`roles/k8s-packages/tasks/main.yml`**

```yaml
---
# 添加 Kubernetes APT 仓库 (Kubernetes 1.35+)
# 注意：从 2023年9月13日起，旧仓库 apt.kubernetes.io 已弃用
# 新仓库使用 pkgs.k8s.io，每个小版本有独立仓库

- name: Create apt keyrings directory
  file:
    path: /etc/apt/keyrings
    state: directory
    mode: '0755'

- name: Download Kubernetes GPG key
  shell: |
    curl -fsSL https://pkgs.k8s.io/core:/stable:/v{{ k8s_version }}/deb/Release.key | \
    gpg --dearmor -o /etc/apt/keyrings/kubernetes-apt-keyring.gpg
  args:
    creates: /etc/apt/keyrings/kubernetes-apt-keyring.gpg

- name: Add Kubernetes apt repository
  apt_repository:
    repo: "deb [signed-by=/etc/apt/keyrings/kubernetes-apt-keyring.gpg] https://pkgs.k8s.io/core:/stable:/v{{ k8s_version }}/deb/ /"
    state: present
    filename: kubernetes
    update_cache: yes

- name: Install kubelet, kubeadm and kubectl
  apt:
    name:
      - kubelet
      - kubeadm
      - kubectl
    state: present
    update_cache: yes

# 固定版本，防止自动升级
- name: Hold kubelet, kubeadm and kubectl versions
  dpkg_selections:
    name: "{{ item }}"
    selection: hold
  loop:
    - kubelet
    - kubeadm
    - kubectl

# 启用 kubelet 服务
- name: Enable kubelet service
  systemd:
    name: kubelet
    enabled: yes

# 配置 kubelet 使用 systemd cgroup driver
- name: Configure kubelet to use systemd cgroup driver
  file:
    path: /etc/default/kubelet
    state: touch
    mode: '0644'

- name: Add kubelet extra args
  lineinfile:
    path: /etc/default/kubelet
    line: 'KUBELET_EXTRA_ARGS="--cgroup-driver=systemd"'
    create: yes
    state: present
```

### 7. Master 节点角色

**`roles/master/templates/kubeadm-config.yaml.j2`**

```yaml
apiVersion: kubeadm.k8s.io/v1beta4
kind: InitConfiguration
localAPIEndpoint:
  advertiseAddress: {{ ansible_default_ipv4.address }}
  bindPort: 6443
nodeRegistration:
  criSocket: unix:///run/containerd/containerd.sock
  imagePullPolicy: IfNotPresent
  kubeletExtraArgs:
    cgroup-driver: systemd
---
apiVersion: kubeadm.k8s.io/v1beta4
kind: ClusterConfiguration
kubernetesVersion: v{{ k8s_version }}.0
controlPlaneEndpoint: {{ control_plane_endpoint }}
networking:
  podSubnet: {{ pod_network_cidr }}
  serviceSubnet: {{ service_cidr }}
dns:
  imageRepository: registry.k8s.io/coredns
---
apiVersion: kubelet.config.k8s.io/v1beta1
kind: KubeletConfiguration
cgroupDriver: systemd
```

**`roles/master/tasks/main.yml`**

```yaml
---
# 初始化 Kubernetes 控制平面
- name: Check if Kubernetes is already initialized
  stat:
    path: /etc/kubernetes/admin.conf
  register: k8s_initialized

- name: Reset Kubernetes if already initialized (optional)
  shell: kubeadm reset -f
  when: 
    - k8s_initialized.stat.exists
    - force_reset | default(false) | bool

- name: Create kubeadm config directory
  file:
    path: /root/kubeadm
    state: directory
    mode: '0755'

- name: Copy kubeadm configuration
  template:
    src: kubeadm-config.yaml.j2
    dest: /root/kubeadm/kubeadm-config.yaml
    mode: '0644'

- name: Pull Kubernetes images
  shell: kubeadm config images pull
  when: not k8s_initialized.stat.exists or (force_reset | default(false) | bool)

- name: Initialize Kubernetes cluster
  shell: |
    kubeadm init --config=/root/kubeadm/kubeadm-config.yaml
  when: not k8s_initialized.stat.exists or (force_reset | default(false) | bool)
  register: kubeadm_init

# 配置 kubectl
- name: Create .kube directory for root
  file:
    path: /root/.kube
    state: directory
    mode: '0755'

- name: Copy admin.conf to root's kube config
  copy:
    src: /etc/kubernetes/admin.conf
    dest: /root/.kube/config
    remote_src: yes
    mode: '0644'

- name: Create .kube directory for ubuntu user
  file:
    path: /home/ubuntu/.kube
    state: directory
    mode: '0755'
    owner: ubuntu
    group: ubuntu

- name: Copy admin.conf to ubuntu user's kube config
  copy:
    src: /etc/kubernetes/admin.conf
    dest: /home/ubuntu/.kube/config
    remote_src: yes
    mode: '0644'
    owner: ubuntu
    group: ubuntu

# 安装 CNI (Calico)
- name: Download Calico manifests
  get_url:
    url: "https://raw.githubusercontent.com/projectcalico/calico/{{ calico_version }}/manifests/calico.yaml"
    dest: /root/calico.yaml
    mode: '0644'

- name: Install Calico CNI
  shell: kubectl apply -f /root/calico.yaml
  environment:
    KUBECONFIG: /etc/kubernetes/admin.conf

# 等待 Calico 就绪
- name: Wait for Calico pods to be ready
  shell: |
    kubectl wait --for=condition=ready pod \
      -l k8s-app=calico-node \
      -n kube-system \
      --timeout=300s
  environment:
    KUBECONFIG: /etc/kubernetes/admin.conf
  ignore_errors: yes

# 生成加入命令
- name: Generate join command
  shell: kubeadm token create --print-join-command
  register: join_command
  changed_when: false

- name: Save join command to file
  copy:
    content: "{{ join_command.stdout }}"
    dest: /root/kubeadm/join-command.sh
    mode: '0750'

- name: Display join command
  debug:
    msg: "Worker join command: {{ join_command.stdout }}"

# 可选：允许在控制平面调度 Pod
- name: Remove control plane taint (optional for single node)
  shell: |
    kubectl taint nodes --all node-role.kubernetes.io/control-plane- 2>/dev/null || true
  environment:
    KUBECONFIG: /etc/kubernetes/admin.conf
  ignore_errors: yes
  when: allow_pods_on_control_plane | default(false) | bool

# 验证集群状态
- name: Get cluster info
  shell: kubectl cluster-info
  environment:
    KUBECONFIG: /etc/kubernetes/admin.conf
  register: cluster_info
  changed_when: false

- name: Display cluster info
  debug:
    var: cluster_info.stdout_lines

- name: Get node status
  shell: kubectl get nodes -o wide
  environment:
    KUBECONFIG: /etc/kubernetes/admin.conf
  register: node_status
  changed_when: false

- name: Display node status
  debug:
    var: node_status.stdout_lines
```

### 8. Worker 节点角色

**`roles/worker/tasks/main.yml`**

```yaml
---
# 加入 Kubernetes 集群
- name: Check if node is already joined
  stat:
    path: /etc/kubernetes/kubelet.conf
  register: kubelet_config

- name: Get join command from master
  shell: cat /root/kubeadm/join-command.sh
  delegate_to: "{{ groups['masters'][0] }}"
  register: join_command_raw
  changed_when: false
  run_once: true
  when: not kubelet_config.stat.exists

- name: Join Kubernetes cluster
  shell: |
    {{ join_command_raw.stdout }}
  when: not kubelet_config.stat.exists
  register: join_result

- name: Verify kubelet is running
  systemd:
    name: kubelet
    state: started
    enabled: yes

- name: Wait for kubelet to stabilize
  pause:
    seconds: 10
  when: not kubelet_config.stat.exists
```

### 9. 主 Playbook

**`playbooks/site.yml`**

```yaml
---
# Kubernetes 1.35 集群部署 Playbook
# 支持 Ubuntu 24.04

- name: Apply common configuration to all nodes
  hosts: k8s
  become: yes
  roles:
    - common

- name: Install containerd on all nodes
  hosts: k8s
  become: yes
  roles:
    - containerd

- name: Install Kubernetes packages on all nodes
  hosts: k8s
  become: yes
  roles:
    - k8s-packages

- name: Initialize Kubernetes control plane
  hosts: masters
  become: yes
  roles:
    - master

- name: Join worker nodes to cluster
  hosts: workers
  become: yes
  roles:
    - worker
```

## 部署步骤

### 1. 准备工作

```bash
# 安装 Ansible
sudo apt update
sudo apt install -y ansible

# 验证 Ansible 版本
ansible --version

# 创建工作目录
mkdir -p ~/k8s-ansible
cd ~/k8s-ansible

# 创建项目结构
mkdir -p inventory group_vars roles/{common,containerd,k8s-packages,master,worker}/{tasks,templates,handlers} playbooks
```

### 2. 配置 SSH 免密登录

```bash
# 生成 SSH 密钥（如未生成）
ssh-keygen -t rsa -b 4096

# 复制公钥到所有节点
for host in 192.168.1.10 192.168.1.11 192.168.1.12 192.168.1.13; do
    ssh-copy-id ubuntu@$host
done
```

### 3. 执行部署

```bash
# 检查连通性
ansible all -m ping

# 语法检查
ansible-playbook playbooks/site.yml --syntax-check

# 试运行（干跑）
ansible-playbook playbooks/site.yml --check

# 正式部署
ansible-playbook playbooks/site.yml

# 查看详细输出
ansible-playbook playbooks/site.yml -v
```

### 4. 验证集群

在控制平面节点上执行：

```bash
# 检查节点状态
kubectl get nodes -o wide

# 检查系统 Pod
kubectl get pods -n kube-system

# 检查 Calico Pod
kubectl get pods -n kube-system -l k8s-app=calico-node

# 查看集群信息
kubectl cluster-info

# 查看版本
kubectl version
```

## 常用运维命令

### 添加新 Worker 节点

```bash
# 在控制平面生成新的加入命令
kubeadm token create --print-join-command

# 在新节点上执行生成的命令
```

### 升级 Kubernetes

```bash
# 1. 升级 kubeadm
sudo apt-mark unhold kubeadm && \
sudo apt-get update && \
sudo apt-get install -y kubeadm=1.35.x-00 && \
sudo apt-mark hold kubeadm

# 2. 验证升级计划
sudo kubeadm upgrade plan

# 3. 执行升级
sudo kubeadm upgrade apply v1.35.x

# 4. 升级 kubelet 和 kubectl
sudo apt-mark unhold kubelet kubectl && \
sudo apt-get update && \
sudo apt-get install -y kubelet=1.35.x-00 kubectl=1.35.x-00 && \
sudo apt-mark hold kubelet kubectl

# 5. 重启 kubelet
sudo systemctl daemon-reload
sudo systemctl restart kubelet
```

### 重置节点

```bash
# 排空节点
kubectl drain <node-name> --delete-emptydir-data --force --ignore-daemonsets

# 重置 kubeadm
sudo kubeadm reset

# 清理 iptables
sudo iptables -F && sudo iptables -t nat -F && sudo iptables -t mangle -F && sudo iptables -X

# 删除节点
kubectl delete node <node-name>
```

## 故障排查

### 检查组件状态

```bash
# 查看 kubelet 日志
sudo journalctl -u kubelet -f

# 查看 containerd 日志
sudo journalctl -u containerd -f

# 查看 kubeadm 初始化日志
sudo kubeadm init --v=5
```

### 常见问题

| 问题 | 解决方案 |
|------|----------|
| `cgroup driver mismatch` | 确保 containerd 和 kubelet 都使用 systemd cgroup driver |
| `node NotReady` | 检查 CNI 插件是否正确安装 |
| `Failed to create shim` | 检查 containerd 配置和 cgroup 驱动 |
| `connection refused` | 检查 API Server 和端口配置 |

## 参考文档

- [Kubernetes 官方文档](https://kubernetes.io/docs/)
- [kubeadm 安装指南](https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/install-kubeadm/)
- [Container Runtimes](https://kubernetes.io/docs/setup/production-environment/container-runtimes/)
- [Creating a cluster with kubeadm](https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/create-cluster-kubeadm/)
- [Calico 文档](https://docs.tigera.io/calico/latest/about/)
- [pkgs.k8s.io 包仓库](https://kubernetes.io/blog/2023/08/15/pkgs-k8s-io-introduction/)

## 版本历史

| 日期 | 版本 | 说明 |
|------|------|------|
| 2025-03-23 | 1.0 | 初始版本，支持 Kubernetes 1.35 + Ubuntu 24.04 |
