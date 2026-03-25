# 集群重置方法

## 重置单个节点

在目标节点上执行：

```bash
# 重置 kubeadm
sudo kubeadm reset -f

# 清理 iptables
sudo iptables -F && sudo iptables -t nat -F && sudo iptables -t mangle -F && sudo iptables -X

# 清理 CNI 和 kubelet 数据
sudo rm -rf /etc/cni/net.d /var/lib/kubelet

# 重启 containerd
sudo systemctl restart containerd
```

然后重新部署：
```bash
ansible-playbook playbooks/site.yml -l k8s-node01
```

## 强制重置所有节点

使用 `force_reset` 变量：

```bash
ansible-playbook playbooks/site.yml -e force_reset=true
```

这将：
1. 重置所有集群节点
2. 重新部署 Kubernetes
3. 重新部署所有启用的题目

## 幂等部署

默认情况下，kubernetes role 是幂等的：
- 如果集群已存在 (`/etc/kubernetes/admin.conf` 存在)，跳过集群部署
- 只执行题目部署任务

这适用于 Q15 故障题场景：修复集群后重新运行 playbook 不会重置集群。
