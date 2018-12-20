package com.jacle.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.Test;

import java.util.Arrays;
import java.util.Properties;

public class Consumer2Tester {

    @Test
    public void consume() {
        Properties props = new Properties();
        /* 定义kakfa 服务的地址，不需要将所有broker指定上 */
        props.put("bootstrap.servers", "10.1.12.202:9092,10.1.12.203:9092,10.1.12.204:9092");
        /* 制定consumer group */
        props.put("group.id", "test2");
        /* 是否自动确认offset */
        //没有使用jps，所以这里采用的是手动提交
        props.put("enable.auto.commit", "false");
        /* 自动确认offset的时间间隔 */
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        /* key的序列化类 */
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        /* value的序列化类 */
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
         /* 定义consumer */
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        /* 消费者订阅的topic, 可同时订阅多个 */
        consumer.subscribe(Arrays.asList("topic_1"));

         /* 读取数据，读取超时时间为100ms */
        while (true) {
            //程序如果没有消费会阻塞在这里
//            System.out.println("waiting for data stream");
            //每隔一定的时间去拉取数据
            ConsumerRecords<String, String> records = consumer.poll(5000);
//            System.out.println("comsume ..."+records.count());
            for (ConsumerRecord<String, String> record : records)
            {
                System.out.println("cosumer2:"+record.offset()+"/"+ record.key()+"/"+ record.value());
            }

            //需要手动提交offset,不然进行重启重复读取数据
            consumer.commitAsync();
        }

    }
}
