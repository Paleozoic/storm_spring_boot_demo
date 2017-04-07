package com.maxplus1.demo.storm.bolt;

import lombok.extern.slf4j.Slf4j;
import org.apache.storm.Config;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.windowing.TupleWindow;

import java.util.Map;

/**
 * Created by xiaolong.qiu on 2017/3/30.
 */
@Slf4j
public class SlidingWindowBolt extends BaseWindowedBolt {

    private OutputCollector collector;

    @Override
    public void prepare(Map stormConf, TopologyContext context,
                        OutputCollector collector) {
        this.collector = collector;
    }


    @Override
    public void execute(TupleWindow inputWindow) {
        this.windowConfiguration.get(Config.TOPOLOGY_BOLTS_WINDOW_LENGTH_DURATION_MS);
        this.windowConfiguration.get(Config.TOPOLOGY_BOLTS_SLIDING_INTERVAL_COUNT);
        this.windowConfiguration.get(Config.TOPOLOGY_BOLTS_SLIDING_INTERVAL_DURATION_MS);
        int sum = 0;
        log.info("一个时间窗口开始！");
//        log.info("",inputWindow.);
        for (Tuple tuple : inputWindow.get()) {
            int count = (Integer) tuple.getValueByField("");
            sum += count;
        }
    }
}
