# storm_spring_boot_demo

# 依赖组件
- Storm
- MySQL
- Redis Cluster
- Kafka

# 进度
- 本地模式已调通…… 

# PS
- 如果Spring Boot的很多对象实现了序列化接口就好办多了

# word count table defination
```sql
CREATE TABLE `word_count` (
  `targetDate` date NOT NULL,
  `word` varchar(255) COLLATE utf8_bin NOT NULL,
  `count` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`targetDate`,`word`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin
```
# BUG
测试发现写入Kafka的数据正确，但是KafkaSpout消费的时候，会出现少量的重复消息。
原因暂时不明。
检查了Bolt都进行了ACK，理论上是不会重发的。
不排除是offset没提交的原因。
待研究。

