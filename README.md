# storm_spring_boot_demo

# 依赖组件
- Storm
- MySQL
- Redis Cluster
- Kafka

# 未完成，Coding中……

# word count table defination
CREATE TABLE `word_count` (
  `word` varchar(255) COLLATE utf8_bin NOT NULL,
  `count` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`word`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin

