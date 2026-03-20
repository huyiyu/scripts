# CKA 2026 环境快速启动

> 基于 cka202602.md 的 16 道真题

## 3步快速开始

### 1. 准备虚拟机 (VMware)

| 虚拟机 | IP | 内存 | CPU |
|-------|-----|------|-----|
| controlplane | 192.168.56.10 | 4G | 2核 |
| node01 | 192.168.56.11 | 2G | 2核 |
| node02 | 192.168.56.12 | 2G | 2核 |

### 2. 配置 SSH 并部署

```bash
cd /c/work/cka-lab
./scripts/setup-ssh.sh
./deploy.sh
```

### 3. 连接到集群

```bash
ssh ubuntu@192.168.56.10
alias k=kubectl
kubectl get nodes
```

## cka202602.md 题目速查

| 题号 | 题目 | 关键命令 |
|-----|------|---------|
| Q1 | HPA | `kubectl autoscale deploy ... --cpu=50% --min=1 --max=4` |
| Q2 | Ingress | `kubectl create ingress echo --class=nginx ...` |
| Q3 | sidecar | `kubectl edit deploy synergy-leverager` |
| Q4 | StorageClass | 官网复制模板，设置 default |
| Q5 | Service | `kubectl expose deploy ... --type=NodePort` |
| Q6 | PriorityClass | `kubectl get pc` 查看现有，创建新 PC |
| Q7 | Helm | `helm repo add`, `helm template`, `helm install` |
| Q8 | PVC | 官网复制模板，storageClassName: local-path |
| Q9 | Gateway | Gateway + HTTPRoute，删除旧 Ingress |
| Q10 | NetworkPolicy | 从 ~/netpol 选择合适的策略应用 |
| Q11 | CRD | `kubectl get crds`, `kubectl explain ...` |
| Q12 | ConfigMap | `kubectl edit cm ...`，设置 immutable |
| Q13 | Calico | 安装 tigera-operator 和 custom-resources |
| Q14 | Resources | `kubectl edit deploy ...` 调整 requests |
| Q15 | etcd | 检查 static pod 配置，修复 kubelet |
| Q16 | cri-dockerd | `dpkg -i`，sysctl 配置 |

## 常用命令

```bash
# 设置别名
alias k=kubectl
complete -F __start_kubectl k

# 生成模板
k run test --image=nginx --dry-run=client -o yaml > pod.yaml
k create deploy test --image=nginx --replicas=3 --dry-run=client -o yaml > deploy.yaml

# 强制删除
k delete pod <name> --force --grace-period=0

# 查看文档
k explain pod.spec.containers
```

## 部署脚本

```bash
./deploy.sh              # 完整部署
./deploy.sh -a scenarios # 仅部署题目
./deploy.sh -a reset     # 重置题目
./deploy.sh -a status    # 查看状态
```

## 官方文档

- https://kubernetes.io/docs
- https://kubernetes.io/blog/
- https://helm.sh/docs
- https://gateway-api.sigs.k8s.io

---

祝考试顺利! 🎉
