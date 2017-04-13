package com.maxplus1.demo.storm.spout.builder;

import com.maxplus1.demo.utils.IDUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by xiaolong.qiu on 2017/3/29.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "storm.spout.kafkaSpout")
public class KafkaSpoutBuilder extends SpoutBuilder{

    private String brokerZkStr;
    private String topic;
    private String zkRoot;


    @Bean("kafkaSpout")
    public KafkaSpout buildSpout() {
        super.setId("kafkaSpout");
        /**
         *
         * new ZkHosts(brokerZkStr):brokerZkStr逗号分隔，kafka的zookeeper集群
         * topic:storm订阅的topic，即从哪个topic读取消息
         * spout会根据config的zkRoot和id两个参数在zookeeper上为每个kafka分区创建保存kafka偏移量的路径，如：/zkRoot/id/partitionId。
         * zkRoot:偏移量保存的zk根路径
         * id:如果重新运行，希望获取同样的偏移量，则设置为固定的ID
         * PS：kafka新版本已经不将偏移量保存在zookeeper了。而且也不推荐将offset写入zk（低效）。
         */
        SpoutConfig spoutConf = new SpoutConfig(new ZkHosts(brokerZkStr), topic, zkRoot, IDUtils.getUuid());
        spoutConf.scheme = new SchemeAsMultiScheme(new StringScheme());
        return new KafkaSpout(spoutConf);
    }
}
