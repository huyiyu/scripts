# 使用 Ansible 搭建 Kubernetes 1.35 单节点集群

本文档介绍如何使用 Ansible 在 Ubuntu 24.04 上自动化部署三个独立的 Kubernetes 1.35 单节点集群。

参考文档：https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/

## 架构概述

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                       三个独立 Kubernetes 1.35 集群                           │
├─────────────────────────────┬─────────────────────────────┬─────────────────┤
│       Cluster 1             │        Cluster 2            │    Cluster 3    │
│   (k8s-node01, 192.168.1.10)│  (k8s-node02, 192.168.1.11) │(k8s-node03,192.168.1.12)│
├─────────────────────────────┼─────────────────────────────┼─────────────────┤
│ • kube-apiserver            │ • kube-apiserver            │ • kube-apiserver│
│ • kube-controller-manager   │ • kube-controller-manager   │ • kube-controller-manager│
│ • kube-scheduler            │ • kube-scheduler            │ • kube-scheduler│
│ • etcd                      │ • etcd                      │ • etcd          │
│ • kubelet                   │ • kubelet                   │ • kubelet       │
│ • containerd (CRI)          │ • containerd (CRI)          │ • containerd (CRI)│
│ • Calico CNI                │ • Calico CNI                │ • Calico CNI    │
│ • 可调度工作负载             │ • 可调度工作负载             │ • 可调度工作负载 │
└─────────────────────────────┴─────────────────────────────┴─────────────────┘
```

## 环境要求

### 服务器配置

| 集群 | 节点 | IP地址 | CPU | 内存 | 磁盘 | OS |
|------|------|--------|-----|------|------|-----|
| Cluster 1 | k8s-node01 | 192.168.1.10 | 2核+ | 4GB+ | 20GB+ | Ubuntu 24.04 LTS |
| Cluster 2 | k8s-node02 | 192.168.1.11 | 2核+ | 4GB+ | 20GB+ | Ubuntu 24.04 LTS |
| Cluster 3 | k8s-node03 | 192.168.1.12 | 2核+ | 4GB+ | 20GB+ | Ubuntu 24.04 LTS |

### 网络要求

| 协议 | 端口 | 用途 |
|------|------|------|
| TCP | 6443 | Kubernetes API Server |
| TCP | 10250 | Kubelet API |
| TCP | 2379-2380 | etcd server client API |
| TCP | 10259 | kube-scheduler |
| TCP | 10257 | kube-controller-manager |
| UDP | 8472 | VXLAN (Calico) |
| TCP | 179 | BGP (Calico) |

## 项目结构

```
k8s-ansible/
├── ansible.cfg
├── inventory/
│   └── hosts.ini
├── group_vars/
│   └── all.yml
├── roles/
│   └── kubernetes/
│       ├── tasks/
│       │   ├── main.yml
│       │   ├── 01-system-prep.yml
│       │   ├── 02-containerd.yml
│       │   ├── 03-k8s-packages.yml
│       │   └── 04-cluster-init.yml
│       ├── templates/
│       │   └── kubeadm-config.yaml.j2
│       └── handlers/
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
[cluster1]
k8s-node01 ansible_host=192.168.1.10

[cluster2]
k8s-node02 ansible_host=192.168.1.11

[cluster3]
k8s-node03 ansible_host=192.168.1.12

[all:children]
cluster1
cluster2
cluster3

[cluster1:vars]
cni_enabled=true

[cluster2:vars]
cni_enabled=false

[cluster3:vars]
cni_enabled=true

[all:vars]
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
pod_network_cidr: "10.244.0.0/16"
service_cidr: "10.96.0.0/12"

# CNI 配置
cni_enabled: true
cni_provider: "calico"
calico_version: "v3.28.0"

# Containerd 版本
containerd_version: "2.0.0"
runc_version: "1.1.12"
cni_plugins_version: "1.5.1"

# 网络接口（根据实际情况修改）
network_interface: "eth0"
```

### 4. Kubernetes 角色

**`roles/kubernetes/tasks/main.yml`**

```yaml
---
# Kubernetes 单节点集群部署主入口

- name: 导入系统准备任务
  import_tasks: 01-system-prep.yml

- name: 导入 Containerd 安装任务
  import_tasks: 02-containerd.yml

- name: 导入 Kubernetes 包安装任务
  import_tasks: 03-k8s-packages.yml

- name: 导入集群初始化任务
  import_tasks: 04-cluster-init.yml
```

**`roles/kubernetes/tasks/01-system-prep.yml`**

```yaml
---
# 系统基础配置

- name: 更新 apt 缓存
  apt:
    update_cache: yes
    cache_valid_time: 3600

- name: 安装必要的基础软件包
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
- name: 加载 Kubernetes 所需的内核模块
  modprobe:
    name: "{{ item }}"
    state: present
  loop:
    - overlay
    - br_netfilter

- name: 确保内核模块持久化配置
  lineinfile:
    path: /etc/modules-load.d/k8s.conf
    line: "{{ item }}"
    create: yes
  loop:
    - overlay
    - br_netfilter

# 配置 sysctl 参数
- name: 配置 Kubernetes 所需的 sysctl 参数
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
- name: 立即禁用 swap
  command: swapoff -a
  changed_when: true

- name: 从 /etc/fstab 中移除 swap 配置
  replace:
    path: /etc/fstab
    regexp: '^([^#].*?\s+swap\s+.*)$'
    replace: '# \1'

# 配置时区
- name: 设置时区为 Asia/Shanghai
  timezone:
    name: Asia/Shanghai

# 禁用防火墙（生产环境请谨慎）
- name: 禁用 UFW 防火墙
  systemd:
    name: ufw
    state: stopped
    enabled: no
  ignore_errors: yes

# 配置 hosts 文件
- name: 添加 hosts 条目
  lineinfile:
    path: /etc/hosts
    line: "{{ hostvars[item]['ansible_host'] }} {{ item }}"
    state: present
  loop: "{{ groups['all'] }}"
```

**`roles/kubernetes/tasks/02-containerd.yml`**

```yaml
---
# 安装和配置 Containerd 容器运行时

- name: 安装 Containerd 依赖包
  apt:
    name:
      - curl
      - gnupg
    state: present

- name: 创建 Containerd 所需目录
  file:
    path: "{{ item }}"
    state: directory
    mode: '0755'
  loop:
    - /etc/containerd
    - /usr/local/lib/systemd/system
    - /opt/cni/bin

- name: 下载 Containerd 安装包
  get_url:
    url: "https://github.com/containerd/containerd/releases/download/v{{ containerd_version }}/containerd-{{ containerd_version }}-linux-amd64.tar.gz"
    dest: "/tmp/containerd-{{ containerd_version }}-linux-amd64.tar.gz"
    mode: '0644'
  register: containerd_download

- name: 解压 Containerd 安装包
  unarchive:
    src: "/tmp/containerd-{{ containerd_version }}-linux-amd64.tar.gz"
    dest: /usr/local
    remote_src: yes
  when: containerd_download.changed

- name: 下载 runc 二进制文件
  get_url:
    url: "https://github.com/opencontainers/runc/releases/download/v{{ runc_version }}/runc.amd64"
    dest: /tmp/runc.amd64
    mode: '0644'

- name: 安装 runc
  copy:
    src: /tmp/runc.amd64
    dest: /usr/local/sbin/runc
    mode: '0755'
    remote_src: yes

- name: 下载 CNI 插件
  get_url:
    url: "https://github.com/containernetworking/plugins/releases/download/v{{ cni_plugins_version }}/cni-plugins-linux-amd64-v{{ cni_plugins_version }}.tgz"
    dest: "/tmp/cni-plugins-linux-amd64-v{{ cni_plugins_version }}.tgz"
    mode: '0644'

- name: 解压 CNI 插件
  unarchive:
    src: "/tmp/cni-plugins-linux-amd64-v{{ cni_plugins_version }}.tgz"
    dest: /opt/cni/bin
    remote_src: yes

- name: 下载 Containerd systemd 服务文件
  get_url:
    url: https://raw.githubusercontent.com/containerd/containerd/main/containerd.service
    dest: /usr/local/lib/systemd/system/containerd.service
    mode: '0644'

- name: 生成 Containerd 默认配置
  shell: containerd config default > /etc/containerd/config.toml
  args:
    creates: /etc/containerd/config.toml

- name: 配置 Containerd 使用 systemd cgroup driver
  lineinfile:
    path: /etc/containerd/config.toml
    regexp: 'SystemdCgroup = false'
    line: '            SystemdCgroup = true'
    state: present

- name: 配置 Containerd sandbox 镜像
  lineinfile:
    path: /etc/containerd/config.toml
    regexp: 'sandbox_image = "registry.k8s.io/pause:.*"'
    line: '    sandbox_image = "registry.k8s.io/pause:3.10"'
    state: present

- name: 重新加载 systemd 配置
  systemd:
    daemon_reload: yes

- name: 启动并启用 Containerd 服务
  systemd:
    name: containerd
    state: started
    enabled: yes

- name: 验证 Containerd 运行状态
  shell: systemctl is-active containerd
  register: containerd_status
  changed_when: false

- name: 检查 Containerd 是否正常运行
  fail:
    msg: "Containerd 未正常运行！"
  when: containerd_status.stdout != "active"
```

**`roles/kubernetes/tasks/03-k8s-packages.yml`**

```yaml
---
# 安装 Kubernetes 组件（kubelet, kubeadm, kubectl）

- name: 创建 apt 密钥环目录
  file:
    path: /etc/apt/keyrings
    state: directory
    mode: '0755'

- name: 下载 Kubernetes GPG 密钥
  shell: |
    curl -fsSL https://pkgs.k8s.io/core:/stable:/v{{ k8s_version }}/deb/Release.key | \
    gpg --dearmor -o /etc/apt/keyrings/kubernetes-apt-keyring.gpg
  args:
    creates: /etc/apt/keyrings/kubernetes-apt-keyring.gpg

- name: 添加 Kubernetes apt 仓库
  apt_repository:
    repo: "deb [signed-by=/etc/apt/keyrings/kubernetes-apt-keyring.gpg] https://pkgs.k8s.io/core:/stable:/v{{ k8s_version }}/deb/ /"
    state: present
    filename: kubernetes
    update_cache: yes

- name: 安装 kubelet, kubeadm 和 kubectl
  apt:
    name:
      - kubelet
      - kubeadm
      - kubectl
    state: present
    update_cache: yes

- name: 固定 kubelet, kubeadm 和 kubectl 版本（防止自动升级）
  dpkg_selections:
    name: "{{ item }}"
    selection: hold
  loop:
    - kubelet
    - kubeadm
    - kubectl

- name: 启用 kubelet 服务
  systemd:
    name: kubelet
    enabled: yes

- name: 创建 kubelet 环境变量文件
  file:
    path: /etc/default/kubelet
    state: touch
    mode: '0644'

- name: 配置 kubelet 使用 systemd cgroup driver
  lineinfile:
    path: /etc/default/kubelet
    line: 'KUBELET_EXTRA_ARGS="--cgroup-driver=systemd"'
    create: yes
    state: present
```

**`roles/kubernetes/tasks/04-cluster-init.yml`**

```yaml
---
# 初始化 Kubernetes 单节点集群

- name: 检查 Kubernetes 是否已初始化
  stat:
    path: /etc/kubernetes/admin.conf
  register: k8s_initialized

- name: 如已初始化则重置 Kubernetes（可选）
  shell: kubeadm reset -f
  when: 
    - k8s_initialized.stat.exists
    - force_reset | default(false) | bool

- name: 创建 kubeadm 配置目录
  file:
    path: /root/kubeadm
    state: directory
    mode: '0755'

- name: 复制 kubeadm 配置文件
  template:
    src: kubeadm-config.yaml.j2
    dest: /root/kubeadm/kubeadm-config.yaml
    mode: '0644'

- name: 拉取 Kubernetes 镜像
  shell: kubeadm config images pull
  when: not k8s_initialized.stat.exists or (force_reset | default(false) | bool)

- name: 初始化 Kubernetes 集群
  shell: |
    kubeadm init --config=/root/kubeadm/kubeadm-config.yaml
  when: not k8s_initialized.stat.exists or (force_reset | default(false) | bool)
  register: kubeadm_init

# 配置 kubectl
- name: 为 root 用户创建 .kube 目录
  file:
    path: /root/.kube
    state: directory
    mode: '0755'

- name: 复制 admin.conf 到 root 用户的 kube 配置
  copy:
    src: /etc/kubernetes/admin.conf
    dest: /root/.kube/config
    remote_src: yes
    mode: '0644'

- name: 为 ubuntu 用户创建 .kube 目录
  file:
    path: /home/ubuntu/.kube
    state: directory
    mode: '0755'
    owner: ubuntu
    group: ubuntu

- name: 复制 admin.conf 到 ubuntu 用户的 kube 配置
  copy:
    src: /etc/kubernetes/admin.conf
    dest: /home/ubuntu/.kube/config
    remote_src: yes
    mode: '0644'
    owner: ubuntu
    group: ubuntu

# 安装 CNI (Calico) - 可选项
- name: 下载 Calico 部署清单
  get_url:
    url: "https://raw.githubusercontent.com/projectcalico/calico/{{ calico_version }}/manifests/calico.yaml"
    dest: /root/calico.yaml
    mode: '0644'
  when: cni_enabled | default(true) | bool

- name: 部署 Calico CNI
  shell: kubectl apply -f /root/calico.yaml
  environment:
    KUBECONFIG: /etc/kubernetes/admin.conf
  when: cni_enabled | default(true) | bool

# 等待 Calico 就绪
- name: 等待 Calico Pod 就绪
  shell: |
    kubectl wait --for=condition=ready pod \
      -l k8s-app=calico-node \
      -n kube-system \
      --timeout=300s
  environment:
    KUBECONFIG: /etc/kubernetes/admin.conf
  ignore_errors: yes
  when: cni_enabled | default(true) | bool

# 移除控制平面污点，允许在单节点上调度 Pod
- name: 移除控制平面污点以允许调度工作负载
  shell: |
    kubectl taint nodes --all node-role.kubernetes.io/control-plane- 2>/dev/null || true
  environment:
    KUBECONFIG: /etc/kubernetes/admin.conf
  ignore_errors: yes

# 验证集群状态
- name: 获取集群信息
  shell: kubectl cluster-info
  environment:
    KUBECONFIG: /etc/kubernetes/admin.conf
  register: cluster_info
  changed_when: false

- name: 显示集群信息
  debug:
    var: cluster_info.stdout_lines

- name: 获取节点状态
  shell: kubectl get nodes -o wide
  environment:
    KUBECONFIG: /etc/kubernetes/admin.conf
  register: node_status
  changed_when: false

- name: 显示节点状态
  debug:
    var: node_status.stdout_lines
```

**`roles/kubernetes/templates/kubeadm-config.yaml.j2`**

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

**`roles/kubernetes/handlers/main.yml`**

```yaml
---
- name: 重新加载 sysctl 配置
  command: sysctl --system
```

### 5. 主 Playbook

**`playbooks/site.yml`**

```yaml
---
# 部署三个独立的 Kubernetes 1.35 单节点集群

- name: 部署 Kubernetes 单节点集群
  hosts: all
  become: yes
  roles:
    - kubernetes
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
mkdir -p inventory group_vars roles/kubernetes/{tasks,templates,handlers} playbooks
```

### 2. 配置 SSH 免密登录

```bash
# 生成 SSH 密钥（如未生成）
ssh-keygen -t rsa -b 4096

# 复制公钥到所有节点
for host in 192.168.1.10 192.168.1.11 192.168.1.12; do
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

在每个节点上执行：

```bash
# 检查节点状态
kubectl get nodes -o wide

# 预期输出（单节点，控制平面可调度）：
# NAME         STATUS   ROLES           AGE   VERSION
# k8s-node01   Ready    control-plane   5m    v1.35.x

# 检查系统 Pod
kubectl get pods -n kube-system

# 检查 Calico Pod
kubectl get pods -n kube-system -l k8s-app=calico-node

# 查看集群信息
kubectl cluster-info

# 查看版本
kubectl version

# 测试部署应用
kubectl create deployment nginx --image=nginx
kubectl get pods
```

## 重置集群

如需重置某个集群节点：

```bash
# 在目标节点上执行
sudo kubeadm reset -f

# 清理 iptables
sudo iptables -F && sudo iptables -t nat -F && sudo iptables -t mangle -F && sudo iptables -X

# 清理 CNI 配置
sudo rm -rf /etc/cni/net.d

# 然后重新运行 playbook
ansible-playbook playbooks/site.yml -l k8s-node01
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
| `Pod 无法调度` | 确认控制平面污点已移除 |

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
| 2025-03-23 | 2.0 | 重构为单角色、单节点模式，支持三个独立集群 |
| 2025-03-23 | 1.0 | 初始版本，支持 Kubernetes 1.35 + Ubuntu 24.04 |
