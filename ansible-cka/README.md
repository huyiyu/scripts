# Kubernetes CKA 环境部署

基于 Ansible 的 Kubernetes 离线部署方案，支持三个独立的单节点集群用于 CKA 考试练习。

## 架构概览

```
┌─────────────────┬─────────────────┬─────────────────┐
│    Cluster 1    │    Cluster 2    │    Cluster 3    │
│  (11.0.1.100)   │  (11.0.1.101)   │  (11.0.1.102)   │
│                 │                 │                 │
│  • Q01-Q14      │  • Q13          │  • Q15-Q16      │
│  (14 道题目)     │  (Calico)       │  (故障排查)      │
└─────────────────┴─────────────────┴─────────────────┘
```

## 快速开始

### 1. 准备离线资源（联网环境）

```bash
cd offline/scripts
chmod +x prepare-offline.sh
./prepare-offline.sh
```

### 2. 配置目标节点

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
```

### 3. 配置 SSH 免密登录

```bash
ssh-keygen -t rsa -b 4096
for host in 11.0.1.100 11.0.1.101 11.0.1.102; do
    ssh-copy-id root@$host
done
```

### 4. 执行部署

```bash
cd ansible-k8s
ansible-playbook -i inventory/hosts.ini playbooks/site.yml
```

部署完成后会显示 SSH 私钥和连接配置，保存到 `~/.ssh/` 即可连接各题目用户。

## 验证部署

```bash
# 检查节点状态
kubectl get nodes -o wide

# 检查系统 Pod
kubectl get pods -n kube-system

# 检查 Calico 状态
kubectl get pods -n calico-system
```

## 文档索引

| 文档 | 说明 |
|------|------|
| [docs/questions.md](docs/questions.md) | 16 道 CKA 题目详细说明 |
| [docs/troubleshooting.md](docs/troubleshooting.md) | 故障排查指南 |
| [docs/variables.md](docs/variables.md) | 配置变量说明 |
| [docs/reset.md](docs/reset.md) | 集群重置方法 |

## 项目结构

```
.
├── ansible-k8s/           # Ansible 部署配置
│   ├── inventory/         # 主机清单和变量
│   ├── playbooks/         # Playbook
│   └── roles/             # 部署角色
│       ├── kubernetes/    # K8s 集群部署
│       └── question/      # CKA 题目部署
├── offline/               # 离线资源
│   ├── packages/          # 软件包
│   ├── images/            # 容器镜像
│   └── scripts/           # 资源准备脚本
└── docs/                  # 文档
```

## 版本信息

| 组件 | 版本 |
|------|------|
| Kubernetes | 1.34.3 |
| Containerd | 2.2.1 |
| Calico | v3.30.6 |

## 参考

- [Kubernetes 官方文档](https://kubernetes.io/docs/)
- [CKA 考试大纲](https://github.com/cncf/curriculum)
