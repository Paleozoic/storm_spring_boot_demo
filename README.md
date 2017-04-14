# storm_spring_boot_demo

# 依赖组件
- Storm
- MySQL
- Redis Cluster
- Kafka

# 未完成，Coding中……

# 进度
- 本地模式已调通…… 但数据正确性待验证

# PS
- 如果Spring Boot的很多对象实现了序列化接口就好办多了
- 本人初接触Storm和Kafka，项目时间较短。很多不足，继续完善中……

# word count table defination
```sql
CREATE TABLE `word_count` (
  `word` varchar(255) COLLATE utf8_bin NOT NULL,
  `count` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`word`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin
```


