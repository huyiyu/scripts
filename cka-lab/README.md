# CKA 2026 考试环境搭建指南

> 基于 cka202602.md 中的 16 道真题搭建的完整 CKA 考试练习环境
> 参考: https://huyiyu.github.io

## 环境概述

本环境严格按照 cka202602.md 中的 16 道题目原封不动创建：

| 题号 | 题目 | 描述 |
|-----|------|------|
| Q1 | HPA autoscale | 自动扩缩容配置 |
| Q2 | Ingress | 创建 Ingress 资源 |
| Q3 | sidecar | 多容器 Pod 配置 |
| Q4 | StorageClass | 存储类配置 |
| Q5 | Service | NodePort 服务配置 |
| Q6 | PriorityClass | 优先级类配置 |
| Q7 | Helm ArgoCD | 使用 Helm 安装 ArgoCD |
| Q8 | PVC | 持久卷声明配置 |
| Q9 | Gateway | Gateway API 配置 |
| Q10 | NetworkPolicy | 网络策略配置 |
| Q11 | CRD | 自定义资源定义 |
| Q12 | ConfigMap | TLS 配置和不可变 ConfigMap |
| Q13 | Calico | CNI 安装 |
| Q14 | Resources cpu/memory | 资源限制配置 |
| Q15 | etcd | 集群修复 |
| Q16 | cri-dockerd | 容器运行时配置 |

## 快速开始

### 1. 准备 VMware 虚拟机

创建 3 台 Ubuntu 22.04 虚拟机：

| 主机名 | IP 地址 | 配置 | 角色 |
|-------|---------|------|------|
| controlplane | 192.168.56.10 | 4C8G | Control Plane |
| node01 | 192.168.56.11 | 2C4G | Worker |
| node02 | 192.168.56.12 | 2C4G | Worker |

### 2. 配置 SSH 免密登录

```bash
./scripts/setup-ssh.sh
```

### 3. 部署环境

```bash
# 完整部署（首次使用）
./deploy.sh

# 或分步执行
cd ansible
ansible-playbook -i inventory/hosts.ini playbooks/site.yml
```

### 4. 开始练习

```bash
ssh ubuntu@192.168.56.10
alias k=kubectl
kubectl get nodes
```

## 题目清单 (cka202602.md)

### Q1 | HPA autoscale
```bash
# 在 autoscale namespace 中创建 HPA
kubectl autoscale -n autoscale deploy apache-server --cpu=50% --min=1 --max=4

# 编辑 HPA 设置缩容稳定窗口
kubectl edit hpa apache-server -n autoscale
# 修改 spec.behavior.scaleDown.stabilizationWindowSeconds 为 30
```

### Q2 | Ingress
```bash
# 创建 Ingress
kubectl create ingress echo --class=nginx -n sound-repeater \
  --rule="example.org/echo=echoserver-service:8080"

# 测试
curl -H "Host: example.org" http://<node-ip>:<nodeport>/echo
```

### Q3 | sidecar
```bash
# 编辑 synergy-leverager Deployment 添加 sidecar 容器
kubectl edit deploy synergy-leverager
```

### Q4 | StorageClass
```bash
# 查看 StorageClass
kubectl get sc
# ran-local-path 已创建，需要设置为默认
```

### Q5 | Service
```bash
# 创建 NodePort Service
kubectl expose -n spline-reticulator deployment front-end \
  --port=80 --target-port=80 --type=NodePort --name front-end-svc
```

### Q6 | PriorityClass
```bash
# 查看现有 PriorityClass
kubectl get pc
# max-user-priority=1000000000 已存在

# 创建 high-priority (值为 999999999)
# 应用到 busybox-logger Deployment
kubectl edit deploy busybox-logger -n priority
```

### Q7 | Helm ArgoCD
```bash
# 添加仓库
helm repo add argo https://argoproj.github.io/argo-helm/

# 生成模板
helm template argocd argo/argo-cd -n argocd --version 7.7.3 \
  --set crds.install=false > ~/argo-helm.yaml

# 安装
helm install argocd argo/argo-cd -n argocd --version 7.7.3 \
  --set crds.install=false
```

### Q8 | PVC
```bash
# mariadb namespace 中已存在 PV mariadb-pv
# 创建 PVC 绑定到该 PV
kubectl apply -f - <<EOF
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: mariadb
  namespace: mariadb
spec:
  accessModes:
  - ReadWriteOnce
  resources:
    requests:
      storage: 250Mi
  storageClassName: "local-path"
EOF
```

### Q9 | Gateway
```bash
# 查看现有 Ingress web
kubectl get ingress web -o yaml

# 创建 Gateway 和 HTTPRoute
# GatewayClass nginx 已存在
kubectl apply -f - <<EOF
apiVersion: gateway.networking.k8s.io/v1
kind: Gateway
metadata:
  name: web-gateway
spec:
  gatewayClassName: nginx
  listeners:
  - name: https
    protocol: HTTPS
    port: 443
    hostname: gateway.web.k8s.local
    tls:
      mode: Terminate
      certificateRefs:
      - name: web-cert
---
apiVersion: gateway.networking.k8s.io/v1
kind: HTTPRoute
metadata:
  name: web-route
spec:
  parentRefs:
  - name: web-gateway
  hostnames:
  - gateway.web.k8s.local
  rules:
  - matches:
    - path:
        type: PathPrefix
        value: /
    backendRefs:
    - name: web
      port: 80
EOF

# 删除旧 Ingress
kubectl delete ingress web
```

### Q10 | NetworkPolicy
```bash
# 查看 backend 和 frontend namespace
kubectl get deploy -n backend --show-labels
kubectl get deploy -n frontend --show-labels

# netpol 目录下有三个示例策略，选择合适的应用
kubectl apply -f ~/netpol/netpol2.yaml
```

### Q11 | CRD
```bash
# cert-manager 已安装
# 获取 CRD 列表
kubectl get crds | grep cert-manager > ~/resources.yaml

# 获取 Certificate subject 文档
kubectl explain Certificate.spec.subject > ~/subject.yaml
```

### Q12 | ConfigMap
```bash
# 编辑 nginx-config ConfigMap
kubectl edit cm nginx-config -n nginx-static
# 修改 ssl_protocols 为 TLSv1.2 TLSv1.3
# 添加 immutable: true

# 重启 Deployment
kubectl rollout restart deploy nginx-static -n nginx-static
```

### Q13 | Calico
```bash
# Calico 已在集群中安装
# 如需重新安装：
kubectl create -f https://raw.githubusercontent.com/projectcalico/calico/v3.27.0/manifests/tigera-operator.yaml
kubectl create -f https://raw.githubusercontent.com/projectcalico/calico/v3.27.0/manifests/custom-resources.yaml
```

### Q14 | Resources
```bash
# 查看节点资源
kubectl describe node <node-name>

# 编辑 wordpress Deployment 调整资源请求
kubectl edit deploy wordpress -n relative-fawn
# 将 requests 调整为节点可分配资源的 1/3
```

### Q15 | etcd
```bash
# 检查 etcd 状态
kubectl get pods -n kube-system | grep etcd

# 检查 kubelet 状态
systemctl status kubelet
journalctl -u kubelet -f

# 常见修复：
# - 检查 /etc/kubernetes/manifests/etcd.yaml
# - 检查证书 /etc/kubernetes/pki/etcd/
# - 检查 kubelet 配置 /var/lib/kubelet/config.yaml
```

### Q16 | cri-dockerd
```bash
# 安装 cri-dockerd 包
dpkg -i ~/cri-dockerd_0.3.6.3-0.ubuntu-jammy_amd64.deb

# 启用服务
systemctl enable cri-docker
systemctl start cri-docker

# 配置 sysctl
sysctl -w net.bridge.bridge-nf-call-iptables=1
sysctl -w net.ipv6.conf.all.forwarding=1
sysctl -w net.ipv4.ip_forward=1
sysctl -w net.netfilter.nf_conntrack_max=131072
```

## 使用 bash 脚本

```bash
# 完整部署
./deploy.sh

# 仅部署题目场景
./deploy.sh -a scenarios

# 重置场景
./deploy.sh -a reset

# 查看状态
./deploy.sh -a status

# 帮助
./deploy.sh -h
```

## 官方文档 (考试允许)

- https://kubernetes.io/docs
- https://kubernetes.io/blog/
- https://helm.sh/docs
- https://gateway-api.sigs.k8s.io

## 考试注意事项

1. 使用 `alias k=kubectl` 节省时间
2. 掌握 `--dry-run=client -o yaml` 生成模板
3. 掌握 `--force --grace-period=0` 强制删除
4. 每个题目注意切换到正确的 context
5. 做完题目记得 `exit` 退出主机

## 文件结构

```
cka-lab/
├── deploy.sh              # 主部署脚本 (bash)
├── SETUP.md               # 详细搭建指南
├── QUICKSTART.md          # 快速参考
├── ansible/               # Ansible 配置
│   ├── inventory/
│   │   └── hosts.ini      # 主机清单
│   ├── playbooks/
│   │   ├── site.yml       # 主 playbook
│   │   └── deploy-scenarios.yml
│   └── roles/
│       ├── common/        # 基础环境
│       ├── containerd/    # 容器运行时
│       ├── kubernetes/    # K8s 组件
│       ├── controlplane/  # 控制平面
│       ├── cni/           # Calico
│       ├── addons/        # 插件
│       ├── worker/        # Worker 节点
│       └── cka_scenarios/ # 16道题目场景
├── scripts/
│   └── setup-ssh.sh       # SSH 配置
└── docs/
    └── vmware-setup.md    # VMware 设置
```

## 参考

- cka202602.md: https://huyiyu.github.io/posts/kubernetes/cka202602/
- CKA 刷题心得: https://huyiyu.github.io/posts/kubernetes/killersh/

---

祝你 CKA 考试顺利！🎉
