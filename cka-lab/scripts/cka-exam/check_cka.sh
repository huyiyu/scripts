#!/bin/sh
# CKA模拟审题自动评分脚本 - 兼容 /bin/sh
# 作者：大平洋馋嘴的菠萝
# 来源：参考环境 /root/check_cka.sh

echo "如api或etcd等损坏无法访问k8s，可能导致无法正确得分。"

GREEN='\033[0;32m'
RED='\033[0;31m'
RESET='\033[0m'

score=0
total=16

check_result() {
  eval "$1"
  if [ $? -eq 0 ]; then
    echo -e "${GREEN}✔ $2${RESET}"
    score=$((score + 1))
  else
    echo -e "${RED}✘ $2${RESET}"
  fi
}

# 1. HPA
check_hpa() {
  echo -e "[题1] HPA"
  hpa=$(kubectl -n autoscale get hpa apache-server --no-headers 2>/dev/null)
  has_scale=$(kubectl -n autoscale get hpa apache-server -o yaml 2>/dev/null | grep -q 'stabilizationWindowSeconds: 30')
  check_result "[ -n \"$hpa\" ] && kubectl -n autoscale get hpa apache-server -o yaml | grep -q 'stabilizationWindowSeconds: 30'" "HPA 存在且缩容窗口设置正确"
}

# 2. Ingress
check_ingress() {
  echo -e "[题2] Ingress"
  host=$(kubectl -n sound-repeater get ingress echo -o jsonpath='{.spec.rules[0].host}' 2>/dev/null)
  svc=$(kubectl -n sound-repeater get ingress echo -o jsonpath='{.spec.rules[0].http.paths[0].backend.service.name}' 2>/dev/null)
  check_result "[ \"$host\" = \"example.org\" ] && [ \"$svc\" = \"echoserver-service\" ]" "Ingress 域名与服务匹配"
}

# 3. Sidecar
check_sidecar() {
  echo -e "[题3] Sidecar"
  containers=$(kubectl get pods -n default -l app=synergy-leverager -o jsonpath='{.items[0].spec.containers[*].name}' 2>/dev/null)
  check_result "echo \"$containers\" | grep -q sidecar" "Sidecar 容器存在"
}

# 4. StorageClass
check_storageclass() {
  echo -e "[题4] StorageClass"
  mode=$(kubectl get sc ran-local-path -o jsonpath='{.volumeBindingMode}' 2>/dev/null)
  default=$(kubectl get sc ran-local-path -o jsonpath='{.metadata.annotations.storageclass\.kubernetes\.io/is-default-class}' 2>/dev/null)
  check_result "[ \"$mode\" = \"WaitForFirstConsumer\" ] && [ \"$default\" = \"true\" ]" "StorageClass 设置正确"
}

# 5. Service
check_service() {
  echo -e "[题5] Service"
  svc_type=$(kubectl -n spline-reticulator get svc front-end-svc -o jsonpath='{.spec.type}' 2>/dev/null)
  port=$(kubectl -n spline-reticulator get svc front-end-svc -o jsonpath='{.spec.ports[0].port}')
  check_result "[ \"$svc_type\" = \"NodePort\" ] && [ \"$port\" = \"80\" ]" "Service 类型和端口正确"
}

# 6. PriorityClass
check_priorityclass() {
  echo -e "[题6] PriorityClass"
  val=$(kubectl get priorityclass high-priority -o jsonpath='{.value}' 2>/dev/null)
  pc=$(kubectl -n priority get deploy busybox-logger -o jsonpath='{.spec.template.spec.priorityClassName}' 2>/dev/null)
  check_result "[ \"$val\" = \"999999999\" ] && [ \"$pc\" = \"high-priority\" ]" "PriorityClass 设置正确"
}

# 7. Argo CD
check_argo_cd() {
  echo -e "[题7] Argo CD"
  pods=$(kubectl -n argocd get pods --no-headers 2>/dev/null | wc -l)
  check_result "[ $pods -gt 0 ]" "Argo CD Pod 正常"
}

# 8. PVC
check_pvc() {
  echo -e "[题8] PVC"
  pvc=$(kubectl -n mariadb get pvc mariadb --no-headers 2>/dev/null)
  dep=$(kubectl -n mariadb get deploy mariadb --no-headers 2>/dev/null)
  check_result "[ -n \"$pvc\" ] && [ -n \"$dep\" ]" "PVC 和 Deployment 存在"
}

# 9. Gateway
check_gateway() {
  echo -e "[题9] Gateway"
  gw=$(kubectl get gateway web-gateway --no-headers 2>/dev/null)
  route=$(kubectl get httproute web-route --no-headers 2>/dev/null)
  ingress=$(kubectl get ingress web 2>/dev/null)
  check_result "[ -n \"$gw\" ] && [ -n \"$route\" ] && [ -z \"$ingress\" ]" "Gateway 迁移成功，Ingress 已删除"
}

# 10. NetworkPolicy
check_netpol() {
  echo -e "[题10] NetworkPolicy"
  kubectl -n backend get netpol |grep app=backend  >/dev/null 2>&1
  check_result "[ $? -eq 0 ]" "已正确应用 netpol2"
}

# 11. CRD
check_crd() {
  echo -e "[题11] CRD"
  resources=$(grep 'cert-manager.io' /home/student/resources.yaml 2>/dev/null)
  subject=$(grep 'subject' /home/student/subject.yaml 2>/dev/null)
  check_result "[ -n \"$resources\" ] && [ -n \"$subject\" ]" "CRD 列表和字段文档生成"
}

# 12. ConfigMap
check_configmap() {
  echo -e "[题12] ConfigMap"
  val=$(kubectl -n nginx-static get cm nginx-config -o yaml 2>/dev/null | grep 'ssl_protocols' | grep -q 'TLSv1.2')
  check_result "kubectl -n nginx-static get cm nginx-config -o yaml | grep -q 'TLSv1.2'" "TLSv1.2 配置生效"
}

# 13. Calico
check_calico() {
  echo -e "[题13] Calico"
  pods=$(kubectl -n kube-system get pod --no-headers 2>/dev/null | wc -l)
  check_result "[ $pods -gt 0 ]" "Calico Pod 存在"
}

# 14. Resources
check_resources() {
  echo -e "[题14] Resources"
  pods=$(kubectl -n relative-fawn get pods -l app=wordpress -o jsonpath='{.items[*].status.phase}' 2>/dev/null)
  check_result "echo \"$pods\" | grep -q Running" "WordPress Pod 正常运行"
}

# 15. etcd
check_etcd() {
  echo -e "[题15] etcd 修复"
  kubectl get nodes 2>/dev/null | grep -q "Ready" && kubectl -n kube-system get pods 2>/dev/null | grep -q "Running"
  check_result "[ $? -eq 0 ]" "etcd 修复成功，集群就绪"
}

# 16. cri-dockerd
check_cridockerd() {
  echo -e "[题16] cri-dockerd"
  systemctl is-active cri-docker >/dev/null 2>&1
  active=$?
  sysctl net.ipv4.ip_forward | grep -q 1
  check_result "[ $active -eq 0 ] && [ $? -eq 0 ]" "cri-dockerd 运行中，内核参数已设置"
}

### 执行全部检查
check_hpa
check_ingress
check_sidecar
check_storageclass
check_service
check_priorityclass
check_argo_cd
check_pvc
check_gateway
check_netpol
check_crd
check_configmap
check_calico
check_resources
check_etcd
check_cridockerd

echo ""
echo "📝 最终得分: $score / $total"

if [ "$score" -ge 11 ]; then
  echo ""
  echo -e "${GREEN}🎉 恭喜考试通过！${RESET}"
else
  echo ""
  echo -e "${RED}❌ 成绩不合格，请继续努力！${RESET}"
fi
