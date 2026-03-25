# 故障排查指南

## 检查离线资源

```bash
# 检查包是否存在
ls -la /opt/offline/packages/containerd/
ls -la /opt/offline/packages/k8s/
ls -la /opt/offline/packages/calico/
ls -la /opt/offline/images/

# 检查镜像是否导入
sudo ctr -n k8s.io images list | grep kube
sudo ctr -n k8s.io images list | grep calico

# 检查二进制文件
which kubelet kubeadm kubectl
kubelet --version
```

## 查看日志

```bash
# kubelet 日志
sudo journalctl -u kubelet -f

# containerd 日志
sudo journalctl -u containerd -f

# Calico Operator 日志
kubectl logs -n tigera-operator -l name=tigera-operator

# Calico 组件日志
kubectl logs -n calico-system -l k8s-app=calico-node
```

## 常见问题

### 节点 NotReady
```bash
# 检查 kubelet 状态
sudo systemctl status kubelet

# 检查 CNI 配置
ls /etc/cni/net.d/

# 重置并重新部署
sudo kubeadm reset -f
sudo iptables -F && sudo iptables -t nat -F
sudo rm -rf /etc/cni/net.d /var/lib/kubelet
ansible-playbook playbooks/site.yml -l <节点>
```

### Pod 无法启动
```bash
# 查看 Pod 事件
kubectl describe pod <pod-name>

# 查看节点资源
kubectl describe node <node-name>
```

### Q15 集群故障修复
Q15 会破坏集群，修复方法：

```bash
# 1. 修复 etcd 端点
sudo sed -i 's/192.168.18.111/127.0.0.1/g' /etc/kubernetes/manifests/kube-apiserver.yaml

# 2. 修复 scheduler CPU
sudo sed -i 's/cpu: 4/cpu: 100m/g' /etc/kubernetes/manifests/kube-scheduler.yaml

# 3. 等待自动恢复
watch kubectl get pods -n kube-system
```
