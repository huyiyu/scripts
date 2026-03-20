# VMware 虚拟机设置指南

## 快速创建步骤

### 1. 下载 Ubuntu Server ISO

下载 Ubuntu 22.04 LTS (Jammy Jellyfish) Server:
- https://releases.ubuntu.com/22.04/ubuntu-22.04.5-live-server-amd64.iso

### 2. 创建 Control Plane 虚拟机

在 VMware Workstation 中:

1. **创建新虚拟机**
   - 选择 "典型"
   - 安装来源: 选择下载的 ISO
   - 操作系统: Linux > Ubuntu 64-bit

2. **虚拟机配置**
   - 名称: `cka-controlplane`
   - 位置: 选择有足够空间的位置 (建议 50GB+)
   - 处理器: 2 核 (4 核更佳)
   - 内存: 4096 MB (8GB 更佳)
   - 网络: NAT 或 桥接
   - 磁盘: 30GB+，单文件

3. **安装 Ubuntu**
   - 语言: English
   - 键盘: 默认
   - 网络: 使用 DHCP 或配置静态 IP
   - 代理: 跳过
   - 镜像: 默认
   - 磁盘: 使用整个磁盘
   - 用户名: `ubuntu`
   - 密码: 设置你的密码
   - SSH: 勾选安装 OpenSSH Server
   - 其他软件: 不选择

4. **配置静态 IP (推荐)**
   ```bash
   sudo nano /etc/netplan/00-installer-config.yaml
   ```
   
   修改为:
   ```yaml
   network:
     version: 2
     ethernets:
       ens33:
         dhcp4: no
         addresses:
           - 192.168.56.10/24
         gateway4: 192.168.56.1
         nameservers:
           addresses:
             - 8.8.8.8
             - 114.114.114.114
   ```
   
   应用配置:
   ```bash
   sudo netplan apply
   ```

### 3. 创建 Worker 节点虚拟机

**方法1: 克隆虚拟机 (推荐)**

1. 关闭 controlplane 虚拟机
2. 右键点击虚拟机 > 管理 > 克隆
3. 创建完整克隆
4. 命名为 `cka-node01`
5. 修改 IP 地址为 `192.168.56.11`
6. 修改主机名: `sudo hostnamectl set-hostname node01`

**方法2: 重新安装**

重复 Control Plane 的步骤，但使用:
- 名称: `cka-node01`
- 内存: 2048 MB (4GB 更佳)
- IP: `192.168.56.11`

### 4. 配置 SSH 免密登录

在 Windows 宿主机上:

```powershell
# 生成密钥
ssh-keygen -t rsa -b 4096 -C "cka-lab" -f $HOME\.ssh\cka_lab

# 复制公钥到各节点 (使用安装时设置的密码)
ssh-copy-id -i $HOME\.ssh\cka_lab.pub ubuntu@192.168.56.10
ssh-copy-id -i $HOME\.ssh\cka_lab.pub ubuntu@192.168.56.11
ssh-copy-id -i $HOME\.ssh\cka_lab.pub ubuntu@192.168.56.12
```

### 5. 测试连接

```powershell
ssh -i $HOME\.ssh\cka_lab ubuntu@192.168.56.10 "hostname && ip addr show"
```

### 6. 更新 Ansible Inventory

编辑 `C:\work\cka-lab\ansible\inventory\hosts.ini`，确保 IP 地址正确。

## VMware 网络配置

### 配置 VMware NAT 网络

1. 编辑 > 虚拟网络编辑器
2. 选择 VMnet8 (NAT)
3. 点击 "NAT 设置"
4. 确保子网 IP 与你的 IP 规划一致 (如 192.168.56.0)
5. 取消 "使用本地 DHCP 服务" (如果使用静态 IP)

### 配置端口转发 (可选)

如需从宿主机访问 NodePort 服务:

1. 虚拟网络编辑器 > NAT 设置 > 添加
2. 主机端口: 30080
3. 虚拟机 IP: 192.168.56.10
4. 虚拟机端口: 30080

## 快照管理

建议在不同阶段创建快照:

1. **初始安装完成** - 干净的 Ubuntu 系统
2. **Kubernetes 安装完成** - 基础集群就绪
3. **所有场景部署完成** - 完整练习环境

```powershell
# 使用 vmrun 创建快照 (需要 VMware VIX)
vmrun snapshot "C:\VMs\cka-controlplane\cka-controlplane.vmx" "初始安装"
```

## 常见问题

### 虚拟机无法联网

```bash
# 检查网络配置
ip addr show

# 重启网络
sudo systemctl restart systemd-networkd

# 或
sudo netplan apply
```

### SSH 连接失败

```bash
# 在虚拟机内检查 SSH 服务
sudo systemctl status ssh

# 检查防火墙
sudo ufw status
sudo ufw allow ssh
```

### IP 地址冲突

确保所有虚拟机使用不同的 IP 地址:
- controlplane: 192.168.56.10
- node01: 192.168.56.11
- node02: 192.168.56.12

### 磁盘空间不足

```bash
# 查看磁盘使用
df -h

# 清理 apt 缓存
sudo apt clean
```

## 性能优化

### 虚拟机设置

1. **内存**: 控制平面至少 4GB，Worker 至少 2GB
2. **处理器**: 启用虚拟化引擎 (VT-x/AMD-V)
3. **磁盘**: 使用 SSD 存储虚拟机文件
4. **显示**: 关闭 3D 加速 (服务器不需要)

### 宿主机设置

1. 关闭不必要的程序
2. 预留足够的内存给 VMware
3. 将虚拟机文件放在 SSD 上

## 备份和恢复

### 导出虚拟机

文件 > 导出为 OVF:
- 选择虚拟机
- 选择导出位置
- 导出

### 导入虚拟机

文件 > 打开 > 选择 .ovf 文件

## 参考

- [VMware Workstation 文档](https://docs.vmware.com/en/VMware-Workstation-Pro/index.html)
- [Ubuntu Server 安装指南](https://ubuntu.com/tutorials/install-ubuntu-server)
