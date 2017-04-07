package com.maxplus1.demo.storm.props;

import lombok.Getter;
import lombok.Setter;
import org.apache.storm.topology.IRichSpout;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Storm 的配置优先级为 defaults.yaml < storm.yaml < 拓扑配置 < 内置型组件信息配置 < 外置型组件信息配置。
 *
 * Storm 的一个很有意思的特点是你可以随时增加或者减少 worker 或者 executor 的数量，而不需要重启集群或者拓扑。这个方法就叫做再平衡（rebalance）。
 *  有两种方法可以对一个拓扑执行再平衡操作：
 *       （1）使用 Storm UI
 *       （2）使用以下所示的客户端（CLI）工具
 *          下面是使用 CLI 工具的一个简单示例：
 *          ## 重新配置拓扑 "mytopology"，使得该拓扑拥有 5 个 worker processes，
 *          ## 另外，配置名为 "blue-spout" 的 spout 使用 3 个 executor，
 *          ## 配置名为 "yellow-bolt" 的 bolt 使用 10 个 executor。
 *          $ storm rebalance mytopology -n 5 -e blue-spout=3 -e yellow-bolt=10
 *
 * Created by xiaolong.qiu on 2017/3/29.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "storm.props")
public class StormProps {


    /**
     * 拓扑名字，一个Storm拓扑一个名字
     */
    private String topologyName;

    /**
     * How many processes should be spawned around the cluster to execute this
     * topology. Each process will execute some number of tasks as threads within
     * them. This parameter should be used in conjunction with the parallelism hints
     * on each component in the topology to tune the performance of a topology.
     * {@link org.apache.storm.Config#setNumWorkers(Map, int)}
     */
    private Integer topologyWorkers = 1;

    /**
     * Storm默认每个Executor内执行一个Task，但是也可以指定。
     * {@link org.apache.storm.topology.ComponentConfigurationDeclarer#setNumTasks(Number)}
     */
    private Integer numTasks = 1;


    /**
     * 此项设置了单个 Spout 任务能够挂起的最大的 tuple 数（tuple 挂起表示该 tuple 已经被发送但是尚未被 ack 或者 fail）。
     * 强烈建议设置此参数来防止消息队列的爆发性增长。
     * The maximum number of tuples that can be pending on a spout task at any given time.
     * This config applies to individual tasks, not to spouts or topologies as a whole.
     *
     * A pending tuple is one that has been emitted from a spout but has not been acked or failed yet.
     * Note that this config parameter has no effect for unreliable spouts that don't tag
     * their tuples with a message id.
     * {@link org.apache.storm.Config#setMaxSpoutPending(Map, int)}}
     */
    private Integer topologyMaxSpoutPending = 5000;

    /**
     * 此项设置了 ackers 跟踪 tuple 的超时时间。默认值是 30 秒，对于大部分拓扑而言这个值基本上是不需要改动的。
     * The maximum amount of time given to the topology to fully process a message
     * emitted by a spout. If the message is not acked within this time frame, Storm
     * will fail the message on the spout. Some spouts implementations will then replay
     * the message at a later time.
     * {@link org.apache.storm.Config#setMessageTimeoutSecs(Map, int)}
     */
    private Integer topologyMessageTimeoutSecs = 30;

    /**
     * 其他Storm相关调优参数
     */
    //....

}
