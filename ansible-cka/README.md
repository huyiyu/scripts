# Kubernetes 离线部署

基于 Ansible 的 Kubernetes 离线部署方案，支持三个独立的单节点集群。

## 架构概述

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                       三个独立 Kubernetes 1.34.3 集群                           │
├─────────────────────────────┬─────────────────────────────┬─────────────────┤
│       Cluster 1             │        Cluster 2            │    Cluster 3    │
│   (k8s-node01, 11.0.1.100)│  (k8s-node02, 11.0.1.101) │(k8s-node03,11.0.1.102)│
├─────────────────────────────┼─────────────────────────────┼─────────────────┤
│ • kube-apiserver            │ • kube-apiserver            │ • kube-apiserver│
│ • kube-controller-manager   │ • kube-controller-manager   │ • kube-controller-manager│
│ • kube-scheduler            │ • kube-scheduler            │ • kube-scheduler│
│ • etcd                      │ • etcd                      │ • etcd          │
│ • kubelet                   │ • kubelet                   │ • kubelet       │
│ • containerd (CRI)          │ • containerd (CRI)          │ • containerd (CRI)│
│ • Calico CNI (Operator)     │ • Calico CNI (Operator)     │ • Calico CNI (Operator)│
│ • 可调度工作负载             │ • 可调度工作负载             │ • 可调度工作负载 │
└─────────────────────────────┴─────────────────────────────┴─────────────────┘
```

## 项目结构

```
.
├── ansible-k8s/                 # Ansible 部署配置
│   ├── ansible.cfg
│   ├── group_vars/
│   │   └── all.yml             # 版本定义和配置变量
│   ├── inventory/
│   │   └── hosts.ini           # 主机清单
│   ├── playbooks/
│   │   └── site.yml            # 主 Playbook
│   └── roles/kubernetes/       # 部署角色
│       ├── tasks/              # 任务文件
│       │   ├── main.yml
│       │   ├── 01-system-prep.yml      # 系统准备
│       │   ├── 02-containerd.yml       # Containerd 离线安装
│       │   ├── 03-k8s-packages.yml     # K8s 二进制文件安装
│       │   ├── 04-offline-images.yml   # 离线镜像导入
│       │   └── 05-cluster-init.yml     # 集群初始化 + Calico Operator
│       ├── templates/
│       │   ├── kubeadm-config.yaml.j2
│       │   └── kubelet.service.j2
│       └── handlers/
│           └── main.yml
├── offline/                     # 离线资源
│   ├── packages/              # 软件包（自动下载）
│   │   ├── containerd/       # Containerd 组件
│   │   ├── cni/              # CNI 插件
│   │   ├── k8s/              # Kubernetes 二进制文件
│   │   └── calico/           # Calico Operator 文件
│   ├── images/                # 容器镜像
│   │   ├── k8s-images-v1.34.3.tar
│   │   └── calico-images-v3.30.6.tar
│   └── scripts/
│       └── prepare-offline.sh # 资源准备脚本
└── docs/                       # 文档
```

## 集群节点

| 集群 | 节点 | IP地址 |
|------|------|--------|
| Cluster 1 | k8s-node01 | 11.0.1.100 |
| Cluster 2 | k8s-node02 | 11.0.1.101 |
| Cluster 3 | k8s-node03 | 11.0.1.102 |

## 版本信息

所有版本统一在 `ansible-k8s/group_vars/all.yml` 中配置：

| 组件 | 版本 | 变量名 |
|------|------|--------|
| Kubernetes | 1.34.3 | `k8s_version` |
| Containerd | 2.2.1 | `containerd_version` |
| Runc | 1.3.4 | `runc_version` |
| CNI Plugins | 1.8.0 | `cni_plugins_version` |
| Calico | v3.30.6 | `calico_version` |
| Calico Operator | v1.38.11 | `calico_operator_version` |
| Pause | 3.10.1 | `pause_version` |
| etcd | 3.5.26 | `etcd_version` |
| CoreDNS | v1.12.1 | `coredns_version` |

## 环境要求

### 服务器配置

| 集群 | 节点 | IP地址 | CPU | 内存 | 磁盘 | OS |
|------|------|--------|-----|------|------|-----|
| Cluster 1 | k8s-node01 | 11.0.1.100 | 2核+ | 4GB+ | 20GB+ | Ubuntu 24.04 LTS |
| Cluster 2 | k8s-node02 | 11.0.1.101 | 2核+ | 4GB+ | 20GB+ | Ubuntu 24.04 LTS |
| Cluster 3 | k8s-node03 | 11.0.1.102 | 2核+ | 4GB+ | 20GB+ | Ubuntu 24.04 LTS |

### 准备机要求

- Ubuntu 24.04 + Docker（用于下载镜像和准备离线资源）

## 快速开始

### 阶段一：在联网环境中准备资源

#### 1. 运行资源准备脚本

```bash
cd offline/scripts
chmod +x prepare-offline.sh
./prepare-offline.sh
```

此脚本将下载：
- Containerd 二进制包 (containerd, runc, service)
- CNI 插件包
- Calico Operator YAML 文件
- Kubernetes 二进制文件 (kubelet, kubeadm, kubectl)
- 工具 (crictl, helm)
- 基础系统依赖 deb 包
- 导出容器镜像（需要 Docker）

### 阶段二：在离线环境中部署

#### 1. 配置目标节点

编辑 `ansible-k8s/inventory/hosts.ini`：

```ini
[cluster1]
k8s-node01 ansible_host=11.0.1.100

[cluster2]
k8s-node02 ansible_host=11.0.1.101

[cluster3]
k8s-node03 ansible_host=11.0.1.102

[all:children]
cluster1
cluster2
cluster3

[all:vars]
ansible_user=root
ansible_ssh_private_key_file=~/.ssh/id_rsa
ansible_python_interpreter=/usr/bin/python3
```

#### 2. 配置 SSH 免密登录（root 用户）

**方案 A：如果 root 可以直接登录**
```bash
# 生成 SSH 密钥
ssh-keygen -t rsa -b 4096

# 复制公钥到所有节点
for host in 11.0.1.100 11.0.1.101 11.0.1.102; do
    ssh-copy-id root@$host
done
```

**方案 B：如果 root 禁止密码登录（需先通过普通用户配置）**
```bash
# 1. 获取控制节点的 root 公钥
cat ~/.ssh/id_rsa.pub

# 2. 在每个目标节点上，以 root 用户执行（通过控制台或其他方式）
mkdir -p /root/.ssh
chmod 700 /root/.ssh
echo "你的公钥内容" >> /root/.ssh/authorized_keys
chmod 600 /root/.ssh/authorized_keys

# 或者通过已有 sudo 权限的普通用户配置
# ssh-copy-id -o StrictHostKeyChecking=no user@host
# ssh user@host "sudo mkdir -p /root/.ssh && sudo chmod 700 /root/.ssh && sudo tee /root/.ssh/authorized_keys" < ~/.ssh/id_rsa.pub
```

#### 3. 测试连通性

```bash
cd ansible-k8s
ansible all -m ping
```

#### 4. 执行部署

```bash
cd ansible-k8s
ansible-playbook -i inventory/hosts.ini playbooks/site.yml
```

部署流程会自动执行：
1. 系统准备（内核模块、sysctl 等）
2. **同步离线资源**（从控制节点 rsync 到目标节点）
3. 安装 Containerd
4. 安装 Kubernetes 二进制包
5. 导入离线镜像
6. 初始化集群并安装 Calico

## 详细配置说明

### 1. 版本变量配置（group_vars/all.yml）

```yaml
---
# Kubernetes 版本配置
k8s_version: "1.34.3"
kubernetes_version_full: "v{{ k8s_version }}"

# 容器运行时版本配置
containerd_version: "2.2.1"
runc_version: "1.3.4"
cni_plugins_version: "1.8.0"
pause_version: "3.10.1"

# CNI 网络插件版本配置
cni_provider: "calico"
calico_version: "v3.30.6"
calico_operator_version: "v1.38.11"

# 核心组件版本
etcd_version: "3.5.26"
coredns_version: "v1.12.1"

# 离线部署配置
offline_packages_path: "/opt/offline/packages"
offline_images_path: "/opt/offline/images"
```

### 2. Kubernetes 组件安装方式

#### 二进制文件安装

1. 从 `/opt/offline/packages/k8s/` 复制二进制文件到 `/usr/local/bin/`
2. 创建软链接到 `/usr/bin/`
3. 创建 kubelet systemd 服务
4. 配置 kubelet 使用 systemd cgroup driver

### 3. Calico Operator 部署

部署流程：

```bash
# 1. 部署 Tigera Operator
kubectl create -f /opt/offline/packages/calico/tigera-operator.yaml

# 2. 等待 Operator Pod 就绪
kubectl wait --for=condition=ready pod -l name=tigera-operator -n tigera-operator

# 3. 创建 Calico Installation CR
kubectl create -f /opt/offline/packages/calico/custom-resources.yaml

# 4. 等待 Calico 安装完成
kubectl wait --for=condition=ready pod -l k8s-app=calico-node -n calico-system
```

### 4. 离线镜像导入

使用 `ctr` 命令导入镜像：

```bash
ctr -n k8s.io images import /opt/offline/images/k8s-images-v1.34.3.tar
ctr -n k8s.io images import /opt/offline/images/calico-images-v3.30.6.tar
```

## 验证部署

在每个节点上执行：

```bash
# 检查节点状态
kubectl get nodes -o wide

# 检查系统 Pod
kubectl get pods -n kube-system

# 检查 Calico Pod
kubectl get pods -n calico-system

# 查看集群信息
kubectl cluster-info

# 查看 Calico 状态
kubectl get tigerastatus default
```

## 故障排查

### 检查离线资源

```bash
# 检查包是否存在
ls -la /opt/offline/packages/containerd/
ls -la /opt/offline/packages/k8s/
ls -la /opt/offline/packages/calico/
ls -la /opt/offline/images/

# 检查镜像是否导入
sudo ctr -n k8s.io images list | grep kube
sudo ctr -n k8s.io images list | grep calico

# 检查二进制文件
which kubelet kubeadm kubectl
kubelet --version
```

### 查看日志

```bash
# 查看 kubelet 日志
sudo journalctl -u kubelet -f

# 查看 containerd 日志
sudo journalctl -u containerd -f

# 查看 Calico Operator 日志
kubectl logs -n tigera-operator -l name=tigera-operator

# 查看 Calico 组件日志
kubectl logs -n calico-system -l k8s-app=calico-node
```

## 重置集群

如需重置某个集群节点：

```bash
# 在目标节点上执行
sudo kubeadm reset -f
sudo iptables -F && sudo iptables -t nat -F && sudo iptables -t mangle -F && sudo iptables -X
sudo rm -rf /etc/cni/net.d /var/lib/kubelet

# 重新运行 playbook
ansible-playbook playbooks/site.yml -l k8s-node01
```

## 修改版本

1. 编辑 `ansible-k8s/group_vars/all.yml` 修改版本号
2. 重新运行 `./prepare-offline.sh` 准备资源
3. 重新复制并部署

## 参考文档

- [Kubernetes 官方文档](https://kubernetes.io/docs/)
- [kubeadm 安装指南](https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/install-kubeadm/)
- [Containerd 文档](https://containerd.io/docs/)
- [Calico Operator 文档](https://docs.tigera.io/calico/latest/getting-started/kubernetes/quickstart)

## 版本历史

| 日期 | 版本 | 说明 |
|------|------|------|
| 2025-03-23 | 3.0 | 使用 Calico Operator 替代 manifest，二进制文件部署 K8s |
| 2025-03-23 | 2.0 | 重构为单角色、单节点模式 |
| 2025-03-23 | 1.0 | 初始版本 |
