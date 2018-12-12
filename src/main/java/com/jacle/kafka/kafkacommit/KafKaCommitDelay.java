package com.jacle.kafka.kafkacommit;

import com.jacle.kafka.threadmethod.ProducerRunnable;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;

public class KafKaCommitDelay
{
    public static void main(String[] args)
    {
        final String topic="topicThread_commit";
        Thread produceThread=new Thread(new ProducerRunnable(topic));

        Thread consumerThread1=new Thread(new Runnable() {
            @Override
            public void run() {
                KafkaConsumer c1=new KafkaConsumer(getConfig("consume"));
                c1.subscribe(Arrays.asList(topic));

                while(true)
                {
                    ConsumerRecords<String,String> records=c1.poll(100);
                    for(ConsumerRecord r: records)
                    {
                        System.out.println("##consumer1##--offset:"+r.offset()+",partitions:"+r.partition());
                    }
                    c1.commitSync();
                }
            }
        });

        produceThread.start();
        consumerThread1.start();

    }

    public static  Properties getConfig(String groupid)
    {
        Properties props = new Properties();
        /* 定义kakfa 服务的地址，不需要将所有broker指定上 */
        props.put("bootstrap.servers", "10.1.12.202:9092,10.1.12.203:9092,10.1.12.204:9092");
        /* 制定consumer group */
        props.put("group.id", groupid);
        /* 是否自动确认offset */
        //没有使用jps，所以这里采用的是手动提交
        props.put("enable.auto.commit", "false");
        props.put("auto.offset.reset", "earliest");
        /* 自动确认offset的时间间隔 */
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("max.poll.records", "1");
        /* key的序列化类 */
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        /* value的序列化类 */
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        return props;
    }


}
