package com.jacle.kafka.advance.kafkastream;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.ValueJoiner;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * Kafka Stream join
 */
public class KStreamJoin {
    //join的操作分为三类ktable join ktable ->ktable
    //kstream join kstream ->kstream must by windowed
    //KStream Join KTable / GlobalKTable 结果为KStream。只有当KStream中有新数据时，才会触发Join计算并输出结果。KStream无新数据时，KTable的更新并不会触发Join计算，也不会输出数据。并且该更新只对下次Join生效。一个典型的使用场景是，KStream中的订单信息与KTable中的用户信息做关联计算。
    private static final CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) {
        //配置属性对象
        Properties props = new Properties();

        //设置流的配置参数
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-stream-innerjoin");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "s203:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        //位移复位设置
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        StreamsBuilder streamsBuilder = new StreamsBuilder();
        final KStream<String, String> source1= streamsBuilder.stream("joinA");
        final KStream<String, String> source2 = streamsBuilder.stream("joinB");

        //对两个数据源进行数据连接操作
        //处理明确key，和value合并之后的数值
    /*
        KStream<String,String> all=source1.selectKey((k,v)-> v.split(",")[1]).join(source2.selectKey((k, v) -> v.split(",")[0]), (s1,s2)->s1+"--"+s2, JoinWindows.of(90000));
        all.print(Printed.toSysOut());

    */

        //外连接分为：左外连接和右外连接
        KStream<String,String> all=source1.selectKey((k,v)-> v.split(",")[1]).outerJoin(source2.selectKey((k, v) -> v.split(",")[0]), (s1,s2)->s1+"--"+s2, JoinWindows.of(90000));
        all.print(Printed.toSysOut());

        //设置正常和合理的启动kafka stream
        final Topology topology = streamsBuilder.build();
        KafkaStreams kafkaStreams = new KafkaStreams(topology, props);

        //正常关闭
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
