# Question Role

用于在 CKA 考试环境中部署题目所需的 Kubernetes 资源。

## 使用方法

```bash
cd ansible-k8s
ansible-playbook -i inventory/hosts.ini playbooks/question.yml --limit cluster1
```

## 支持的题目

### Question 1 - HPA Autoscale (cluster1)

**考生用户**: `cka000001`

**资源**:
- Namespace: `autoscale`
- Deployment: `apache-server`

**要求**: 创建 HPA，目标 CPU 50%，min=1, max=4，scaleDown 稳定窗口 30s

**登录**: `ssh cka000001@11.0.1.100`

---

### Question 2 - Ingress (cluster1)

**考生用户**: `cka000002`

**资源**:
- Namespace: `sound-repeater`
- Deployment: `echoserver`
- Service: `echoserver-service:8080`
- Ingress Controller: Traefik (NodePort 30080)
- 反向代理: Nginx (统一入口)
  - 80 端口 → Traefik NodePort (30080)
- Hosts: `example.org` -> `127.0.0.1`

**要求**: 创建 Ingress `echo`，在 `http://example.org/echo` 公开 `echoserver-service:8080`

**验证**: `curl http://example.org/echo`

**登录**: `ssh cka000002@11.0.1.100`

---

### Question 9 - Gateway (cluster1)

**考生用户**: `cka000009`

**依赖**: 需要 Question 2 基础设施（Traefik + Nginx 反向代理）已部署

**基础环境** (`question9_infra_enabled: true`):
- Namespace: `default`
- **Ingress Controller**: 复用 Question 2 的 Traefik（IngressClass: nginx）
- **反向代理**: Nginx（统一入口，SSL 透传）
  - 80 端口 → Traefik (Ingress)
  - 443 端口 → Gateway (HTTPS，SSL 透传)
- GatewayClass: `nginx`
- Ingress: `web`（主机: ingress.web.k8s.local，需要迁移）
- Service: `web:80`
- Secret: `web-cert`（TLS 证书，由 Gateway 处理）
- Deployment: `web`（nginx 后端）

**要求**: 
- 创建名为 `web-gateway` 的 Gateway，主机名 `gateway.web.k8s.local`
- 创建名为 `web-route` 的 HTTPRoute
- 保持现有 Ingress `web` 的 TLS 配置（使用 web-cert）
- 保持现有路由规则（路径: /, 后端: web:80）
- 最后删除 Ingress `web`

**验证**: 
```bash
kubectl get gateway web-gateway -n default
kubectl get httproute web-route -n default
# 测试访问（通过 Nginx 入口）
curl -k https://gateway.web.k8s.local/
```

**登录**: `ssh cka000009@11.0.1.100`

---

### Question 3 - Sidecar (cluster1)

**考生用户**: `cka000003`

**资源**:
- Namespace: `default`
- Deployment: `synergy-leverager`（含 emptyDir volume，不含 sidecar）

**要求**: 
- 添加 sidecar 容器（busybox:stable）
- 运行命令: `tail -n+1 -f /var/log/synergy-leverager.log`
- 共享 `/var/log` 卷

**验证**: `kubectl logs <pod-name> -c sidecar`

**登录**: `ssh cka000003@11.0.1.100`

---

### Question 4 - StorageClass (cluster1)

**考生用户**: `cka000004`

**资源**:
- local-path-provisioner 已部署
- StorageClass: 需创建 ran-local-path

**要求**: 
- 创建名为 `ran-local-path` 的 StorageClass
- provisioner: `rancher.io/local-path`
- volumeBindingMode: `WaitForFirstConsumer`
- 设置为默认 StorageClass

**登录**: `ssh cka000004@11.0.1.100`

---

### Question 5 - Service (cluster1)

**考生用户**: `cka000005`

**资源**:
- Namespace: `spline-reticulator`
- Deployment: `front-end`（2 个副本，未暴露端口）

**要求**: 
- 更新 front-end Deployment，添加 containerPort: 80
- 创建名为 `front-end-svc` 的 Service
- Service 类型为 NodePort，暴露容器端口 80

**验证**: `kubectl get svc -n spline-reticulator front-end-svc`

**登录**: `ssh cka000005@11.0.1.100`

---

### Question 6 - PriorityClass (cluster1)

**考生用户**: `cka000006`

**资源**:
- Namespace: `priority`
- PriorityClass: `max-user-priority` (value: 1000000000)
- Deployment: `busybox-logger`（未设置 PriorityClass）
- Deployment: `nginx-static`、`redis-cache`（干扰项，不应修改）

**要求**: 
- 创建名为 `high-priority` 的 PriorityClass
- 值设置为比现有最高优先级小一 (999999999)
- 修改 `busybox-logger` Deployment 使用 `high-priority`
- 不要修改其他 Deployment

**验证**: `kubectl get -n priority pod <pod-name> -o jsonpath='{.spec.priority}'`

**登录**: `ssh cka000006@11.0.1.100`

---

### Question 7 - Helm ArgoCD (cluster1)

**考生用户**: `cka000007`

**资源**:
- Namespace: `argocd`
- Helm: 已安装到 `/usr/local/bin/helm`

**要求**: 
- 添加名为 `argo` 的官方 Argo CD Helm 存储库
- 生成 Argo CD Helm 图表版本 7.7.3 的模板，保存到 `~/argo-helm.yaml`
- 配置为不安装 CRDs
- 使用 Helm 安装 Argo CD，发布名称为 `argocd`
- 安装在 `argocd` namespace 中

**验证**: `kubectl get pods -n argocd`

**登录**: `ssh cka000007@11.0.1.100`

---

### Question 8 - PVC (cluster1)

**考生用户**: `cka000008`

**资源**:
- Namespace: `mariadb`
- PersistentVolume: `mariadb-pv` (500Mi, ReadWriteOnce)
- Deployment 模板: `~/mariadb-deployment.yaml`

**要求**: 
- 创建名为 `mariadb` 的 PersistentVolumeClaim (PVC)
  - 访问模式: `ReadWriteOnce`
  - 存储: `250Mi`
  - 必须使用现有的 PV (`mariadb-pv`)
- 编辑 `~/mariadb-deployment.yaml` 添加 volumeMounts 和 volumes
- 应用 Deployment 并确保正常运行

**验证**: `kubectl get pvc -n mariadb` 和 `kubectl get pods -n mariadb`

**登录**: `ssh cka000008@11.0.1.100`

---

### Question 9 - Gateway (cluster1)

**考生用户**: `cka000009`

**资源**:
- Namespace: `default`
- GatewayClass: `nginx`
- Ingress: `web`（需要迁移并删除）
- Service: `web:80`
- Secret: `web-cert`（TLS 证书）

**要求**: 
- 创建名为 `web-gateway` 的 Gateway，主机名 `gateway.web.k8s.local`
- 创建名为 `web-route` 的 HTTPRoute
- 保持现有 Ingress `web` 的 TLS 配置（使用 web-cert）
- 保持现有路由规则（路径: /, 后端: web:80）
- 最后删除 Ingress `web`

**验证**: `kubectl get gateway -n default` 和 `kubectl get httproute -n default`

**登录**: `ssh cka000009@11.0.1.100`

---

## 题目清单

| 题号 | 名称 | 考生用户 | 部署目标 | 状态 |
|------|------|----------|----------|------|
| 1 | HPA Autoscale | cka000001 | cluster1 | ✅ 已完成 |
| 2 | Ingress | cka000002 | cluster1 | ✅ 已完成 |
| 3 | Sidecar | cka000003 | cluster1 | ✅ 已完成 |
| 4 | StorageClass | cka000004 | cluster1 | ✅ 已完成 |
| 5 | Service | cka000005 | cluster1 | ✅ 已完成 |
| 6 | PriorityClass | cka000006 | cluster1 | ✅ 已完成 |
| 7 | Helm ArgoCD | cka000007 | cluster1 | ✅ 已完成 |
| 8 | PVC | cka000008 | cluster1 | ✅ 已完成 |
| 9 | Gateway | cka000009 | cluster1 | ✅ 已完成 |
| 10-16 | 待添加 | - | - | ⏳ 待开发 |

## 配置说明

题目配置位于 `inventory/group_vars/cluster1.yml`：

```yaml
# Question 1
question1_enabled: true
question1_student_user: "cka000001"

# Question 2
question2_enabled: true
question2_student_user: "cka000002"

# Question 3
question3_enabled: true
question3_student_user: "cka000003"

# Question 4
question4_enabled: true
question4_student_user: "cka000004"
question4_infra_enabled: true

# Question 5
question5_enabled: true
question5_student_user: "cka000005"

# Question 6
question6_enabled: true
question6_student_user: "cka000006"

# Question 7
question7_enabled: true
question7_student_user: "cka000007"

# Question 8
question8_enabled: true
question8_student_user: "cka000008"

# Question 9
question9_enabled: true
question9_student_user: "cka000009"
question9_infra_enabled: true  # 依赖 Question 2 的 Traefik
```

**依赖关系说明**:
- Question 2 基础设施 (Traefik) 是 Question 9 的前提条件
- Question 4 基础设施 (local-path-provisioner) 是 Question 8 的前提条件
- 确保先部署基础设施，再部署对应题目

## 参考文档

- CKA 考试题目详解: `/root/cka2026.md`
