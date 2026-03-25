# CKA 题目部署说明

## 题目分布

| 题目 | 集群 | 命名空间 | 考生用户 | 部署内容 |
|------|------|----------|----------|----------|
| Q01 | cluster1 | autoscale | cka000050 | HPA autoscale |
| Q02 | cluster1 | sound-repeater | cka000024 | Ingress + Service |
| Q03 | cluster1 | default | cka000037 | 节点污点 + Pod |
| Q04 | cluster1 | one | cka000046 | etcd 备份与还原 |
| Q05 | cluster1 | delta | cka000025 | Deployment + Service |
| Q06 | cluster1 | priority | cka000049 | PriorityClass |
| Q07 | cluster1 | argocd | cka000060 | Helm ArgoCD |
| Q08 | cluster1 | mariadb | cka000023 | PVC |
| Q09 | cluster1 | default | cka000029 | Gateway API |
| Q10 | cluster1 | frontend/backend | cka000031 | NetworkPolicy |
| Q11 | cluster1 | cert-manager | cka000011 | cert-manager CRD |
| Q12 | cluster1 | k8snginx | cka000012 | HTTPS + ConfigMap |
| Q13 | cluster2 | calico-netpol | cka000013 | Calico NetworkPolicy |
| Q14 | cluster1 | hollow | cka000014 | Resources |
| Q15 | cluster3 | default | cka000015 | etcd 故障排查 |
| Q16 | cluster3 | default | cka000016 | cri-dockerd |

## 特殊题目说明

### Q03 - 污点题目
Pod 将因节点污点而处于 Pending 状态，考生需要移除污点使 Pod 运行。

### Q11 - cert-manager
使用在线 YAML 部署 cert-manager v1.13.0，镜像已通过离线包导入。

### Q15 - 故障排查（破坏性）
**注意**：此题目会破坏集群！
- 修改 kube-apiserver etcd 端点为错误 IP
- 修改 kube-scheduler CPU 请求为 4（导致无法调度）

用于练习集群故障排查和修复。

### Q16 - cri-dockerd
需要安装 cri-dockerd 并配置 kubelet 使用 cri-docker 运行时。

## 连接方式

部署完成后，控制节点 `/root/cka-ssh-info/` 目录包含：
- `cka_ed25519` - SSH 私钥
- `ssh_config` - SSH 配置

复制到本地使用：
```bash
cp /root/cka-ssh-info/cka_ed25519 ~/.ssh/
chmod 600 ~/.ssh/cka_ed25519
cp /root/cka-ssh-info/ssh_config ~/.ssh/config

# 连接示例
ssh cka000050  # Question 1
ssh cka000013  # Question 13 (cluster2)
ssh cka000015  # Question 15 (cluster3)
```
