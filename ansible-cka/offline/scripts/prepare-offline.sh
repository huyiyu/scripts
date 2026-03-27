#!/bin/bash
# prepare-offline.sh - 一键准备 Kubernetes 离线部署资源
# 版本号从 ansible-k8s/group_vars/all.yml 读取

set -e

# 获取脚本目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="${SCRIPT_DIR}/../.."
GROUP_VARS_FILE="${PROJECT_ROOT}/ansible-k8s/inventory/group_vars/all.yml"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

# 日志函数（直接输出到stdout）
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[OK]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_step() {
    echo -e "${CYAN}[STEP $1/5]${NC} $2"
}

# 是否强制重新下载
FORCE_DOWNLOAD=false

# 解析命令行参数
parse_args() {
    while [[ $# -gt 0 ]]; do
        case "$1" in
            --force|-f)
                FORCE_DOWNLOAD=true
                log_warn "强制重新下载模式"
                shift
                ;;
            *)
                shift
                ;;
        esac
    done
}

# 下载函数（幂等性：文件存在则跳过）
download_file() {
    local url="$1"
    local filename="$2"
    local description="$3"
    
    if [ -z "$filename" ]; then
        filename=$(basename "$url")
    fi
    
    # 检查文件是否已存在（非空文件）
    if [ -f "$filename" ] && [ -s "$filename" ] && [ "$FORCE_DOWNLOAD" = false ]; then
        local size=$(du -h "$filename" 2>/dev/null | cut -f1)
        log_info "${description} 已存在 (${size})，跳过下载"
        return 0
    fi
    
    log_info "下载 ${description}..."
    
    if wget -q "$url" -O "$filename" 2>/dev/null; then
        local size=$(du -h "$filename" 2>/dev/null | cut -f1)
        log_success "${description} 下载完成 (${size})"
        return 0
    else
        log_error "${description} 下载失败"
        rm -f "$filename"
        return 1
    fi
}

# 检查 group_vars 文件
if [ ! -f "${GROUP_VARS_FILE}" ]; then
    log_error "找不到 group_vars 文件: ${GROUP_VARS_FILE}"
    exit 1
fi

# 从 YAML 读取版本号（使用 grep + sed 简单解析）
read_var() {
    local var_name="$1"
    local default_value="${2:-}"
    local value=$(grep -E "^${var_name}:" "${GROUP_VARS_FILE}" | head -1 | sed -E 's/^[^:]+:[[:space:]]*"?([^"]+)"?$/\1/')
    echo "${value:-$default_value}"
}

# 读取版本号
K8S_VERSION=$(read_var "k8s_version" "1.34.3")
CONTAINERD_VERSION=$(read_var "containerd_version" "2.2.1")
RUNC_VERSION=$(read_var "runc_version" "1.3.4")
CNI_PLUGINS_VERSION=$(read_var "cni_plugins_version" "1.8.0")
PAUSE_VERSION=$(read_var "pause_version" "3.10.1")
CALICO_VERSION=$(read_var "calico_version" "v3.30.6")
CALICO_OPERATOR_VERSION=$(read_var "calico_operator_version" "v1.38.11")
ETCD_VERSION=$(read_var "etcd_version" "3.5.26")
COREDNS_VERSION=$(read_var "coredns_version" "v1.12.1")

# 派生变量
KUBERNETES_VERSION_FULL="v${K8S_VERSION}"
K8S_VERSION_MAJOR_MINOR="${K8S_VERSION%.*}"
K8S_IMAGES_PACKAGE="k8s-images-v${K8S_VERSION}.tar"
CALICO_IMAGES_PACKAGE="calico-images-${CALICO_VERSION}.tar"
CONTAINERD_PACKAGE="containerd-${CONTAINERD_VERSION}-linux-amd64.tar.gz"
CNI_PLUGINS_PACKAGE="cni-plugins-linux-amd64-v${CNI_PLUGINS_VERSION}.tgz"

# 目录设置
PACKAGES_DIR="${SCRIPT_DIR}/../packages"
IMAGES_DIR="${SCRIPT_DIR}/../images"

# 显示版本信息
echo ""
echo "========================================"
echo -e "${CYAN}Kubernetes 离线资源准备工具${NC}"
echo "========================================"
echo ""
echo ""
echo "版本配置 (来自 group_vars/all.yml):"
echo "  Kubernetes: ${K8S_VERSION}"
echo "  Containerd: ${CONTAINERD_VERSION}"
echo "  Calico: ${CALICO_VERSION}"
echo "  Calico Operator: ${CALICO_OPERATOR_VERSION}"
echo ""

# 检查环境
check_env() {
    log_info "检查环境..."
    
    if [ ! -f /etc/debian_version ]; then
        log_error "需要在 Ubuntu/Debian 系统上运行"
        exit 1
    fi
    
    for cmd in curl wget tar docker; do
        if ! command -v $cmd &> /dev/null; then
            log_warn "缺少命令: $cmd"
        fi
    done
    
    log_success "环境检查通过"
}

# 创建目录
mkdir -p "${PACKAGES_DIR}"/containerd
mkdir -p "${PACKAGES_DIR}"/k8s
mkdir -p "${PACKAGES_DIR}"/cni
mkdir -p "${PACKAGES_DIR}"/calico
mkdir -p "${IMAGES_DIR}"

# 下载 Containerd 组件
download_containerd() {
    log_step "1" "下载 Containerd 组件"
    cd "${PACKAGES_DIR}/containerd"
    
    download_file \
        "https://github.com/containerd/containerd/releases/download/v${CONTAINERD_VERSION}/${CONTAINERD_PACKAGE}" \
        "" \
        "containerd ${CONTAINERD_VERSION}"
    
    download_file \
        "https://github.com/opencontainers/runc/releases/download/v${RUNC_VERSION}/runc.amd64" \
        "" \
        "runc ${RUNC_VERSION}"
    
    download_file \
        "https://raw.githubusercontent.com/containerd/containerd/main/containerd.service" \
        "" \
        "containerd.service"
}

# 下载工具（crictl, helm, cri-dockerd）
download_tools() {
    log_step "2" "下载工具（crictl, helm, cri-dockerd）"
    cd "${PACKAGES_DIR}/k8s"
    
    download_file \
        "https://github.com/kubernetes-sigs/cri-tools/releases/download/v1.34.0/crictl-v1.34.0-linux-amd64.tar.gz" \
        "" \
        "crictl v1.34.0"
    
    download_file \
        "https://get.helm.sh/helm-v3.18.4-linux-amd64.tar.gz" \
        "" \
        "helm v3.18.4"
    
    # 下载 cri-dockerd（用于 CKA Q16）- 使用 debian-bookworm 版本兼容 Ubuntu 24.04
    download_file \
        "https://github.com/Mirantis/cri-dockerd/releases/download/v0.3.24/cri-dockerd_0.3.24.3-0.debian-bookworm_amd64.deb" \
        "" \
        "cri-dockerd v0.3.24"
}

# 下载 Kubernetes 二进制文件（kubelet, kubectl, kubeadm）
download_k8s_binaries() {
    log_step "3" "下载 Kubernetes 二进制文件"
    cd "${PACKAGES_DIR}/k8s"
    
    download_file \
        "https://dl.k8s.io/release/${KUBERNETES_VERSION_FULL}/bin/linux/amd64/kubelet" \
        "" \
        "kubelet ${KUBERNETES_VERSION_FULL}"
    
    download_file \
        "https://dl.k8s.io/release/${KUBERNETES_VERSION_FULL}/bin/linux/amd64/kubectl" \
        "" \
        "kubectl ${KUBERNETES_VERSION_FULL}"
    
    download_file \
        "https://dl.k8s.io/release/${KUBERNETES_VERSION_FULL}/bin/linux/amd64/kubeadm" \
        "" \
        "kubeadm ${KUBERNETES_VERSION_FULL}"
    
    # 添加执行权限
    chmod +x kubelet kubectl kubeadm 2>/dev/null || true
    log_success "已添加执行权限 (kubelet, kubectl, kubeadm)"
}

# 下载 CNI 插件
download_cni() {
    log_step "4" "下载 CNI 插件"
    cd "${PACKAGES_DIR}/cni"
    
    download_file \
        "https://github.com/containernetworking/plugins/releases/download/v${CNI_PLUGINS_VERSION}/${CNI_PLUGINS_PACKAGE}" \
        "" \
        "CNI plugins ${CNI_PLUGINS_VERSION}"
}

# 下载 Calico Operator
download_calico() {
    log_step "5" "下载 Calico Operator"
    cd "${PACKAGES_DIR}/calico"
    
    # 下载 Tigera Operator
    download_file \
        "https://raw.githubusercontent.com/projectcalico/calico/${CALICO_VERSION}/manifests/tigera-operator.yaml" \
        "" \
        "tigera-operator ${CALICO_OPERATOR_VERSION}"
    
    # 下载默认 custom-resources（作为参考）
    download_file \
        "https://raw.githubusercontent.com/projectcalico/calico/${CALICO_VERSION}/manifests/custom-resources.yaml" \
        "custom-resources-reference.yaml" \
        "custom-resources (参考)"
    
    log_info "使用自定义 custom-resources.yaml 进行配置"
}



# 导出镜像（幂等性：检查 tar 包是否已存在）
export_images() {
    echo ""
    echo "========================================"
    log_info "导出容器镜像"
    echo "========================================"
    
    cd "${IMAGES_DIR}"
    
    # 检查 docker
    if ! command -v docker &> /dev/null; then
        log_warn "未找到 Docker，跳过镜像导出"
        return
    fi
    
    # Kubernetes 镜像
    log_info "导出 Kubernetes 镜像..."
    
    # 检查镜像包是否已存在
    if [ -f "${K8S_IMAGES_PACKAGE}" ] && [ "$FORCE_DOWNLOAD" = false ]; then
        local size=$(du -h "${K8S_IMAGES_PACKAGE}" 2>/dev/null | cut -f1)
        log_info "Kubernetes 镜像包已存在 (${size})，跳过导出"
    else
        K8S_IMAGES=(
            "registry.k8s.io/kube-apiserver:${KUBERNETES_VERSION_FULL}"
            "registry.k8s.io/kube-controller-manager:${KUBERNETES_VERSION_FULL}"
            "registry.k8s.io/kube-scheduler:${KUBERNETES_VERSION_FULL}"
            "registry.k8s.io/kube-proxy:${KUBERNETES_VERSION_FULL}"
            "registry.k8s.io/pause:${PAUSE_VERSION}"
            "registry.k8s.io/etcd:${ETCD_VERSION}"
            "registry.k8s.io/coredns/coredns:${COREDNS_VERSION}"
        )
        
        for img in "${K8S_IMAGES[@]}"; do
            log_info "拉取 ${img}..."
            docker pull "${img}" >/dev/null 2>&1 || log_warn "拉取 ${img} 失败"
        done
        
        log_info "保存 Kubernetes 镜像包..."
        docker save "${K8S_IMAGES[@]}" -o "${K8S_IMAGES_PACKAGE}" 2>/dev/null && \
            log_success "Kubernetes 镜像包保存完成" || \
            log_warn "Kubernetes 镜像包保存失败"
    fi
    
    # Calico 镜像（Operator 方式部署所需的所有镜像）
    log_info "导出 Calico 镜像（含 Operator）..."
    
    # 检查镜像包是否已存在
    if [ -f "${CALICO_IMAGES_PACKAGE}" ] && [ "$FORCE_DOWNLOAD" = false ]; then
        local size=$(du -h "${CALICO_IMAGES_PACKAGE}" 2>/dev/null | cut -f1)
        log_info "Calico 镜像包已存在 (${size})，跳过导出"
    else
        # Operator 部署 Calico 时使用的所有镜像（quay.io 镜像）
        CALICO_IMAGES=(
            # Operator
            "quay.io/tigera/operator:${CALICO_OPERATOR_VERSION}"
            # Calico 核心组件（Operator 会从 quay.io 拉取）
            "quay.io/calico/node:${CALICO_VERSION}"
            "quay.io/calico/cni:${CALICO_VERSION}"
            "quay.io/calico/kube-controllers:${CALICO_VERSION}"
            "quay.io/calico/typha:${CALICO_VERSION}"
            # CSI 相关组件
            "quay.io/calico/csi:${CALICO_VERSION}"
            "quay.io/calico/node-driver-registrar:${CALICO_VERSION}"
            # FlexVolume 驱动
            "quay.io/calico/pod2daemon-flexvol:${CALICO_VERSION}"
        )
        
        for img in "${CALICO_IMAGES[@]}"; do
            log_info "拉取 ${img}..."
            docker pull "${img}" >/dev/null 2>&1 || log_warn "拉取 ${img} 失败"
        done
        
        log_info "保存 Calico 镜像包..."
        docker save "${CALICO_IMAGES[@]}" -o "${CALICO_IMAGES_PACKAGE}" 2>/dev/null && \
            log_success "Calico 镜像包保存完成" || \
            log_warn "Calico 镜像包保存失败"
    fi
    
    # CKA 题目镜像（Traefik, local-path-provisioner 等）
    log_info "导出 CKA 题目镜像..."
    
    CKA_IMAGES_PACKAGE="cka-images.tar"
    if [ -f "${CKA_IMAGES_PACKAGE}" ] && [ "$FORCE_DOWNLOAD" = false ]; then
        local size=$(du -h "${CKA_IMAGES_PACKAGE}" 2>/dev/null | cut -f1)
        log_info "CKA 题目镜像包已存在 (${size})，跳过导出"
    else
        CKA_IMAGES=(
            # 基础组件
            "rancher/local-path-provisioner:v0.0.24"
            "docker.io/traefik:v2.10.7"
            # ArgoCD 相关 (Q7)
            "quay.io/argoproj/argocd:v2.13.0"
            "ghcr.io/dexidp/dex:v2.41.1"
            "public.ecr.aws/docker/library/redis:7.4.1-alpine"
            # Gateway API (Q9)
            "ghcr.io/nginx/nginx-gateway-fabric:2.4.2"
            "ghcr.io/nginx/nginx-gateway-fabric/nginx:2.4.2"
            # cert-manager (Q11)
            "quay.io/jetstack/cert-manager-controller:v1.13.0"
            "quay.io/jetstack/cert-manager-cainjector:v1.13.0"
            "quay.io/jetstack/cert-manager-webhook:v1.13.0"
            # 题目镜像 (阿里云镜像仓库)
            "registry.cn-hangzhou.aliyuncs.com/fizz_1024/cka:echoserver"
            "docker.io/library/httpd:latest"
            # Q12 ConfigMap TLS
            "docker.io/library/nginx:alpine"
            # Q14 WordPress Resources
            "docker.io/library/wordpress:latest"
            # Q3 Sidecar (busybox stable版本作为备选)
            "docker.io/library/busybox:stable"
        )
        
        for img in "${CKA_IMAGES[@]}"; do
            log_info "拉取 ${img}..."
            docker pull "${img}" >/dev/null 2>&1 || log_warn "拉取 ${img} 失败"
        done
        
        log_info "保存 CKA 题目镜像包..."
        docker save "${CKA_IMAGES[@]}" -o "${CKA_IMAGES_PACKAGE}" 2>/dev/null && \
            log_success "CKA 题目镜像包保存完成" || \
            log_warn "CKA 题目镜像包保存失败"
    fi
}

# 显示下载汇总
show_summary() {
    echo ""
    echo "========================================"
    log_success "离线资源准备完成!"
    echo "========================================"
    echo ""
    
    # 显示版本信息
    echo -e "${CYAN}版本信息:${NC}"
    echo "  Kubernetes: ${K8S_VERSION}"
    echo "  Containerd: ${CONTAINERD_VERSION}"
    echo "  Calico: ${CALICO_VERSION}"
    echo "  Calico Operator: ${CALICO_OPERATOR_VERSION}"
    echo ""
    
    # 显示所有下载的文件列表
    echo -e "${CYAN}下载文件列表:${NC}"
    echo ""
    
    # containerd 目录
    echo "  📁 packages/containerd/"
    if [ -d "${PACKAGES_DIR}/containerd" ]; then
        ls -lh "${PACKAGES_DIR}/containerd/" 2>/dev/null | grep -v '^d' | grep -v '^总用量' | grep -v '.gitkeep' | awk '$9 != "" {printf "     📄 %s (%s)\n", $9, $5}'
    fi
    echo ""
    
    # k8s 目录
    echo "  📁 packages/k8s/"
    if [ -d "${PACKAGES_DIR}/k8s" ]; then
        ls -lh "${PACKAGES_DIR}/k8s/" 2>/dev/null | grep -v '^d' | grep -v '^总用量' | grep -v '.gitkeep' | awk '$9 != "" {printf "     📄 %s (%s)\n", $9, $5}'
    fi
    echo ""
    
    # cni 目录
    echo "  📁 packages/cni/"
    if [ -d "${PACKAGES_DIR}/cni" ]; then
        ls -lh "${PACKAGES_DIR}/cni/" 2>/dev/null | grep -v '^d' | grep -v '^总用量' | grep -v '.gitkeep' | awk '$9 != "" {printf "     📄 %s (%s)\n", $9, $5}'
    fi
    echo ""
    
    # calico 目录
    echo "  📁 packages/calico/"
    if [ -d "${PACKAGES_DIR}/calico" ]; then
        ls -lh "${PACKAGES_DIR}/calico/" 2>/dev/null | grep -v '^d' | grep -v '^总用量' | grep -v '.gitkeep' | awk '$9 != "" {printf "     📄 %s (%s)\n", $9, $5}'
    fi
    echo ""
    
    # images 目录
    echo "  📁 images/"
    if [ -d "${IMAGES_DIR}" ]; then
        ls -lh "${IMAGES_DIR}/" 2>/dev/null | grep -v '^d' | grep -v '^总用量' | grep -v '.gitkeep' | awk '$9 != "" {printf "     📄 %s (%s)\n", $9, $5}'
    fi
    echo ""
    
    # 统计汇总
    echo -e "${CYAN}统计汇总:${NC}"
    local total_pkg_count=0
    local total_pkg_size=0
    for dir in containerd k8s cni calico; do
        local dir_path="${PACKAGES_DIR}/${dir}"
        if [ -d "$dir_path" ]; then
            local count=$(find "$dir_path" -type f ! -name '.gitkeep' 2>/dev/null | wc -l)
            total_pkg_count=$((total_pkg_count + count))
        fi
    done
    local img_count=$(find "${IMAGES_DIR}" -type f ! -name '.gitkeep' 2>/dev/null | wc -l)
    
    echo "  软件包: ${total_pkg_count} 个文件"
    echo "  镜像包: ${img_count} 个文件"
    echo "  总大小: $(du -sh "${PACKAGES_DIR}" "${IMAGES_DIR}" 2>/dev/null | awk '{sum+=$1} END {print sum " MB"}' || echo "N/A")"
    echo ""
    
    log_info "离线资源路径: ${SCRIPT_DIR}/../"
    echo ""
    echo "========================================"
    log_success "准备完成！现在可以使用 Ansible 部署到目标节点。"
    echo "========================================"
    echo ""
}

# 主函数
main() {
    parse_args "$@"
    check_env
    download_containerd
    download_tools
    download_k8s_binaries
    download_cni
    download_calico
    export_images
    show_summary
}

# 运行
main "$@"
