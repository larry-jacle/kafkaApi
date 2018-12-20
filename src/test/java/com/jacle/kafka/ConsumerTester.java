package com.jacle.kafka;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.internals.Topic;
import org.junit.Test;

import java.util.*;

public class ConsumerTester {

    private Map<TopicPartition,OffsetAndMetadata>  currentOffset=new HashMap<TopicPartition, OffsetAndMetadata>();

    @Test
    public void consume() {
        Properties props = new Properties();
        /* 定义kakfa 服务的地址，不需要将所有broker指定上 */
        props.put("bootstrap.servers", "10.1.12.202:9092,10.1.12.203:9092,10.1.12.204:9092");
        /* 制定consumer group */
        props.put("group.id", "test");
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
        final KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        /* 消费者订阅的topic, 可同时订阅多个 */
        consumer.subscribe(Collections.singletonList("topic_1"), new ConsumerRebalanceListener() {
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> collection) {
                //消费者停止读取数据后，设置之后消费者读取消息的偏移量
                consumer.commitSync(currentOffset);

            }

            //在重新分区之后，开始消费之前，设置下偏移量
            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                long committedOffset = -1;
                for(TopicPartition topicPartition : partitions) {
                    // 获取该分区已经消费的偏移量
                    committedOffset = consumer.committed(topicPartition).offset();
                    // 重置偏移量到上一次提交的偏移量的下一个位置处开始消费
                    consumer.seek(topicPartition, committedOffset + 1);
                }

                //从头开始消费
                //consumer.seekToBeginning(partitions);

                //从最新位置开始消费
                //consumer.seekToEnd(partitions);

            }
        });

         /* 读取数据，读取超时时间为100ms */
         try {

             while (true) {
                 //程序如果没有消费会阻塞在这里
//            System.out.println("waiting for data stream");
                 //每隔一定的时间去拉取数据
                 ConsumerRecords<String, String> records = consumer.poll(5000);
//            System.out.println("comsume ..."+records.count());

                 //获取最大风趣的
                 for (ConsumerRecord<String, String> record : records) {
                     System.out.println("value = " + record.value() + ", topic = " + record.topic() + ", partition = "
                             + record.partition() + ", offset = " + record.offset());

                     //记录分区的偏移量
                     currentOffset.put(new TopicPartition(record.topic(), record.partition()),
                             new OffsetAndMetadata(record.offset() + 1, "no metadata"));
                 }


                 //需要手动提交offset,不然进行重启重复读取数据
                 //异步提交的方式更快
                 //提交本地最大的offset
                 consumer.commitAsync();
             }

         }catch (Exception e)
         {
             e.printStackTrace();
         }
         finally
         {
              //消费者发生异常是退出消费，定位问题，在开启重新消费
              //关闭之前要先
             try
             {
                 //关闭comsumer之后会发生rebalance，所以要先提交offset，防止出现重复消费的情况
                 consumer.commitSync();
             }finally
             {
                 consumer.close();
             }
         }

    }
}
