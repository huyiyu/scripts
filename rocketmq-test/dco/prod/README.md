# rocketmq controller 主备切换集群搭建

1. namesrv 无状态,不需要考虑启动顺序,直接启动即可
2. controller 要先于broker启动, 可以内嵌nameserv启动 也可以独立部署,x86架构下可采用Jraft作为底层依赖，设置奇数台启动
3. 启动后可通过 `./mqadmin getControllerMetaData -a namesrv3:9770` 校验controller 选举结果
```bash
# 如下 leader 是namesrv3
#ControllerGroup	jRaft-Controller
#ControllerLeaderId	namesrv3:9880
#ControllerLeaderAddress	namesrv3:9770
#Peer:	namesrv1:9770
#Peer:	namesrv2:9770
#Peer:	namesrv3:9770
 ```
4. 大部分场景推荐使用jraft内核代替dledger,非x86架构使用Dledger
5. 同一个业务范围broker 配置文件要保证完全一致,每个broker 要提供单独的数据盘。
6. 等待所有broker 启动完成后使用`./mqadmin getBrokerepoch -c clusterV5 -n namesrv1:9876`校验broker 中的选举结果
```bash
# 如下 172.27.0.6 是master 结束该broker 后会重新选举新的broker 应用不中断
#clusterName	clusterV5
#brokerName	MyBroker
#brokerAddr	172.27.0.6:10911
#brokerId	0
#Epoch: EpochEntry{epoch=1, startOffset=0, endOffset=2924528}

#clusterName	clusterV5
#brokerName	MyBroker
#brokerAddr	172.27.0.7:10911
#brokerId	3
#Epoch: EpochEntry{epoch=1, startOffset=0, endOffset=2924528}

#clusterName	clusterV5
#brokerName	MyBroker
#brokerAddr	172.27.0.5:10911
#brokerId	2
#Epoch: EpochEntry{epoch=1, startOffset=0, endOffset=2924528}
```
7. proxy 必须要放在所有broker 加入集群后再启动,避免提前写入默认队列导致commitLog不一致
8. 部署参照prod 目录进行部署,至少需要三台机器在每个节点上部署namesrv controller broker proxy 
9. 4.X 客户端可以正常使用5.x 集群 （remoiting 协议）通过proxy 8080端口,但要保证broker 端口暴露
10. 5.X 流量使用GRPC 和proxy 8081端口通信没有这个问题