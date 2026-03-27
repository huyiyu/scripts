#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
CKA 考试评分脚本
"""

import subprocess
import sys
import json
import os
from datetime import datetime
from typing import Dict, List, Tuple, Optional

PASSING_SCORE = 66
TOTAL_SCORE = 100
QUESTION_COUNT = 16
SCORE_PER_QUESTION = TOTAL_SCORE / QUESTION_COUNT

KUBECONFIGS = {
    "cluster1": "/home/cka000016/.kube/cluster1-config",
    "cluster2": "/home/cka000016/.kube/cluster2-config",
    "cluster3": "/etc/kubernetes/admin.conf",
}

class Colors:
    GREEN = '\033[92m'
    RED = '\033[91m'
    YELLOW = '\033[93m'
    BLUE = '\033[94m'
    CYAN = '\033[96m'
    MAGENTA = '\033[95m'
    BOLD = '\033[1m'
    DIM = '\033[2m'
    END = '\033[0m'

def run_kubectl(cluster: str, args: str, ignore_error: bool = False) -> Tuple[bool, str]:
    kubeconfig = KUBECONFIGS.get(cluster)
    if not kubeconfig or not os.path.exists(kubeconfig):
        return False, f"Kubeconfig not found: {kubeconfig}"
    cmd = f"KUBECONFIG={kubeconfig} kubectl {args}"
    try:
        result = subprocess.run(cmd, shell=True, capture_output=True, text=True, timeout=30)
        if result.returncode == 0:
            return True, result.stdout
        if ignore_error:
            return True, result.stdout + result.stderr
        return False, result.stderr
    except subprocess.TimeoutExpired:
        return False, "Command timeout"
    except Exception as e:
        return False, str(e)

def check_deployment(cluster: str, namespace: str, name: str) -> Tuple[bool, str]:
    success, output = run_kubectl(cluster, f"get deployment {name} -n {namespace} -o json")
    if not success:
        return False, f"Deployment {name} 不存在"
    try:
        data = json.loads(output)
        ready = data.get('status', {}).get('readyReplicas', 0)
        desired = data.get('spec', {}).get('replicas', 1)
        if ready >= desired:
            return True, f"Deployment {name} 运行正常 ({ready}/{desired})"
        return False, f"Deployment {name} 未就绪 ({ready}/{desired})"
    except:
        return False, f"解析 Deployment 状态失败"

def check_service(cluster: str, namespace: str, name: str) -> Tuple[bool, str]:
    success, _ = run_kubectl(cluster, f"get service {name} -n {namespace}")
    if success:
        return True, f"Service {name} 存在"
    return False, f"Service {name} 不存在"

def check_ingress(cluster: str, namespace: str, name: str) -> Tuple[bool, str]:
    success, _ = run_kubectl(cluster, f"get ingress {name} -n {namespace}")
    if success:
        return True, f"Ingress {name} 存在"
    return False, f"Ingress {name} 不存在"

def check_hpa(cluster: str, namespace: str, name: str) -> Tuple[bool, str]:
    success, output = run_kubectl(cluster, f"get hpa {name} -n {namespace} -o json")
    if not success:
        return False, f"HPA {name} 不存在"
    try:
        data = json.loads(output)
        spec = data.get('spec', {})
        min_replicas = spec.get('minReplicas')
        max_replicas = spec.get('maxReplicas')
        metrics = spec.get('metrics', [])
        cpu_percent = None
        for m in metrics:
            if m.get('type') == 'Resource' and m.get('resource', {}).get('name') == 'cpu':
                cpu_percent = m.get('resource', {}).get('target', {}).get('averageUtilization')
        if min_replicas == 1 and max_replicas == 4 and cpu_percent == 50:
            return True, f"HPA 配置正确 (min={min_replicas}, max={max_replicas}, cpu={cpu_percent}%)"
        return False, f"HPA 配置错误 (min={min_replicas}, max={max_replicas}, cpu={cpu_percent}%)"
    except:
        return False, "解析 HPA 配置失败"

def check_pod_running(cluster: str, namespace: str, name: str) -> Tuple[bool, str]:
    success, output = run_kubectl(cluster, f"get pod -n {namespace} -o json")
    if not success:
        return False, f"无法获取 Pod 列表"
    try:
        data = json.loads(output)
        for pod in data.get('items', []):
            pod_name = pod.get('metadata', {}).get('name', '')
            if name in pod_name:
                phase = pod.get('status', {}).get('phase', '')
                if phase == 'Running':
                    return True, f"Pod {pod_name} 运行中"
                return False, f"Pod {pod_name} 状态: {phase}"
        return False, f"Pod {name} 不存在"
    except:
        return False, f"解析 Pod 状态失败"

def check_pod_sidecar(cluster: str, namespace: str, pod_name: str) -> Tuple[bool, str]:
    success, output = run_kubectl(cluster, f"get pod -n {namespace} -o json")
    if not success:
        return False, f"无法获取 Pod 列表"
    try:
        data = json.loads(output)
        for pod in data.get('items', []):
            if pod_name in pod.get('metadata', {}).get('name', ''):
                containers = pod.get('spec', {}).get('containers', [])
                if len(containers) >= 2:
                    return True, f"Pod 包含 {len(containers)} 个容器 (含 sidecar)"
                return False, f"Pod 只有 {len(containers)} 个容器，缺少 sidecar"
        return False, f"Pod {pod_name} 不存在"
    except:
        return False, f"解析 Pod 配置失败"

def check_log_file(cluster: str, namespace: str, pod_name: str, container: str, log_path: str) -> Tuple[bool, str]:
    success, output = run_kubectl(cluster, f"get pod -n {namespace} -o json")
    if not success:
        return False, f"无法获取 Pod 列表"
    try:
        data = json.loads(output)
        for pod in data.get('items', []):
            if pod_name in pod.get('metadata', {}).get('name', ''):
                exec_cmd = f"exec {pod.get('metadata', {}).get('name')} -n {namespace} -c {container} -- ls {log_path}"
                success, _ = run_kubectl(cluster, exec_cmd, ignore_error=True)
                if success:
                    return True, f"日志文件 {log_path} 存在"
                return False, f"日志文件 {log_path} 不存在"
        return False, f"Pod {pod_name} 不存在"
    except:
        return False, f"检查日志文件失败"

def check_storageclass(cluster: str, name: str, is_default: bool = True) -> Tuple[bool, str]:
    success, output = run_kubectl(cluster, f"get storageclass {name} -o json")
    if not success:
        return False, f"StorageClass {name} 不存在"
    try:
        data = json.loads(output)
        annotations = data.get('metadata', {}).get('annotations', {})
        is_default_sc = annotations.get('storageclass.kubernetes.io/is-default-class') == 'true'
        if is_default and not is_default_sc:
            return False, f"StorageClass {name} 未设置为默认"
        elif is_default and is_default_sc:
            return True, f"StorageClass {name} 存在且为默认"
        return True, f"StorageClass {name} 存在"
    except:
        return False, f"解析 StorageClass 失败"

QUESTIONS = {
    "Q01": {
        "name": "HPA Autoscale",
        "cluster": "cluster1",
        "namespace": "autoscale",
        "checks": [
            lambda c, n: check_deployment(c, n, "apache-server"),
            lambda c, n: check_hpa(c, n, "apache-server"),
        ]
    },
    "Q02": {
        "name": "Ingress",
        "cluster": "cluster1",
        "namespace": "sound-repeater",
        "checks": [
            lambda c, n: check_deployment(c, n, "echoserver"),
            lambda c, n: check_service(c, n, "echoserver-service"),
            lambda c, n: check_ingress(c, n, "echo"),
        ]
    },
    "Q03": {
        "name": "Sidecar",
        "cluster": "cluster1",
        "namespace": "default",
        "checks": [
            lambda c, n: check_pod_running(c, n, "synergy-leverager"),
            lambda c, n: check_pod_sidecar(c, n, "synergy-leverager"),
            lambda c, n: check_log_file(c, n, "synergy-leverager", "synergy-leverager", "/var/log/synergy-leverager.log"),
        ]
    },
    "Q05": {
        "name": "Service",
        "cluster": "cluster1",
        "namespace": "spline-reticulator",
        "checks": [
            lambda c, n: check_deployment(c, n, "front-end"),
            lambda c, n: check_service(c, n, "front-end-svc"),
        ]
    },
}

def print_header():
    width = 70
    print()
    print(f"{Colors.CYAN}{'=' * width}{Colors.END}")
    print(f"{Colors.BOLD}{Colors.CYAN}{'CKA 考试评分系统 (Cluster3)':^{width}}{Colors.END}")
    print(f"{Colors.CYAN}{'=' * width}{Colors.END}")
    print()
    print(f"  {Colors.DIM}考试时间:{Colors.END} {Colors.YELLOW}{datetime.now().strftime('%Y-%m-%d %H:%M:%S')}{Colors.END}")
    print(f"  {Colors.DIM}通过分数:{Colors.END} {Colors.GREEN}{PASSING_SCORE}/{TOTAL_SCORE}{Colors.END}")
    print(f"  {Colors.DIM}题目数量:{Colors.END} {Colors.BLUE}{QUESTION_COUNT} 题{Colors.END}")
    print()

def print_question_result(qid, name, passed, details, max_qid_len=4):
    if passed:
        status = f"{Colors.GREEN}✓ 通过{Colors.END}"
        score = f"{Colors.GREEN}+{SCORE_PER_QUESTION:.1f}{Colors.END}"
    else:
        status = f"{Colors.RED}✗ 未通过{Colors.END}"
        score = f"{Colors.RED}+0{Colors.END}"
    
    remark = ""
    for detail in details:
        if passed and detail.startswith("✓"):
            remark = detail[1:].strip()
            break
        elif not passed and detail.startswith("✗"):
            remark = detail[1:].strip()
            break
    
    if len(remark) > 35:
        remark = remark[:32] + "..."
    
    print(f"  {Colors.BOLD}{qid:<{max_qid_len}}{Colors.END} │ {name:<22} │ {status:<14} │ {score:<10} │ {remark}")

def print_table_header(max_qid_len=4):
    print(f"  {'题目':<{max_qid_len}} │ {'名称':<22} │ {'状态':<14} │ {'得分':<10} │ {'备注'}")
    print(f"  {'─' * (max_qid_len + 1)}┼{'─' * 24}┼{'─' * 16}┼{'─' * 12}┼{'─' * 35}")

def main():
    print_header()
    
    total_score = 0.0
    passed_count = 0
    failed_count = 0
    
    max_qid_len = max(len(qid) for qid in QUESTIONS.keys())
    print_table_header(max_qid_len)
    
    for qid, config in QUESTIONS.items():
        cluster = config["cluster"]
        namespace = config["namespace"]
        name = config["name"]
        checks = config["checks"]
        
        details = []
        all_passed = True
        
        for check in checks:
            try:
                passed, msg = check(cluster, namespace)
                if passed:
                    details.append(f"✓ {msg}")
                else:
                    details.append(f"✗ {msg}")
                    all_passed = False
            except Exception as e:
                details.append(f"✗ 检查异常: {str(e)}")
                all_passed = False
        
        if all_passed:
            total_score += SCORE_PER_QUESTION
            passed_count += 1
        else:
            failed_count += 1
        
        print_question_result(qid, name, all_passed, details, max_qid_len)
    
    print(f"\n{Colors.CYAN}{'-' * 70}{Colors.END}")
    print(f"{Colors.BOLD}{'评分总结':^{70}}{Colors.END}")
    print(f"{Colors.CYAN}{'-' * 70}{Colors.END}")
    print(f"  {Colors.BOLD}总得分:{Colors.END} {Colors.GREEN if total_score >= PASSING_SCORE else Colors.RED}{total_score:.1f}/{TOTAL_SCORE}{Colors.END}")
    print(f"  {Colors.GREEN}● 通过题目:{Colors.END} {passed_count} 题")
    print(f"  {Colors.RED}● 未通过:{Colors.END} {failed_count} 题")
    return 0 if total_score >= PASSING_SCORE else 1

if __name__ == "__main__":
    sys.exit(main())
