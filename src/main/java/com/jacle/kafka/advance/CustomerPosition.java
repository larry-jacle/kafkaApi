package com.jacle.kafka.advance;

import com.jacle.lombok.Company;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomerPosition {
    //这个值其他类会操作，要考虑线程安全；
    public static AtomicBoolean isRunnig = new AtomicBoolean(true);
    public static AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) {
        consumeMsg();
    }

    public static void consumeMsg() {
        Properties properties = getConfig("consumerGroup1");

        KafkaConsumer<String, Company> consumer = new KafkaConsumer<>(properties);

        //partitionInfo是主题对应分区的元数据
        List<PartitionInfo> partitionInfos = consumer.partitionsFor("test");

        TopicPartition tp = new TopicPartition(partitionInfos.get(0).topic(), partitionInfos.get(0).partition());
        //消费者可以订阅一个队列的一个分区
        consumer.assign(Arrays.asList(tp));
        long lastOffset = -1;

        try {
            while (isRunnig.get()) {
                ConsumerRecords<String, Company> records = consumer.poll(200);


                System.out.println("##########" + counter.get() + "#############");
                Thread.sleep(6000);

                //手动提交,同步提交，无参数方法
                //有参数的同步提交能够实现，消费一条，提交一条
                //这种方式实际使用的很少，性能比较低
                //commitSyncOnebyOne(consumer, tp, result);

                //按照分区的力度来进行位移提交,获取消息所在的分区集合
                for(TopicPartition topicPartition:records.partitions())
                {
                    //根据主题进行消费
                    List<ConsumerRecord<String, Company>> result = records.records(topicPartition);

                    for(ConsumerRecord record:result)
                    {
                        //TODO
                    }

                    //开始提交位移，一个分区处理完，提交一次
                    consumer.commitSync(Collections.singletonMap(topicPartition,new OffsetAndMetadata(result.get(result.size()-1).offset()+1)));
                }


                System.out.println("##########" + counter.get() + "#############");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            consumer.close();
        }
    }

    private static void commitSyncOnebyOne(KafkaConsumer<String, Company> consumer, TopicPartition tp, List<ConsumerRecord<String, Company>> result) {
        for(ConsumerRecord<String,Company> record:result)
        {
            consumer.commitSync(Collections.singletonMap(tp,new OffsetAndMetadata(record.offset()+1)));
        }
    }


    public static Properties getConfig(String groupid) {
        Properties props = new Properties();
        /* 定义kakfa 服务的地址，不需要将所有broker指定上 */
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "10.1.12.202:9092,10.1.12.203:9092,10.1.12.204:9092");
        /* 制定consumer group */
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupid);
        /* 是否自动确认offset */
        //没有使用jps，所以这里采用的是手动提交
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        /* 自动确认offset的时间间隔 */
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        /* key的序列化类 */
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        /* value的序列化类 */
//        props.put("value.deserializer",SelfDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ProtobuffDeserializer.class.getName());
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "consumer01");

        return props;
    }
}
