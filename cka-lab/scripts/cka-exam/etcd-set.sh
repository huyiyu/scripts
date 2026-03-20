#!/bin/bash
# CKA 第15题 - ETCD 故障环境初始化脚本
# 来源：参考环境 /root/etcd-set.sh
# 用途：破坏etcd配置，创建修复练习环境

# 修改 kube-apiserver 的 etcd 端点配置（模拟IP变更后未更新的情况）
sudo sed -i 's|etcd-servers=https://127.0.0.1:2379|etcd-servers=https://192.168.18.111:2379|' /etc/kubernetes/manifests/kube-apiserver.yaml

# 修改 kube-scheduler 的 CPU 请求（模拟资源问题）
sudo sed -i '/requests:/a\        # set CPU requests to 10% of CPUs available in the SYSTEM' /etc/kubernetes/manifests/kube-scheduler.yaml
sudo sed -i 's/cpu: 100m/cpu: 4/' /etc/kubernetes/manifests/kube-scheduler.yaml

echo "初始化完成,请等待POD重启"
sleep 20
