/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.storm.starter.bolt;

import org.apache.log4j.Logger;
import org.apache.storm.Config;
import org.apache.storm.starter.tools.NthLastModifiedTimeTracker;
import org.apache.storm.starter.tools.SlidingWindowCounter;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.TupleUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This bolt performs rolling counts of incoming objects, i.e. sliding window based counting.
 * <p/>
 * The bolt is configured by two parameters, the length of the sliding window in seconds (which influences the output
 * data of the bolt, i.e. how it will count objects) and the emit frequency in seconds (which influences how often the
 * bolt will output the latest window counts). For instance, if the window length is set to an equivalent of five
 * minutes and the emit frequency to one minute, then the bolt will output the latest five-minute sliding window every
 * minute.
 * <p/>
 * The bolt emits a rolling count tuple per object, consisting of the object itself, its latest rolling count, and the
 * actual duration of the sliding window. The latter is included in case the expected sliding window length (as
 * configured by the user) is different from the actual length, e.g. due to high system load. Note that the actual
 * window length is tracked and calculated for the window, and not individually for each object within a window.
 * <p/>
 * Note: During the startup phase you will usually observe that the bolt warns you about the actual sliding window
 * length being smaller than the expected length. This behavior is expected and is caused by the way the sliding window
 * counts are initially "loaded up". You can safely ignore this warning during startup (e.g. you will see this warning
 * during the first ~ five minutes of startup time if the window length is set to five minutes).
 */
public class RollingCountBolt extends BaseRichBolt {

  private static final long serialVersionUID = 5537727428628598519L;
  private static final Logger LOG = Logger.getLogger(RollingCountBolt.class);
  private static final int NUM_WINDOW_CHUNKS = 5;
  private static final int DEFAULT_SLIDING_WINDOW_IN_SECONDS = NUM_WINDOW_CHUNKS * 60;
  private static final int DEFAULT_EMIT_FREQUENCY_IN_SECONDS = DEFAULT_SLIDING_WINDOW_IN_SECONDS / NUM_WINDOW_CHUNKS;
  private static final String WINDOW_LENGTH_WARNING_TEMPLATE =
      "Actual window length is %d seconds when it should be %d seconds"
          + " (you can safely ignore this warning during the startup phase)";

  private final SlidingWindowCounter<Object> counter;
  private final int windowLengthInSeconds;
  private final int emitFrequencyInSeconds;
  private OutputCollector collector;
  private NthLastModifiedTimeTracker lastModifiedTracker;

  public RollingCountBolt() {
    this(DEFAULT_SLIDING_WINDOW_IN_SECONDS, DEFAULT_EMIT_FREQUENCY_IN_SECONDS);
  }

  public RollingCountBolt(int windowLengthInSeconds, int emitFrequencyInSeconds) {
    this.windowLengthInSeconds = windowLengthInSeconds;
    this.emitFrequencyInSeconds = emitFrequencyInSeconds;
    counter = new SlidingWindowCounter<Object>(deriveNumWindowChunksFrom(this.windowLengthInSeconds,
        this.emitFrequencyInSeconds));
  }

  private int deriveNumWindowChunksFrom(int windowLengthInSeconds, int windowUpdateFrequencyInSeconds) {
    return windowLengthInSeconds / windowUpdateFrequencyInSeconds;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
    this.collector = collector;
      /**
       * new NthLastModifiedTimeTracker(numSlots)
       * 新建一个追踪器，使用CircularFifoBuffer实现，循环缓冲。追踪数量为numSlots。追踪内容为time-since-last-modify。
       * 这个追踪器同样也是一个时间滑动窗口，并且和SlidingWindowCounter是一致的。
       */
    lastModifiedTracker = new NthLastModifiedTimeTracker(deriveNumWindowChunksFrom(this.windowLengthInSeconds,
        this.emitFrequencyInSeconds));
  }

  @Override
  public void execute(Tuple tuple) {
    //如果是TickTuple（则说明该Tuple触发了时间窗口），则将当前时间窗口的计数和实际的时间窗口长度emit。
    if (TupleUtils.isTick(tuple)) {
      LOG.debug("Received tick tuple, triggering emit of current window counts");
      emitCurrentWindowCounts();
    }
    else {
      countObjAndAck(tuple);
    }
  }

  private void emitCurrentWindowCounts() {
  /**
   * counter是SlidingWindowCounter，他的类结构如下：
   *
   * SlidingWindowCounter 滑动窗口计数器
   *    private SlotBasedCounter<T> objCounter; 基于槽的计数器
   *         private final Map<T, long[]> objToCounts = new HashMap<T, long[]>();  构成一个二维矩阵纵轴为对象列，横轴为槽。表示某个对象在某个槽的计数
   *         private final int numSlots; 槽数量 即long[]的length
   *    private int headSlot; 当前计数slot
   *    private int tailSlot; 下一个计数slot
   *    private int windowLengthInSlots; 用一组slot表示窗口长度。slot的个数便是windowLengthInSlots。也就是SlotBasedCounter的numSlots
   */
    Map<Object, Long> counts = counter.getCountsThenAdvanceWindow();
      /**
       * 根据追踪器获取上次最早时间到现在的时间长度，追踪器追踪了numSlots个时间的修改，
       * 这里返回的是第0个和当前时间之差，在这里表示的是一整个时间窗口（所有slot之和）。
       */
    int actualWindowLengthInSeconds = lastModifiedTracker.secondsSinceOldestModification();
    //记录当前时间为修改时间
    lastModifiedTracker.markAsModified();
      /**
       * 如果时间窗口长度和实际获取的时间窗口长度不一致，
       * 打印一个警告信息，然而并没有什么用。
       */
    if (actualWindowLengthInSeconds != windowLengthInSeconds) {
      LOG.warn(String.format(WINDOW_LENGTH_WARNING_TEMPLATE, actualWindowLengthInSeconds, windowLengthInSeconds));
    }
    //将实际的窗口长度和计数发送给下游
    emit(counts, actualWindowLengthInSeconds);
  }

  private void emit(Map<Object, Long> counts, int actualWindowLengthInSeconds) {
    for (Entry<Object, Long> entry : counts.entrySet()) {
      Object obj = entry.getKey();
      Long count = entry.getValue();
      collector.emit(new Values(obj, count, actualWindowLengthInSeconds));
    }
  }

    /**
     * 计算接收到的Tuple Count，但是由于没有到时间窗口更新的时间，所以只做计数+ack功能。
     * @param tuple
     */
  private void countObjAndAck(Tuple tuple) {
    Object obj = tuple.getValue(0);
    counter.incrementCount(obj);
    collector.ack(tuple);
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declare(new Fields("obj", "count", "actualWindowLengthInSeconds"));
  }

  @Override
  public Map<String, Object> getComponentConfiguration() {
    Map<String, Object> conf = new HashMap<String, Object>();
      /**
       * 这里配置TickTuple的发送频率
       */
    conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, emitFrequencyInSeconds);
    return conf;
  }
}
