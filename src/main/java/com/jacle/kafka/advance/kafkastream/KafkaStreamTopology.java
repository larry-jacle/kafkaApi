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

        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "ks2");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "s203:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

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

        builder.addSource("SOURCE", "in").addProcessor("PROCESS1",new KafkaStreamProcessorSupplier(),"SOURCE")
                .addStateStore(stateStoreSupplier,"PROCESS1").addProcessor("PROCESS2",MapperProcessor::new,"PROCESS1")
                .addSink("SINK1", "out2", "PROCESS2");

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
