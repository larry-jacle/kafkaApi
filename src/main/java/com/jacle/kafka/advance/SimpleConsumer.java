package com.jacle.kafka.advance;

import org.apache.kafka.clients.consumer.*;

import java.util.Arrays;
import java.util.Properties;

public class SimpleConsumer {

    public static void main(String[] args)
    {
        consumeMsg();
    }

    public static void consumeMsg() {
        Properties properties = getConfig("G--consumerGroup1");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
        consumer.subscribe(Arrays.asList("temperature"));

        while(true)
        {
            ConsumerRecords<String,String> records=consumer.poll(100);

            for(ConsumerRecord<String,String> record:records)
            {
                System.out.println(record.offset()+"/"+record.partition()+"/"+record.key()+"/"+record.value());
            }
        }
    }


    public static Properties getConfig(String groupid) {
        Properties props = new Properties();
        /* 定义kakfa 服务的地址，不需要将所有broker指定上 */
        props.put("bootstrap.servers", "10.1.12.202:9092,10.1.12.203:9092,10.1.12.204:9092");
        /* 制定consumer group */
        props.put("group.id", groupid);
        /* 是否自动确认offset */
        //没有使用jps，所以这里采用的是手动提交
        props.put("enable.auto.commit", "true");
        /* 自动确认offset的时间间隔 */
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        /* key的序列化类 */
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        /* value的序列化类 */
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        //设置为sticky的分区分配策略
        props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, StickyAssignor.class.getName());

        return props;
    }
}
