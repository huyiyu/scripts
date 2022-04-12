# wiki js 搭建

## 保存镜像
```bash
# 1. 保存镜像
docker save postgres:11-alpine ghcr.io/requarks/wiki:2 internetsystemsconsortium/bind9:9.18 > wiki.tar
# 2. 如果仍然太大可以用gzip压缩 可压缩到大概195M 
gzip -9 wiki.tar
```
## 导入镜像
```bash
docker load < wiki.tar.gz
```
## 新建数据目录并启动
```bash
mkdir -p ./data/db
mkdir -p ./config/bind9
cat <<EOF >> ./config/bind9/db.itjinfuyun.com
$TTL 1D
@       IN SOA  jinfuyun.com. email.com. (
                                        0       ; serial
                                        1D      ; refresh
                                        1H      ; retry
                                        1W      ; expire
                                        3H )    ; minimum
;
@ IN	NS	localhost.
wiki   IN  A     121.5.50.204
EOF

cat <<EOF >> ./config/bind9/named.conf.default-zones
zone "." {
	type hint;
	file "/usr/share/dns/root.hints";
};

// be authoritative for the localhost forward and reverse zones, and for
// broadcast zones as per RFC 1912

zone "localhost" {
	type master;
	file "/etc/bind/db.local";
};

zone "127.in-addr.arpa" {
	type master;
	file "/etc/bind/db.127";
};

zone "0.in-addr.arpa" {
	type master;
	file "/etc/bind/db.0";
};

zone "255.in-addr.arpa" {
	type master;
	file "/etc/bind/db.255";
};


zone "itjinfuyun.com" {
	type master;
	file "/etc/bind/db.itjinfuyun.com";
};
EOF

cat <<EOF >> named.conf.options
options {
	directory "/var/cache/bind";
	dnssec-validation auto;
	listen-on-v6 { any; };
        listen-on port 53{any;};
        allow-recursion {
                none;
        };
        allow-transfer {
                none;
        };
        allow-update {
                none;
        };
        allow-query     { any; }; 
};
EOF
docker-compose up -d
```
## 访问页面
### 设置 admin 账号密码
![setup](./setup.png)

* admin 邮箱:admin@jfy.com
* 密码: hc1w0U1k4%BY6%mU%Gq4HVf
* 主站域名: http://wiki.fjjfypt.com
### 登陆
![login](./login.png)
### 设置语言和搜索引擎
![home](./home.png)
![language](./language.png)
![search](./search.png)
## 备份
* postgreSql 的数据目录 data/db 的迁移
* 使用postgres 提供的[pg_dump]()

```bash
# 备份数据 类似mysqldump
docker-compose exec db pg_dump -f /var/lib/postgresql/data/backup.sql -U wikijs  wiki
# 还原数据
docker exec -i postgresql psql -U wikijs  wiki data/backup.sql
```