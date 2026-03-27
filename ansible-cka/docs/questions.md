# CKA 题目部署说明

## 题目分布

| 题目 | 集群 | 命名空间 | 考生用户 | 部署内容 |
|------|------|----------|----------|----------|
| Q01 | cluster1 | autoscale | cka000001 | HPA autoscale |
| Q02 | cluster1 | sound-repeater | cka000002 | Ingress + Service |
| Q03 | cluster1 | default | cka000003 | 节点污点 + Pod |
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

## 特殊题目说明

### Q03 - 污点题目
Pod 将因节点污点而处于 Pending 状态，考生需要移除污点使 Pod 运行。

### Q09 - Gateway API
- 预先部署了 Ingress `web` 和 GatewayClass `nginx`
- 考生需要创建 Gateway + HTTPRoute，然后删除旧 Ingress
- Gateway 需要配置 HTTPS listener + TLS termination

### Q11 - cert-manager
- 使用在线 YAML 部署 cert-manager v1.13.0，镜像已通过离线包导入
- nginx 虚拟主机配置在 18080 端口，暴露 /home/cka000011 目录
- 评分系统通过 HTTP 访问该端口验证 crd-list.txt 和 certificate-subject.txt

### Q13 - Calico (cluster2)
- 集群初始没有 CNI，需要考生自行安装 Calico
- tigera-operator 已预安装 CRD
- 考生需要应用 Installation 资源完成 Calico 部署

### Q15 - 故障排查（破坏性）
**注意**：此题目会破坏集群！
- 修改 kube-apiserver etcd 端点为错误 IP
- 修改 kube-scheduler CPU 请求为 4（导致无法调度）

用于练习集群故障排查和修复。

### Q16 - cri-dockerd
- 需要安装 cri-dockerd 并配置系统参数
- Docker 和 deb 包已预安装
- 评分系统部署在此用户上

## 连接方式

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
ssh cka000001    # Question 1
ssh cka000013    # Question 13 (cluster2)
ssh cka000015    # Question 15 (cluster3)
ssh cka000016    # Question 16 + 评分系统 (cluster3)
```

## 评分系统

评分脚本部署在 cluster3 的 cka000016 用户上：

```bash
# 连接评分用户
ssh cka000016@11.0.1.102

# 运行评分
cka-score
```

评分脚本会自动检查所有 16 道题目的完成情况。
