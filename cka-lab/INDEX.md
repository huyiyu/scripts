# CKA 2026 考试环境 - 文件索引

> 基于 cka202602.md 的 16 道真题

## 开始使用

### 第一步：阅读指南
1. **[SETUP.md](SETUP.md)** - 完整的搭建和使用指南
2. **[QUICKSTART.md](QUICKSTART.md)** - 快速参考卡片

### 第二步：执行部署

```bash
cd /c/work/cka-lab

# 1. 配置 SSH 免密登录
./scripts/setup-ssh.sh

# 2. 部署完整环境
./deploy.sh

# 3. 连接到集群
ssh ubuntu@192.168.56.10
kubectl get nodes
```

## 文件说明

### 核心脚本 (Bash)

| 文件 | 说明 |
|-----|------|
| `deploy.sh` | 主部署脚本 |
| `scripts/setup-ssh.sh` | SSH 配置脚本 |
| `scripts/cka-exam/check_cka.sh` | **自动评分脚本** (16道题) |
| `scripts/cka-exam/etcd-set.sh` | **ETCD故障环境初始化** (第15题) |
| `scripts/cka-exam/10-network.yaml` | **NetworkPolicy资源** (第10题) |

### 文档

| 文件 | 说明 |
|-----|------|
| `SETUP.md` | 完整搭建指南 |
| `QUICKSTART.md` | 快速参考 |
| `README.md` | 项目概述 |
| `INDEX.md` | 本文件 |
| `docs/vmware-setup.md` | VMware 设置 |

### Ansible 配置

```
ansible/
├── ansible.cfg              # Ansible 配置
├── inventory/
│   └── hosts.ini            # 主机清单 (修改 IP)
├── playbooks/
│   ├── site.yml             # 主 playbook
│   └── deploy-scenarios.yml # 仅部署题目
└── roles/
    ├── common/              # 基础环境
    ├── containerd/          # 容器运行时
    ├── kubernetes/          # K8s 安装
    ├── controlplane/        # Control Plane
    ├── cni/                 # Calico 网络
    ├── addons/              # 插件
    ├── worker/              # Worker 节点
    └── cka_scenarios/       # 16道题目场景
        └── tasks/
            └── main.yml     # cka202602.md 题目定义
```

## cka202602.md 题目覆盖

本环境严格按照 cka202602.md 创建 16 道题目：

### 已创建的资源

| 题号 | 题目 | 创建的命名空间/资源 |
|-----|------|-------------------|
| Q1 | HPA | autoscale namespace, apache-server Deployment |
| Q2 | Ingress | sound-repeater namespace, echoserver Deployment + Service |
| Q3 | sidecar | default namespace, synergy-leverager Deployment |
| Q4 | StorageClass | ran-local-path StorageClass |
| Q5 | Service | spline-reticulator namespace, front-end Deployment |
| Q6 | PriorityClass | priority namespace, max-user-priority PC, busybox-logger Deployment |
| Q7 | Helm | Helm 已安装，argocd namespace 待创建 |
| Q8 | PVC | mariadb namespace, mariadb-pv PV |
| Q9 | Gateway | default namespace, web Deployment + Service + Ingress + web-cert Secret |
| Q10 | NetworkPolicy | backend/frontend namespaces, backend/frontend Deployments |
| Q11 | CRD | cert-manager 已安装 |
| Q12 | ConfigMap | nginx-static namespace, nginx-config ConfigMap, nginx-static Deployment |
| Q13 | Calico | Calico CNI 已安装 |
| Q14 | Resources | relative-fawn namespace, wordpress Deployment |
| Q15 | etcd | 集群健康，可手动破坏配置练习修复 |
| Q16 | cri-dockerd | 基础环境已配置 |

## 快速命令参考

### 部署命令

```bash
./deploy.sh              # 完整部署
./deploy.sh -a scenarios # 仅部署题目
./deploy.sh -a reset     # 重置题目
./deploy.sh -a status    # 查看状态
./deploy.sh -h           # 帮助
```

### Ansible 命令

```bash
cd ansible

# 测试连接
ansible all -m ping

# 执行特定标签
ansible-playbook -i inventory/hosts.ini playbooks/site.yml --tags "cka_scenarios"

# 仅部署题目场景
ansible-playbook -i inventory/hosts.ini playbooks/deploy-scenarios.yml
```

### kubectl 命令

```bash
# 设置别名
alias k=kubectl
complete -F __start_kubectl k

# 查看题目资源
kubectl get ns
kubectl get deploy -A
kubectl get svc -A
kubectl get ingress -A
kubectl get sc

# 生成模板
k run test --image=nginx --dry-run=client -o yaml
k create deploy test --image=nginx --replicas=3 --dry-run=client -o yaml

# 故障排查
k describe pod <name>
k logs <name>
k explain pod.spec.containers
```

## 网络架构

```
┌─────────────────────────────────────────┐
│            VMware Network               │
│         (192.168.56.0/24)               │
│                                         │
│  ┌─────────────────┐                   │
│  │  controlplane   │  192.168.56.10    │
│  │   (Master)      │                   │
│  │  API Server     │                   │
│  │  Scheduler      │                   │
│  │  Controller     │                   │
│  │  ETCD           │                   │
│  └────────┬────────┘                   │
│           │                             │
│     ┌─────┴─────┐                       │
│     │           │                       │
│  ┌──┴──┐    ┌──┴──┐                    │
│  │node01│    │node02│  192.168.56.11/12 │
│  │      │    │      │                   │
│  │Worker│    │Worker│                   │
│  │ Pod  │    │ Pod  │                   │
│  └──────┘    └──────┘                   │
│                                         │
└─────────────────────────────────────────┘
```

## 自定义配置

### 修改 IP 地址

编辑 `ansible/inventory/hosts.ini`：

```ini
[controlplane]
192.168.56.10 ansible_hostname=controlplane

[workers]
192.168.56.11 ansible_hostname=node01
192.168.56.12 ansible_hostname=node02
```

## 故障排除

| 问题 | 解决方案 |
|-----|---------|
| SSH 连接失败 | 检查 IP 地址和防火墙设置 |
| 节点无法加入 | `kubeadm token create --print-join-command` |
| Calico 无法启动 | 检查 Pod CIDR 配置 |

## 参考

- cka202602.md: https://huyiyu.github.io/posts/kubernetes/cka202602/
- CKA 刷题心得: https://huyiyu.github.io/posts/kubernetes/killersh/

---

祝考试顺利! 🚀
