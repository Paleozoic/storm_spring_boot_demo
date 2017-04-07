package com.maxplus1.demo.storm.spout;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;

import java.util.Map;

/**
 * 可以实现自己的Spout来提取数据源，
 * 比如提取队列数据，可以在nextTuple的时候出队
 * Created by xiaolong.qiu on 2017/3/28.
 * {@link org.apache.storm.spout.ISpout}
 * {@link org.apache.storm.topology.IComponent}
 */
public class TestSpout extends BaseRichSpout {


    /**
     * task在集群的每个worker初始化时都会被调用，
     * open函数提供了每个spout运行的上下文环境。
     * Called when a task for this component is initialized within a worker on the cluster.
     * It provides the spout with the environment in which the spout executes.
     *
     * This includes the:
     *
     * @param conf The Storm configuration for this spout. This is the configuration provided to the topology merged in with cluster configuration on this machine.
     * @param context This object can be used to get information about this task's place within the topology, including the task id and component id of this task, input and output information, etc.
     * @param collector The collector is used to emit tuples from this spout. Tuples can be emitted at any time, including the open and close methods. The collector is thread-safe and should be saved as an instance variable of this spout object.
     */
    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {

    }
    /**
     * Storm请求Spout发送tuples到output collector的时候，nextTuple被调用。
     * 此方法必须是非阻塞的，所以如果没有tuples，则必须返回。
     * When this method is called, Storm is requesting that the Spout emit tuples to the
     * output collector. This method should be non-blocking, so if the Spout has no tuples
     * to emit, this method should return. nextTuple, ack, and fail are all called in a tight
     * loop in a single thread in the spout task. When there are no tuples to emit, it is courteous
     * to have nextTuple sleep for a short amount of time (like a single millisecond)
     * so as not to waste too much CPU.
     */

    @Override
    public void nextTuple() {

    }

    /**
     * 为此topology的所有streams声明输出定义
     * Declare the output schema for all the streams of this topology.
     *
     * @param declarer this is used to declare output stream ids, output fields, and whether or not each output stream is a direct stream
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }
}
