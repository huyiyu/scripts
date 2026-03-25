# 配置变量说明

## 版本配置

编辑 `ansible-k8s/inventory/group_vars/all.yml`：

```yaml
# Kubernetes 版本
k8s_version: "1.34.3"

# 容器运行时
containerd_version: "2.2.1"
runc_version: "1.3.4"
cni_plugins_version: "1.8.0"

# CNI 网络插件
calico_version: "v3.30.6"
calico_operator_version: "v1.38.11"

# 核心组件
etcd_version: "3.5.26"
coredns_version: "v1.12.1"
```

## 网络配置

```yaml
# 集群网络
pod_network_cidr: "192.168.0.0/16"
service_cidr: "10.96.0.0/12"
network_interface: "eth0"
```

## 离线资源路径

```yaml
# 目标节点上的路径
offline_packages_path: "/opt/offline/packages"
offline_images_path: "/opt/offline/images"
```

## 按集群配置

各集群特有配置在：
- `ansible-k8s/inventory/group_vars/cluster1.yml`
- `ansible-k8s/inventory/group_vars/cluster2.yml`
- `ansible-k8s/inventory/group_vars/cluster3.yml`

例如启用/禁用题目：
```yaml
question1_enabled: true
question2_enabled: false
```

## 强制重置

如需强制重新部署集群：
```bash
ansible-playbook playbooks/site.yml -e force_reset=true
```
