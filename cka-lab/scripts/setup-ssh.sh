#!/bin/bash
# 快速配置 SSH 免密登录

NODES=("192.168.56.10" "192.168.56.11" "192.168.56.12")
USER="ubuntu"

# 生成密钥
if [ ! -f ~/.ssh/id_rsa ]; then
    echo "生成 SSH 密钥..."
    ssh-keygen -t rsa -b 4096 -C "cka-lab" -f ~/.ssh/id_rsa -N ""
fi

# 复制公钥
for node in "${NODES[@]}"; do
    echo "配置 $node ..."
    ssh-copy-id -o StrictHostKeyChecking=no -i ~/.ssh/id_rsa.pub $USER@$node
done

echo "SSH 配置完成!"
echo "测试连接: ssh $USER@${NODES[0]} hostname"
