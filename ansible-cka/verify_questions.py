#!/usr/bin/env python3
"""
CKA 题目环境验证脚本
检查各题目的环境是否正确部署
"""

import subprocess
import json
import sys

# 集群配置
CLUSTERS = {
    "cluster1": {
        "host": "11.0.1.100",
        "questions": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 14],
        "ssh_user": "root",
        "ssh_key": "~/.ssh/id_rsa"
    },
    "cluster2": {
        "host": "11.0.1.101", 
        "questions": [13],
        "ssh_user": "root",
        "ssh_key": "~/.ssh/id_rsa"
    },
    "cluster3": {
        "host": "11.0.1.102",
        "questions": [15, 16],
        "ssh_user": "root",
        "ssh_key": "~/.ssh/id_rsa"
    }
}

# 题目配置
QUESTIONS = {
    1: {"name": "HPA autoscale", "namespace": "autoscale", "user": "cka000001", 
        "checks": ["namespace", "deployment", "user"]},
    2: {"name": "Ingress", "namespace": "sound-repeater", "user": "cka000002",
        "checks": ["namespace", "deployment", "service", "user"]},
    3: {"name": "Sidecar", "namespace": "default", "user": "cka000003",
        "checks": ["namespace", "deployment", "user"]},
    4: {"name": "StorageClass", "namespace": "one", "user": "cka000004",
        "checks": ["namespace", "user"]},
    5: {"name": "Service", "namespace": "spline-reticulator", "user": "cka000005",
        "checks": ["namespace", "deployment", "user"]},
    6: {"name": "PriorityClass", "namespace": "priority", "user": "cka000006",
        "checks": ["namespace", "deployment", "user"]},
    7: {"name": "Helm ArgoCD", "namespace": "argocd", "user": "cka000007",
        "checks": ["namespace", "user"]},
    8: {"name": "PVC", "namespace": "mariadb", "user": "cka000008",
        "checks": ["namespace", "user"]},
    9: {"name": "Gateway", "namespace": "default", "user": "cka000009",
        "checks": ["namespace", "ingress", "gatewayclass", "user"]},
    10: {"name": "NetworkPolicy", "namespace": "frontend", "user": "cka000010",
        "checks": ["namespace", "deployment", "user"], "extra_ns": ["backend"]},
    11: {"name": "CRD cert-manager", "namespace": "cert-manager", "user": "cka000011",
        "checks": ["namespace", "user"]},
    12: {"name": "ConfigMap TLS", "namespace": "nginx-static", "user": "cka000012",
        "checks": ["namespace", "deployment", "configmap", "secret", "user"]},
    13: {"name": "Calico", "namespace": "default", "user": "cka000013",
        "checks": ["namespace", "user"], "no_cni": True},
    14: {"name": "Resources", "namespace": "relative-fawn", "user": "cka000014",
        "checks": ["namespace", "deployment", "user"]},
    15: {"name": "etcd troubleshoot", "namespace": "default", "user": "cka000015",
        "checks": ["namespace", "user"], "broken": True},
    16: {"name": "cri-dockerd", "namespace": "default", "user": "cka000016",
        "checks": ["namespace", "user"], "score_user": True},
}

def run_ssh_command(host, user, key, command):
    """在远程主机上执行命令"""
    ssh_cmd = [
        "ssh", "-o", "StrictHostKeyChecking=no", "-o", "ConnectTimeout=10",
        "-i", key, f"{user}@{host}", command
    ]
    try:
        result = subprocess.run(ssh_cmd, capture_output=True, text=True, timeout=30)
        return result.returncode == 0, result.stdout, result.stderr
    except Exception as e:
        return False, "", str(e)

def check_namespace(host, user, key, namespace):
    """检查 namespace 是否存在"""
    cmd = f"kubectl get namespace {namespace} -o name"
    success, stdout, stderr = run_ssh_command(host, user, key, cmd)
    return success and f"namespace/{namespace}" in stdout

def check_deployment(host, user, key, namespace):
    """检查 deployment 是否存在"""
    cmd = f"kubectl get deployment -n {namespace} -o name"
    success, stdout, stderr = run_ssh_command(host, user, key, cmd)
    return success and "deployment/" in stdout

def check_service(host, user, key, namespace):
    """检查 service 是否存在"""
    cmd = f"kubectl get service -n {namespace} -o name"
    success, stdout, stderr = run_ssh_command(host, user, key, cmd)
    return success and "service/" in stdout

def check_configmap(host, user, key, namespace, name=None):
    """检查 configmap 是否存在"""
    cmd = f"kubectl get configmap -n {namespace} -o name"
    if name:
        cmd = f"kubectl get configmap {name} -n {namespace} -o name"
    success, stdout, stderr = run_ssh_command(host, user, key, cmd)
    return success and "configmap/" in stdout

def check_secret(host, user, key, namespace, name=None):
    """检查 secret 是否存在"""
    cmd = f"kubectl get secret -n {namespace} -o name"
    if name:
        cmd = f"kubectl get secret {name} -n {namespace} -o name"
    success, stdout, stderr = run_ssh_command(host, user, key, cmd)
    return success and "secret/" in stdout

def check_ingress(host, user, key, namespace):
    """检查 ingress 是否存在"""
    cmd = f"kubectl get ingress -n {namespace} -o name"
    success, stdout, stderr = run_ssh_command(host, user, key, cmd)
    return success and "ingress/" in stdout

def check_gatewayclass(host, user, key):
    """检查 gatewayclass 是否存在"""
    cmd = "kubectl get gatewayclass -o name"
    success, stdout, stderr = run_ssh_command(host, user, key, cmd)
    return success and "gatewayclass/" in stdout

def check_system_user(host, user, key, student_user):
    """检查系统用户是否存在"""
    cmd = f"id {student_user}"
    success, stdout, stderr = run_ssh_command(host, user, key, cmd)
    return success

def check_ssh_login(host, student_user, ssh_key_path):
    """检查 SSH 登录是否正常"""
    cmd = "whoami"
    ssh_cmd = [
        "ssh", "-o", "StrictHostKeyChecking=no", "-o", "ConnectTimeout=10",
        "-i", ssh_key_path, f"{student_user}@{host}", cmd
    ]
    try:
        result = subprocess.run(ssh_cmd, capture_output=True, text=True, timeout=15)
        return result.returncode == 0 and student_user in result.stdout
    except Exception as e:
        return False

def check_node_status(host, user, key):
    """检查节点状态"""
    cmd = "kubectl get nodes -o json"
    success, stdout, stderr = run_ssh_command(host, user, key, cmd)
    if not success:
        return False, f"无法获取节点状态: {stderr}"
    try:
        data = json.loads(stdout)
        nodes = data.get("items", [])
        status_list = []
        for node in nodes:
            name = node["metadata"]["name"]
            conditions = node.get("status", {}).get("conditions", [])
            ready = any(c.get("type") == "Ready" and c.get("status") == "True" for c in conditions)
            status_list.append((name, ready))
        return True, status_list
    except Exception as e:
        return False, str(e)

def verify_question(q_num, cluster_name, cluster_config):
    """验证单个题目环境"""
    q_config = QUESTIONS.get(q_num, {})
    host = cluster_config["host"]
    user = cluster_config["ssh_user"]
    key = cluster_config["ssh_key"]
    
    results = {
        "question": q_num,
        "name": q_config.get("name", ""),
        "namespace": q_config.get("namespace", ""),
        "user": q_config.get("user", ""),
        "checks": {},
        "errors": []
    }
    
    checks = q_config.get("checks", [])
    namespace = q_config.get("namespace")
    
    # 检查 namespace
    if "namespace" in checks:
        ok = check_namespace(host, user, key, namespace)
        results["checks"]["namespace"] = ok
        if not ok:
            results["errors"].append(f"Namespace {namespace} 不存在")
    
    # 检查 deployment
    if "deployment" in checks:
        ok = check_deployment(host, user, key, namespace)
        results["checks"]["deployment"] = ok
        if not ok:
            results["errors"].append(f"Deployment 不存在")
    
    # 检查 service
    if "service" in checks:
        ok = check_service(host, user, key, namespace)
        results["checks"]["service"] = ok
        if not ok:
            results["errors"].append(f"Service 不存在")
    
    # 检查 configmap
    if "configmap" in checks:
        if q_num == 12:
            ok = check_configmap(host, user, key, namespace, "nginx-config")
        else:
            ok = check_configmap(host, user, key, namespace)
        results["checks"]["configmap"] = ok
        if not ok:
            results["errors"].append(f"ConfigMap 不存在")
    
    # 检查 secret
    if "secret" in checks:
        if q_num == 12:
            ok = check_secret(host, user, key, namespace, "nginx-tls-secret")
        else:
            ok = check_secret(host, user, key, namespace)
        results["checks"]["secret"] = ok
        if not ok:
            results["errors"].append(f"Secret 不存在")
    
    # 检查 ingress
    if "ingress" in checks:
        ok = check_ingress(host, user, key, namespace)
        results["checks"]["ingress"] = ok
        if not ok:
            results["errors"].append(f"Ingress 不存在")
    
    # 检查 gatewayclass
    if "gatewayclass" in checks:
        ok = check_gatewayclass(host, user, key)
        results["checks"]["gatewayclass"] = ok
        if not ok:
            results["errors"].append(f"GatewayClass 不存在")
    
    # 检查系统用户
    if "user" in checks:
        student_user = q_config.get("user")
        ok = check_system_user(host, user, key, student_user)
        results["checks"]["system_user"] = ok
        if not ok:
            results["errors"].append(f"系统用户 {student_user} 不存在")
    
    return results

def main():
    print("=" * 80)
    print("CKA 题目环境验证报告")
    print("=" * 80)
    
    all_results = []
    
    for cluster_name, cluster_config in CLUSTERS.items():
        print(f"\n【{cluster_name.upper()} - {cluster_config['host']}】")
        print("-" * 60)
        
        # 检查节点状态
        host = cluster_config["host"]
        user = cluster_config["ssh_user"]
        key = cluster_config["ssh_key"]
        
        node_ok, node_info = check_node_status(host, user, key)
        if node_ok:
            print("节点状态:")
            if isinstance(node_info, list):
                for node_name, ready in node_info:
                    status = "✓ Ready" if ready else "✗ NotReady"
                    print(f"  {node_name}: {status}")
            else:
                print(f"  错误: {node_info}")
        else:
            print(f"节点状态: 无法获取 ({node_info})")
        
        # 检查各题目
        for q_num in cluster_config["questions"]:
            result = verify_question(q_num, cluster_name, cluster_config)
            all_results.append(result)
            
            q_name = result["name"]
            errors = result["errors"]
            
            if errors:
                status = "✗ 有问题"
                print(f"\n  Q{q_num:02d} ({q_name}): {status}")
                for err in errors:
                    print(f"    - {err}")
            else:
                status = "✓ OK"
                print(f"  Q{q_num:02d} ({q_name}): {status}")
    
    # 检查 SSH 登录
    print("\n" + "=" * 80)
    print("SSH 登录验证")
    print("-" * 60)
    
    # 检查 CKA SSH 密钥是否存在
    import os
    cka_key = os.path.expanduser("/root/.ssh/cka_ed25519")
    if not os.path.exists(cka_key):
        print(f"  ✗ CKA SSH 密钥不存在: {cka_key}")
        print(f"    请先部署环境生成密钥")
    else:
        print(f"  ✓ CKA SSH 密钥存在")
        
        # 测试几个用户的 SSH 登录
        test_users = [
            ("11.0.1.100", "cka000001"),
            ("11.0.1.100", "cka000012"),
            ("11.0.1.101", "cka000013"),
            ("11.0.1.102", "cka000016"),
        ]
        
        for host, student_user in test_users:
            ok = check_ssh_login(host, student_user, cka_key)
            if ok:
                print(f"  ✓ {student_user}@{host}: 登录成功")
            else:
                print(f"  ✗ {student_user}@{host}: 登录失败")
    
    # 汇总
    print("\n" + "=" * 80)
    print("汇总")
    print("-" * 60)
    
    total = len(all_results)
    ok_count = sum(1 for r in all_results if not r["errors"])
    error_count = total - ok_count
    
    print(f"题目总数: {total}")
    print(f"正常: {ok_count}")
    print(f"有问题: {error_count}")
    
    if error_count > 0:
        print("\n需要修复的题目:")
        for r in all_results:
            if r["errors"]:
                print(f"  - Q{r['question']:02d} ({r['name']}): {', '.join(r['errors'])}")
    
    print("=" * 80)

if __name__ == "__main__":
    main()
