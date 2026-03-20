# CKA 考试辅助脚本

> 来源：参考环境 /root 目录
> 用于本地 CKA 练习环境

## 脚本说明

### 1. check_cka.sh - 自动评分脚本

**用途**：自动检查16道题目的完成情况并评分

**使用方法**：
```bash
# 在 control plane 节点上执行
sudo -i
/root/check_cka.sh

# 或在本地复制后执行
./check_cka.sh
```

**评分标准**：
- 共16道题，每题1分
- 及格线：11分（约68%）

**检查内容**：
| 题号 | 检查项 |
|-----|--------|
| 1 | HPA 存在且缩容窗口为30秒 |
| 2 | Ingress 域名和服务匹配 |
| 3 | Sidecar 容器存在 |
| 4 | StorageClass 为默认且绑定模式正确 |
| 5 | Service 类型为 NodePort 且端口正确 |
| 6 | PriorityClass 值为 999999999 且已应用 |
| 7 | Argo CD Pod 正常运行 |
| 8 | PVC 和 Deployment 存在 |
| 9 | Gateway 和 HTTPRoute 存在，Ingress 已删除 |
| 10 | NetworkPolicy 正确应用 |
| 11 | CRD 列表和字段文档已生成 |
| 12 | ConfigMap TLSv1.2 配置生效 |
| 13 | Calico Pod 存在 |
| 14 | WordPress Pod 正常运行 |
| 15 | etcd 修复成功，集群就绪 |
| 16 | cri-dockerd 运行中，内核参数已设置 |

### 2. etcd-set.sh - ETCD 故障环境初始化

**用途**：创建第15题（etcd修复）的故障环境

**执行的操作**：
1. 修改 kube-apiserver.yaml，将 etcd 端点从 `127.0.0.1` 改为错误的 `192.168.18.111`
2. 修改 kube-scheduler.yaml，增加 CPU 请求（从 100m 改为 4）

**使用方法**：
```bash
# 在初始化环境后执行，创建故障场景
sudo -i
/root/etcd-set.sh

# 然后练习修复：
# 1. 检查 kube-apiserver 日志，发现 etcd 连接失败
# 2. 修改 /etc/kubernetes/manifests/kube-apiserver.yaml
# 3. 将 etcd-servers 改回 https://127.0.0.1:2379
# 4. 等待 Pod 自动重启
```

### 3. 10-network.yaml - NetworkPolicy 资源

**用途**：第10题（NetworkPolicy）的基础资源

**包含的资源**：
- frontend Deployment（frontend namespace）
- backend Deployment（backend namespace）
- default-deny-all NetworkPolicy（backend namespace，拒绝所有入站流量）

**使用方法**：
```bash
# 应用基础资源
kubectl apply -f 10-network.yaml

# 然后从 ~/netpol 目录选择合适的 NetworkPolicy 应用
kubectl apply -f ~/netpol/netpol2.yaml
```

## 本地环境集成

这些脚本已集成到 Ansible 部署流程中，位于：
```
cka-lab/
└── scripts/
    └── cka-exam/
        ├── check_cka.sh      # 评分脚本
        ├── etcd-set.sh       # 故障环境初始化
        ├── 10-network.yaml   # NetworkPolicy 资源
        └── README.md         # 本文件
```

## 使用流程

### 练习评分
```bash
# 1. 连接到 control plane
ssh ubuntu@192.168.56.10

# 2. 执行评分脚本
sudo -i
bash /root/check_cka.sh
```

### 练习 etcd 修复（第15题）
```bash
# 1. 初始化故障环境
sudo -i
bash /root/etcd-set.sh

# 2. 等待Pod重启，此时集群会出现故障

# 3. 开始修复
kubectl get nodes  # 应该显示 NotReady

# 4. 检查 kube-apiserver 日志
journalctl -u kubelet -f

# 5. 修复配置
vim /etc/kubernetes/manifests/kube-apiserver.yaml
# 将 etcd-servers=https://192.168.18.111:2379
# 改回 etcd-servers=https://127.0.0.1:2379

# 6. 等待自动恢复
kubectl get nodes  # 应该显示 Ready
```

## 注意事项

1. **etcd-set.sh 会破坏集群**，请在练习前确保可以接受集群短暂不可用
2. **check_cka.sh 需要在集群正常时运行**，如果 etcd 损坏可能无法正确评分
3. 所有脚本需要在 **root 用户** 或具有 **sudo 权限** 的用户下执行

## 参考

- 原文件位置：参考环境 `/root/check_cka.sh`, `/root/etcd-set.sh`, `/root/10.network.yaml`
- 博客文章：https://huyiyu.github.io/posts/kubernetes/cka202602/
