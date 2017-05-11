package com.maxplus1.demo.storm;

import com.maxplus1.demo.storm.props.StormProps;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.topology.TopologyBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by qxloo on 2017/4/15.
 */
@Service
public class AppMain {

    @Autowired
    private StormProps stormProps;
    @Autowired
    private TopologyBuilder topologyBuilder;

    public void Laugher() throws InvalidTopologyException, AuthorizationException, AlreadyAliveException, InterruptedException {
        Config config = new Config();
//        remoteSubmit(stormProps,topologyBuilder,config);
        localSubmit(stormProps.getTopologyName(),topologyBuilder,config);
    }


    private static void remoteSubmit(StormProps stormProps, TopologyBuilder builder, Config conf)
            throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {
        conf.setNumWorkers(stormProps.getTopologyWorkers());
        conf.setMaxSpoutPending(stormProps.getTopologyMaxSpoutPending());
        StormSubmitter.submitTopology(stormProps.getTopologyName(), conf, builder.createTopology());
    }

    /**
     * 用于debug
     * @param name
     * @param builder
     * @throws InterruptedException
     */
    private static void localSubmit(String name,TopologyBuilder builder, Config conf)
            throws InterruptedException {
        conf.setDebug(false);
        conf.setMaxTaskParallelism(3);
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology(name, conf, builder.createTopology());
        Thread.sleep(1000000);
        cluster.shutdown();
    }
}
