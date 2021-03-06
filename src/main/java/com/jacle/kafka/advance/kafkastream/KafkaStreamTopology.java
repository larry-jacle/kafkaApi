package com.jacle.kafka.advance.kafkastream;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.processor.StateStoreSupplier;
import org.apache.kafka.streams.processor.TopologyBuilder;
import org.apache.kafka.streams.state.Stores;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class KafkaStreamTopology
{
    private static final CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args)
    {
        Properties props = new Properties();

        //每次部署的时候，需要更改ApplicationId
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "ks2-3");
        //日志级别
        props.put(StreamsConfig.METRICS_RECORDING_LEVEL_CONFIG, "INFO");
        //每个任务的副本数，当任务失败的时候，会切换到副本机器执行，减少初始化时间
        props.put(StreamsConfig.NUM_STANDBY_REPLICAS_CONFIG, "2");
        //设置线程的个数，同时可以开启多个实例，从而形成多实例部署
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, "2");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "s203:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "500");
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);

        //位移复位设置
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        StreamsBuilder streamsBuilder = new StreamsBuilder();

        TopologyBuilder builder = new TopologyBuilder();
  /*      builder.addSource("SOURCE", "in").addProcessor("PROCESS1",KafkaStreamProcessor::new,"SOURCE")
        .addStateStore(Stores.create("count").withStringKeys().withLongValues().inMemory().build(),"PROCESS1").addSink("SINK1", "out2", "PROCESS1");
*/
        //可以单独创建statestore
        StateStoreSupplier stateStoreSupplier=Stores.create("count2")
                .withKeys(Serdes.String())
                .withValues(Serdes.Long())
                .persistent()
                .build();

//        builder.addSource("SOURCE", "in").addProcessor("PROCESS1",KafkaStreamProcessor::new,"SOURCE")
//                .addStateStore(stateStoreSupplier,"PROCESS1").addSink("SINK1", "out2", "PROCESS1");


        //如果源是多个topic，只能按照最大的分区数的topic去分配任务
        builder.addSource("SOURCE", "in3","in").addProcessor("PROCESS1",new KafkaStreamProcessorSupplier(),"SOURCE")
                .addStateStore(stateStoreSupplier,"PROCESS1").addProcessor("PROCESS2",MapperProcessor::new,"PROCESS1")
                .addSink("SINK1", "out3", "PROCESS2");

        KafkaStreams kafkaStreams = new KafkaStreams(builder, props);
        Runtime.getRuntime().addShutdownHook(new Thread("streamShutThread") {
            @Override
            public void run() {
                kafkaStreams.close();
                latch.countDown();
                System.out.println("程序关闭...");
            }
        });


        try {
            kafkaStreams.start();

            //latch让程序不停止，跟while的方式一致，但是比while的方式优雅
            //阻塞程序
            System.out.println("程序已启动");
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.exit(0);
    }
}
