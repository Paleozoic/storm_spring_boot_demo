package com.maxplus1.demo.storm.bolt.builder;

import lombok.Getter;
import lombok.Setter;
import org.apache.storm.task.IBolt;
import org.apache.storm.topology.IBasicBolt;
import org.apache.storm.topology.IComponent;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.IRichSpout;

/**
 * Created by xiaolong.qiu on 2017/3/29.
 */
@Getter
@Setter
public abstract class BoltBuilder {
    /**
     * 拓扑的并行度：它代表着一个组件的初始 executor （也是线程）数量
     * the number of tasks that should be assigned to execute this spout.
     * Each task will run on a thread in a process somewhere around the cluster.
     * {@link org.apache.storm.topology.TopologyBuilder#setSpout(String, IRichSpout, Number)}
     */
    private Integer parallelismHint = 5;

    private String id;

    abstract public IComponent buildBolt();
}
