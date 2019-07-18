package com.jacle.kafka.advance;

import com.jacle.lombok.Company;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * kafka从任意位置开始消费
 *
 */
public class KafkaSeek
{
    //这个值其他类会操作，要考虑线程安全；
    public static AtomicBoolean isRunnig=new AtomicBoolean(true);
    public static AtomicInteger counter=new AtomicInteger(0);

    public static void main(String[] args) {
        consumeMsg();
    }

    public static void consumeMsg()
    {
        String groupid="consumerGroup2";
        String topic="test";

        Properties properties = getConfig(groupid);
        KafkaConsumer<String, Company> consumer = new KafkaConsumer<>(properties);

        //1、消费者可以订阅一个队列的一个分区
        consumer.subscribe(Arrays.asList(topic));


        //2、定于之后获取消费者分配的分区
        Set<TopicPartition> topicPartitions=new HashSet<>();
        while(topicPartitions.size()==0)
        {
            System.out.println("获取分区分配信息...");
            consumer.poll(2000);
            topicPartitions=consumer.assignment();
        }

        //获取每个分区的最后的新消息位置
        Map<TopicPartition,Long> endOffsets=consumer.endOffsets(topicPartitions);

        //通过endoffsets来设置每个分区的last position
        SeekToEnd(consumer, topicPartitions, endOffsets);
        //consumer.seekToEnd(topicPartitions);

        //3、从指定位置开始消费
        try {
            while (isRunnig.get()) {
                ConsumerRecords<String, Company> records = consumer.poll(6000);

                System.out.println("#######################");


                if(records.count()>0)
                {
                    //消费到信息才去提交offset
                    System.out.println("接收到消息条数:"+records.count());
                    consumer.commitSync();
                }

                System.out.println("#######################");
            }
        }catch (WakeupException w)
        {
            //可以通过consumer的wakeup
            //ignore exception
        }catch (Exception e) {
            e.printStackTrace();
        }finally
        {
            consumer.close();
        }
    }

    private static void SeekToEnd(KafkaConsumer<String, Company> consumer, Set<TopicPartition> topicPartitions, Map<TopicPartition, Long> endOffsets) {
        //设置消费者分区的指定offset
        for(TopicPartition tp:topicPartitions)
        {
            //设置每个分区都是10开始消费
            //如果分区设置为越界，会出发auto.reset.offset的设置
            consumer.seek(tp,2000);

            //从每个分区的末尾开始消费
//            consumer.seek(tp,endOffsets.get(tp));
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
        //如果能够找到消费者的offset，offset.reset就不会生效
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
        /* 自动确认offset的时间间隔 */
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        /* key的序列化类 */
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        /* value的序列化类 */
//        props.put("value.deserializer",SelfDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ProtobuffDeserializer.class.getName());
        props.put(ConsumerConfig.CLIENT_ID_CONFIG,"consumer01");

        return props;
    }
}
