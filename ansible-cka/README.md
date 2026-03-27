# Kubernetes CKA 考试环境部署

基于 Ansible 的 Kubernetes 离线部署方案，支持三个独立的单节点集群用于 CKA 考试练习。

## 架构概览

```
┌─────────────────┬─────────────────┬─────────────────┐
│    Cluster 1    │    Cluster 2    │    Cluster 3    │
│  (11.0.1.100)   │  (11.0.1.101)   │  (11.0.1.102)   │
│                 │                 │                 │
│  • Q01-Q14      │  • Q13          │  • Q15-Q16      │
│  (14 道题目)     │  (Calico)       │  (故障排查+评分) │
└─────────────────┴─────────────────┴─────────────────┘
```

### 题目分布

| 题目 | 集群 | 命名空间 | 考生用户 | 说明 |
|------|------|----------|----------|------|
| Q01 | cluster1 | autoscale | cka000001 | HPA autoscale |
| Q02 | cluster1 | sound-repeater | cka000002 | Ingress |
| Q03 | cluster1 | default | cka000003 | Sidecar |
| Q04 | cluster1 | one | cka000004 | etcd 备份与还原 |
| Q05 | cluster1 | delta | cka000005 | Deployment + Service |
| Q06 | cluster1 | priority | cka000006 | PriorityClass |
| Q07 | cluster1 | argocd | cka000007 | Helm ArgoCD |
| Q08 | cluster1 | mariadb | cka000008 | PVC |
| Q09 | cluster1 | default | cka000009 | Gateway API |
| Q10 | cluster1 | frontend/backend | cka000010 | NetworkPolicy |
| Q11 | cluster1 | cert-manager | cka000011 | cert-manager CRD |
| Q12 | cluster1 | nginx-static | cka000012 | HTTPS + ConfigMap |
| Q13 | cluster2 | calico-netpol | cka000013 | Calico NetworkPolicy |
| Q14 | cluster1 | relative-fawn | cka000014 | Resource Limits |
| Q15 | cluster3 | default | cka000015 | etcd 故障排查 |
| Q16 | cluster3 | default | cka000016 | cri-dockerd + 评分系统 |

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

## 评分系统

部署完成后，在 cluster3 (cka000016) 上运行评分脚本：

```bash
# 连接评分用户
ssh -i ~/.ssh/cka_ed25519 cka000016@11.0.1.102

# 运行评分
cka-score
```

评分脚本会自动检查所有 16 道题目的完成情况，输出格式化的评分报告。

## SSH 连接方式

部署完成后，控制节点 `/root/cka-ssh-info/` 目录包含：
- `cka_ed25519` - SSH 私钥
- `cka_ed25519.pub` - SSH 公钥
- `ssh_config` - SSH 配置

复制到本地使用：
```bash
cp /root/cka-ssh-info/cka_ed25519 ~/.ssh/
chmod 600 ~/.ssh/cka_ed25519
cp /root/cka-ssh-info/ssh_config ~/.ssh/config

# 连接示例
ssh cka000001    # Question 1 (cluster1)
ssh cka000013    # Question 13 (cluster2)
ssh cka000015    # Question 15 (cluster3)
ssh cka000016    # Question 16 + 评分系统 (cluster3)
```

## 验证部署

```bash
# 检查节点状态
kubectl get nodes -o wide

# 检查系统 Pod
kubectl get pods -n kube-system

# 检查 Calico 状态 (cluster2)
kubectl get pods -n calico-system

# 运行评分脚本
ssh cka000016@11.0.1.102 cka-score
```

## 文档索引

| 文档 | 说明 |
|------|------|
| [docs/cka2026.md](docs/cka2026.md) | CKA 真题详解与答案 |
| [docs/questions.md](docs/questions.md) | 16 道 CKA 题目部署说明 |
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
│       ├── question/      # CKA 题目部署
│       └── score/         # 评分系统部署
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
| Helm | 3.17.3 |

## 参考

- [Kubernetes 官方文档](https://kubernetes.io/docs/)
- [CKA 考试大纲](https://github.com/cncf/curriculum)
- [Gateway API 文档](https://gateway-api.sigs.k8s.io/)
