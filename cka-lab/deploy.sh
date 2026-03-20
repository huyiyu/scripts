#!/bin/bash
# CKA 2026 考试环境部署脚本 (Bash 版本)
# 严格按照 cka202602.md 中的 16 道题目创建
# 参考: https://huyiyu.github.io

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# 默认配置
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
INVENTORY="${SCRIPT_DIR}/ansible/inventory/hosts.ini"
ACTION="full"

# 显示帮助
show_help() {
    echo "CKA 2026 考试环境部署工具 (cka202602.md)"
    echo ""
    echo "用法: ./deploy.sh [选项]"
    echo ""
    echo "选项:"
    echo "  -a, --action <action>     操作类型: full|scenarios|reset|status (默认: full)"
    echo "  -i, --inventory <path>    inventory 文件路径"
    echo "  -h, --help               显示帮助"
    echo ""
    echo "示例:"
    echo "  ./deploy.sh                           # 完整部署"
    echo "  ./deploy.sh -a scenarios              # 仅部署场景"
    echo "  ./deploy.sh -a reset                  # 重置场景"
    echo "  ./deploy.sh -a status                 # 查看状态"
}

# 显示标题
show_header() {
    clear 2>/dev/null || true
    echo -e "${CYAN}========================================${NC}"
    echo -e "${CYAN}   CKA 2026 考试环境部署工具${NC}"
    echo -e "${CYAN}   题目来源: cka202602.md${NC}"
    echo -e "${CYAN}   参考: https://huyiyu.github.io${NC}"
    echo -e "${CYAN}========================================${NC}"
    echo ""
}

# 测试 SSH 连接
test_ssh_connection() {
    local ip=$1
    timeout 5 bash -c "cat < /dev/null > /dev/tcp/$ip/22" 2>/dev/null
}

# 检查 Ansible
 check_ansible() {
    if command -v ansible &> /dev/null; then
        echo -e "${GREEN}Ansible 已安装: $(ansible --version | head -1)${NC}"
        return 0
    else
        echo -e "${RED}Ansible 未安装${NC}"
        echo -e "${YELLOW}请安装 Ansible:${NC}"
        echo "  Ubuntu/Debian: sudo apt install ansible"
        echo "  CentOS/RHEL:   sudo yum install ansible"
        echo "  macOS:         brew install ansible"
        echo "  Python:        pip install ansible"
        return 1
    fi
}

# 显示状态
show_status() {
    echo -e "${YELLOW}检查集群状态...${NC}"
    
    local nodes=("192.168.56.10" "192.168.56.11" "192.168.56.12")
    
    for node in "${nodes[@]}"; do
        echo -n "检查节点 $node ..."
        if test_ssh_connection "$node"; then
            echo -e " ${GREEN}在线${NC}"
        else
            echo -e " ${RED}离线${NC}"
        fi
    done
}

# 完整部署
deploy_full() {
    echo -e "${YELLOW}开始完整部署...${NC}"
    
    cd "${SCRIPT_DIR}/ansible"
    
    echo -e "${CYAN}步骤 1/5: 测试连接...${NC}"
    if ! ansible all -i "$INVENTORY" -m ping; then
        echo -e "${RED}连接测试失败，请检查 SSH 配置${NC}"
        exit 1
    fi
    
    echo -e "${CYAN}步骤 2/5: 部署基础环境...${NC}"
    ansible-playbook -i "$INVENTORY" playbooks/site.yml --tags "common,containerd,kubernetes"
    
    echo -e "${CYAN}步骤 3/5: 初始化集群...${NC}"
    ansible-playbook -i "$INVENTORY" playbooks/site.yml --tags "controlplane,cni,addons"
    
    echo -e "${CYAN}步骤 4/5: 加入 Worker 节点...${NC}"
    ansible-playbook -i "$INVENTORY" playbooks/site.yml --tags "worker"
    
    echo -e "${CYAN}步骤 5/5: 部署考试题目场景 (cka202602.md)...${NC}"
    ansible-playbook -i "$INVENTORY" playbooks/site.yml --tags "cka_scenarios"
    
    echo -e "${GREEN}部署完成!${NC}"
}

# 仅部署场景
deploy_scenarios() {
    echo -e "${YELLOW}仅部署考试题目场景 (cka202602.md)...${NC}"
    
    cd "${SCRIPT_DIR}/ansible"
    
    ansible-playbook -i "$INVENTORY" playbooks/deploy-scenarios.yml
    
    echo -e "${GREEN}场景部署完成!${NC}"
}

# 重置场景
reset_scenarios() {
    echo -e "${YELLOW}重置考试题目场景...${NC}"
    echo -e "${RED}注意: 这将删除所有练习资源!${NC}"
    
    read -p "确认重置? (yes/no) " confirm
    if [ "$confirm" != "yes" ]; then
        echo -e "${YELLOW}已取消${NC}"
        return
    fi
    
    # 使用 SSH 执行清理 - 只删除 cka202602.md 中定义的命名空间
    ssh ubuntu@192.168.56.10 << 'EOF'
kubectl delete ns autoscale sound-repeater spline-reticulator priority mariadb nginx-static relative-fawn backend frontend --ignore-not-found=true 2>/dev/null || true
kubectl delete deploy synergy-leverager --ignore-not-found=true 2>/dev/null || true
kubectl delete deploy web --ignore-not-found=true 2>/dev/null || true
kubectl delete ingress web --ignore-not-found=true 2>/dev/null || true
kubectl delete secret web-cert --ignore-not-found=true 2>/dev/null || true
echo "清理完成"
EOF
    
    echo -e "${YELLOW}场景已重置，重新部署中...${NC}"
    deploy_scenarios
}

# 解析参数
parse_args() {
    while [[ $# -gt 0 ]]; do
        case $1 in
            -a|--action)
                ACTION="$2"
                shift 2
                ;;
            -i|--inventory)
                INVENTORY="$2"
                shift 2
                ;;
            -h|--help)
                show_help
                exit 0
                ;;
            *)
                echo "未知选项: $1"
                show_help
                exit 1
                ;;
        esac
    done
}

# 主程序
main() {
    parse_args "$@"
    show_header
    
    # 检查 Ansible
    if ! check_ansible; then
        exit 1
    fi
    
    # 执行操作
    case $ACTION in
        full)
            deploy_full
            ;;
        scenarios)
            deploy_scenarios
            ;;
        reset)
            reset_scenarios
            ;;
        status)
            show_status
            ;;
        *)
            echo -e "${RED}未知操作: $ACTION${NC}"
            show_help
            exit 1
            ;;
    esac
    
    echo ""
    echo -e "${CYAN}========================================${NC}"
    echo -e "${GREEN}完成! 连接到集群: ssh ubuntu@192.168.56.10${NC}"
    echo -e "${CYAN}========================================${NC}"
}

main "$@"
