# storm_spring_boot_demo

# 依赖组件
- Storm
- MySQL
- Redis Cluster
- Kafka

# word count table defination
```sql
CREATE TABLE `word_count` (
  `targetDate` date NOT NULL,
  `word` varchar(255) COLLATE utf8_bin NOT NULL,
  `count` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`targetDate`,`word`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin
```

# 模拟功能
- 模拟业务数据写入Kafka（生成句子写入Kafka）
- KafkaSpout消费Kafka指定topic
- 清洗数据（句子拆分为单词）
- 实时统计（滑动窗口数据写入Redis）
- 汇总统计（每天的单词计数根据日期归档至MySQL）
- TopN（这个纯属娱乐，源码实现待研究）

# PS
- 如果Spring Boot的很多对象实现了序列化接口就好办多了


# BUG
测试发现写入Kafka的数据正确，但是KafkaSpout消费的时候，偶尔会出现少量的重复消息。
原因暂时不明。
检查了Bolt都进行了ACK，理论上是不会重发的。
待研究。
- offset没提交，重复消费
- 没有ACK，重复发送
- 网络抖动/启动机制/or其他？？？

