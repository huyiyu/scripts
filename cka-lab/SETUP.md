# CKA 2026 环境搭建完整指南

## 环境要求

- **宿主机**: Windows/Linux/macOS
- **虚拟机软件**: VMware Workstation/Player
- **Ansible**: 2.9+
- **SSH 客户端**: OpenSSH

## 快速开始 (3分钟)

### 1. 克隆或下载本仓库

```bash
cd /c/work  # 或你的目录
git clone <仓库地址> cka-lab  # 或直接解压
```

### 2. 创建虚拟机

使用 VMware 创建 3 台 Ubuntu 22.04 虚拟机：

| 虚拟机名称 | IP 地址 | 内存 | CPU | 磁盘 |
|-----------|---------|------|-----|------|
| controlplane | 192.168.56.10 | 4G | 2核 | 30G |
| node01 | 192.168.56.11 | 2G | 2核 | 20G |
| node02 | 192.168.56.12 | 2G | 2核 | 20G |

详细步骤见 [docs/vmware-setup.md](docs/vmware-setup.md)

### 3. 配置 SSH

```bash
cd /c/work/cka-lab
./scripts/setup-ssh.sh
```

### 4. 部署环境

```bash
# 完整部署（首次使用）
./deploy.sh

# 或分步执行
cd ansible
ansible-playbook -i inventory/hosts.ini playbooks/site.yml
```

## 使用 bash 脚本的详细说明

### 部署脚本选项

```bash
./deploy.sh [选项]

选项:
  -a, --action <action>      操作类型 (默认: full)
                              - full: 完整部署
                              - scenarios: 仅部署题目场景
                              - reset: 重置所有场景
                              - status: 查看节点状态
  -i, --inventory <path>     指定 inventory 文件
  -t, --troubleshooting      包含故障排查场景
  -h, --help                显示帮助

示例:
  ./deploy.sh                           # 完整部署
  ./deploy.sh -a scenarios              # 仅更新场景
  ./deploy.sh -a full -t                # 完整部署+故障场景
  ./deploy.sh -a reset                  # 重置场景
  ./deploy.sh -a status                 # 查看节点状态
```

### Ansible 直接操作

```bash
cd ansible

# 测试连接
ansible all -m ping

# 查看节点信息
ansible all -a "hostname && ip addr"

# 仅安装基础软件
ansible-playbook -i inventory/hosts.ini playbooks/site.yml --tags "common"

# 仅部署 Kubernetes
ansible-playbook -i inventory/hosts.ini playbooks/site.yml --tags "kubernetes,controlplane"

# 仅部署考试场景
ansible-playbook -i inventory/hosts.ini playbooks/deploy-scenarios.yml
```

## 练习题目清单

所有题目场景部署后会创建以下资源：

| 命名空间 | 资源 | 题目类型 |
|---------|------|---------|
| autoscale | apache-server Deployment | HPA |
| sound-repeater | echoserver + Service | Ingress |
| default | synergy-leverager Deployment | Sidecar |
| - | ran-local-path StorageClass | StorageClass |
| spline-reticulator | front-end Deployment | Service NodePort |
| priority | busybox-logger Deployment | PriorityClass |
| mariadb | mariadb Deployment + PVC | PVC |
| default | web Deployment + Ingress | Gateway API |
| backend/frontend | backend/frontend Deployments | NetworkPolicy |
| cert-manager | cert-manager | CRD |
| nginx-static | nginx-static + ConfigMap | ConfigMap TLS |
| relative-fawn | wordpress Deployment | Resources |

## 故障排查场景

使用 `-t` 参数部署后，会有以下问题场景：

1. **nginx-pod** - 镜像拼写错误 (`nginx:ltest`)
2. **hello-kubernetes** - 命令错误 (使用 bash 而不是 sh)
3. **my-pod** - PVC accessMode 不匹配
4. **postgres-deployment** - Secret 名称和 key 错误
5. **nginx-service** - selector 不匹配
6. **nginx-deployment** - ConfigMap 名称错误

## 连接到集群

```bash
# 登录到 control plane
ssh ubuntu@192.168.56.10

# 设置环境
alias k=kubectl
complete -F __start_kubectl k

# 查看状态
kubectl get nodes
kubectl get pods -A
```

## 常用练习命令

```bash
# 生成模板
kubectl run test --image=nginx --dry-run=client -o yaml > pod.yaml
kubectl create deploy test --image=nginx --replicas=3 --dry-run=client -o yaml > deploy.yaml

# 快速操作
kubectl expose deploy test --port=80 --target-port=80 --type=NodePort
kubectl scale deploy test --replicas=5
kubectl set image deploy test nginx=nginx:1.20

# 故障排查
kubectl logs <pod>
kubectl logs <pod> -c <container>
kubectl logs <pod> --previous
kubectl describe pod <pod>
kubectl get events --sort-by='.lastTimestamp'

# 强制删除
kubectl delete pod <pod> --force --grace-period=0

# 编辑资源
kubectl edit deploy <name>
kubectl patch deploy <name> --type='json' -p='[...]'
```

## 文件说明

```
cka-lab/
├── deploy.sh              # 主部署脚本 (bash)
├── SETUP.md               # 本文件
├── QUICKSTART.md          # 快速参考
├── README.md              # 详细说明
├── ansible/               # Ansible 配置
│   ├── ansible.cfg
│   ├── inventory/
│   │   └── hosts.ini      # 主机清单 (修改 IP)
│   ├── playbooks/
│   │   ├── site.yml       # 主 playbook
│   │   └── deploy-scenarios.yml
│   └── roles/             # 角色定义
│       ├── common/        # 基础配置
│       ├── containerd/    # 容器运行时
│       ├── kubernetes/    # K8s 安装
│       ├── controlplane/  # 控制平面
│       ├── cni/           # Calico 网络
│       ├── addons/        # 插件
│       ├── worker/        # 工作节点
│       └── cka_scenarios/ # 考试场景
├── scripts/               # 辅助脚本
│   └── setup-ssh.sh       # SSH 配置
├── manifests/             # YAML 文件
│   ├── questions/         # 题目模板
│   └── solutions/         # 参考答案
└── docs/                  # 文档
    └── vmware-setup.md    # VMware 设置
```

## 考试技巧

1. **设置别名** (考试环境已预配置)
   ```bash
   alias k=kubectl
   complete -F __start_kubectl k
   ```

2. **使用模板生成**
   ```bash
   k run pod --image=nginx --dry-run=client -o yaml > pod.yaml
   ```

3. **快速查找文档**
   - 使用 `kubectl explain` 命令
   - 官网搜索框直接搜索资源类型

4. **保存常用书签**
   - https://kubernetes.io/docs/concepts/
   - https://kubernetes.io/docs/tasks/
   - https://kubernetes.io/docs/reference/kubectl/

## 常见问题

### Q: 节点无法加入集群
A: 检查 token 是否过期，在 controlplane 执行：
```bash
kubeadm token create --print-join-command
```

### Q: Calico 无法启动
A: 检查 Pod CIDR 配置是否正确：
```bash
kubectl get ippool -n calico-system
```

### Q: Metrics Server 无法工作
A: 检查证书配置：
```bash
kubectl logs -n kube-system deployment/metrics-server
```

### Q: 虚拟机 IP 变化
A: 修改 `ansible/inventory/hosts.ini` 中的 IP 地址

## 清理环境

```bash
# 删除所有场景
ssh ubuntu@192.168.56.10 "kubectl delete ns --all --ignore-not-found=true"

# 重置集群 (在节点上执行)
sudo kubeadm reset -f
sudo rm -rf /etc/cni/net.d
sudo rm -rf /var/lib/etcd
sudo rm -rf ~/.kube
```

## 参考资源

- [CKA 刷题心得](https://huyiyu.github.io/posts/kubernetes/killersh/)
- [CKA 一模考试总结](https://huyiyu.github.io/posts/kubernetes/cka-simulators1/)
- [CKA 二模考试总结](https://huyiyu.github.io/posts/kubernetes/cka-simulators2/)
- [k8s 真题总结](https://huyiyu.github.io/posts/kubernetes/cka202602/)
- [Killer.sh CKA](https://killercoda.com/sachin/course/CKA)

---

祝你考试顺利! 🎉
