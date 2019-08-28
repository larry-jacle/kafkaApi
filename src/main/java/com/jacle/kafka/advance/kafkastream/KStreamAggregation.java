package com.jacle.kafka.advance.kafkastream;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * kafka stream中：Kstream和Ktable
 */
public class KStreamAggregation {
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

        //lamda方式编写
        //输出的是Ktable是一个日志流，覆盖流
        //Kstream可能会被压缩，所以建立队列的是偶要注意;
        source.flatMapValues(line -> Arrays.asList(line.split(" "))).map((k,v)->new KeyValue<>(v,v)).groupByKey().
                count().toStream().foreach((k,v)->{System.out.println(k+":"+v);});
//                toStream().to("ksstreamoutput", Produced.with(Serdes.String(), Serdes.Long()));

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
