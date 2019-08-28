package com.jacle.kafka.advance.kafkastream;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;

import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * kafka stream中：Kstream和Ktable
 */
public class KStreamInstance {
    private static final CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) {
        //配置属性对象
        Properties props = new Properties();

        //设置流的配置参数
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-stream-2");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "s203:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        //位移复位设置
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        StreamsBuilder streamsBuilder = new StreamsBuilder();
        //Kstream相同的key也不会覆盖,记录流，Ktable日志流，只保存最新的key-value
        //通过窗口将流数据分为多个窗口，窗口是流处理的基本单位
        //kstream定义了三种窗口

        //要写在一起，否则不会执行，流的导向
        final KStream<String, String> source = streamsBuilder.stream("streams-plaintext-input");
        //所有的processor要连起来写,否则执行会发生中断，具体表现为无响应;
//        source.to("streams-pipe-output");
        source.flatMapValues(line->Arrays.asList(line.split(" "))).to("streams-pipe-output");

        //select key设置entry的key
     /*   source.selectKey(new KeyValueMapper<String, String, String>() {
            @Override
            public String apply(String key, String value) {
                return null;
            }
        });*/

        //这个是通过java api来编写的，flatmap返回的是一个数组
/*       KTable<String,Long> counts=source.flatMapValues(new ValueMapper<String, Iterable<String>>() {
            @Override
            public Iterable<String> apply(String value) {
                return Arrays.asList(value.toLowerCase(Locale.getDefault()).split(" "));
            }
        }).map(new KeyValueMapper<String, String, KeyValue<String, String>>() {
            @Override
            public KeyValue<String, String> apply(String key, String value) {
                return new KeyValue<>(value,value);
            }
        }).groupByKey().count(Materialized.as("cout"));*/

        //lamda方式编写
/*        source.flatMapValues(line -> Arrays.asList(line.split(" ")))
                .groupByKey().count().toStream().to("ksstreamoutput",Produced.with(Serdes.String(),Serdes.Long()));*/

        final Topology topology = streamsBuilder.build();
        KafkaStreams kafkaStreams = new KafkaStreams(topology, props);
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
