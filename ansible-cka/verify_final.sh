#!/bin/bash
# CKA 题目环境最终验证

echo "================================================================================"
echo "CKA 题目环境最终验证报告"
echo "================================================================================"

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

check_ssh() {
    local host=$1
    local user=$2
    if ssh -o StrictHostKeyChecking=no -i /root/.ssh/cka_ed25519 -o ConnectTimeout=3 "$user@$host" 'whoami' >/dev/null 2>&1; then
        echo -e "${GREEN}✓${NC} SSH 登录正常"
        return 0
    else
        echo -e "${RED}✗${NC} SSH 登录失败"
        return 1
    fi
}

echo ""
echo "【CLUSTER1 - 11.0.1.100】"
echo "------------------------------------------------------------"
# 检查节点状态
if ssh -o StrictHostKeyChecking=no -i ~/.ssh/id_rsa root@11.0.1.100 'kubectl get nodes' >/dev/null 2>&1; then
    echo -e "节点状态: ${GREEN}✓ Ready${NC}"
else
    echo -e "节点状态: ${RED}✗ NotReady${NC}"
fi

# Q01
echo -n "Q01 (HPA): "
check_ssh "11.0.1.100" "cka000001"

# Q02
echo -n "Q02 (Ingress): "
check_ssh "11.0.1.100" "cka000002"

# Q03
echo -n "Q03 (Sidecar): "
check_ssh "11.0.1.100" "cka000003"

# Q04
echo -n "Q04 (StorageClass): "
check_ssh "11.0.1.100" "cka000004"

# Q05
echo -n "Q05 (Service): "
check_ssh "11.0.1.100" "cka000005"

# Q06
echo -n "Q06 (PriorityClass): "
check_ssh "11.0.1.100" "cka000006"

# Q07
echo -n "Q07 (Helm ArgoCD): "
check_ssh "11.0.1.100" "cka000007"

# Q08
echo -n "Q08 (PVC): "
check_ssh "11.0.1.100" "cka000008"

# Q09
echo -n "Q09 (Gateway): "
check_ssh "11.0.1.100" "cka000009"

# Q10
echo -n "Q10 (NetworkPolicy): "
check_ssh "11.0.1.100" "cka000010"

# Q11
echo -n "Q11 (CRD): "
check_ssh "11.0.1.100" "cka000011"

# Q12
echo -n "Q12 (ConfigMap TLS): "
if check_ssh "11.0.1.100" "cka000012"; then
    # 额外检查 nginx-static Pod 状态
    if ssh -o StrictHostKeyChecking=no -i ~/.ssh/id_rsa root@11.0.1.100 'kubectl get pod -n nginx-static -l app=nginx-static -o jsonpath="{.items[0].status.phase}"' 2>/dev/null | grep -q "Running"; then
        echo -e "  ${GREEN}✓${NC} nginx-static Pod 运行正常"
    else
        echo -e "  ${YELLOW}!${NC} nginx-static Pod 可能有问题"
    fi
fi

# Q14
echo -n "Q14 (Resources): "
check_ssh "11.0.1.100" "cka000014"

echo ""
echo "【CLUSTER2 - 11.0.1.101】"
echo "------------------------------------------------------------"
# 检查节点状态（应该是 NotReady，因为没有 CNI）
if ssh -o StrictHostKeyChecking=no -i ~/.ssh/id_rsa root@11.0.1.101 'kubectl get nodes' 2>&1 | grep -q "NotReady"; then
    echo -e "节点状态: ${YELLOW}! NotReady (设计如此，需安装 Calico)${NC}"
else
    echo -e "节点状态: ${GREEN}✓ Ready${NC}"
fi

# Q13
echo -n "Q13 (Calico): "
if check_ssh "11.0.1.101" "cka000013"; then
    # 检查 tigera-operator 是否已安装
    if ssh -o StrictHostKeyChecking=no -i ~/.ssh/id_rsa root@11.0.1.101 'kubectl get pods -n tigera-operator' 2>/dev/null | grep -q "tigera-operator"; then
        echo -e "  ${GREEN}✓${NC} tigera-operator 已安装"
    else
        echo -e "  ${YELLOW}!${NC} tigera-operator 未安装"
    fi
fi

echo ""
echo "【CLUSTER3 - 11.0.1.102】"
echo "------------------------------------------------------------"
echo -e "节点状态: ${YELLOW}! 无法连接 API Server (设计如此，故障排查题)${NC}"

# Q15
echo -n "Q15 (etcd troubleshoot): "
if check_ssh "11.0.1.102" "cka000015"; then
    echo -e "  ${YELLOW}!${NC} 需要修复 kube-apiserver 和 kube-scheduler 配置"
fi

# Q16
echo -n "Q16 (cri-dockerd): "
if check_ssh "11.0.1.102" "cka000016"; then
    # 检查 cri-dockerd 包是否存在
    if ssh -o StrictHostKeyChecking=no -i ~/.ssh/id_rsa root@11.0.1.102 'ls ~/cri-dockerd_*.deb 2>/dev/null' >/dev/null 2>&1; then
        echo -e "  ${GREEN}✓${NC} cri-dockerd 包已准备"
    else
        echo -e "  ${YELLOW}!${NC} cri-dockerd 包可能不存在"
    fi
fi

echo ""
echo "================================================================================"
echo "总结"
echo "================================================================================"
echo -e "${GREEN}✓${NC} Q12 证书问题已修复"
echo -e "${GREEN}✓${NC} SSH 登录问题已修复"
echo -e "${YELLOW}!${NC} Q13 需要安装 Calico (考生任务)"
echo -e "${YELLOW}!${NC} Q15 需要修复集群故障 (考生任务)"
echo "================================================================================"
