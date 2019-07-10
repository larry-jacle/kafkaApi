package com.jacle.kafka.advance;

import com.jacle.lombok.Company;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SelfDeserializerConsumer {

    //这个值其他类会操作，要考虑线程安全；
    public static AtomicBoolean isRunnig=new AtomicBoolean(true);
    public static AtomicInteger counter=new AtomicInteger(0);

    public static void main(String[] args) {
        consumeMsg();
    }

    public static void consumeMsg() {
        Properties properties = getConfig("consumerGroup1");

        KafkaConsumer<String, Company> consumer = new KafkaConsumer<>(properties);

        //消费者可以订阅一个队列
//        consumer.subscribe(Arrays.asList("test"));


        //消费者也可以指定订阅一个队列的分区，如何判断一个分区是否存在，可以通过获取分区元数据对象来解决
        List<PartitionInfo> partitionInfos=consumer.partitionsFor("test");
        List<TopicPartition>  topicPartitions=new ArrayList<>();
        for(PartitionInfo pi:partitionInfos)
        {
            topicPartitions.add(new TopicPartition(pi.topic(),pi.partition()));
        }

        //assign订阅的分区，不能够实现负载均衡，当消费者增加或者减少的时候，不能进行故障转移和负载均衡
        consumer.assign(topicPartitions);

        long start=0l;
        long end =0l;
        try {
            while (isRunnig.get()) {
                //通过拉取的方式来从缓冲区获取消息
                //poll的timeout为0，不管是否有数据都会立刻返回应用线程处理
                //timeout表示的是缓冲区没有消息，多久移交控制权,所以为了提高吞吐，timeout取决于应用响应的时间
                //如果设置为Long.MAX_VALUE可以用来当没有消息的时候，完全阻塞，不移交控制权,此类情况用来处理，此类应用程序只是拉取消息和消费消息
                ConsumerRecords<String, Company> records = consumer.poll(Long.MAX_VALUE);
                //records的方法
                System.out.println(records.count());
                System.out.println(records.isEmpty());

//                ConsumerRecords<String, Company> records = consumer.poll(200);
                System.out.println("##########"+counter.get()+"#############");
                start=System.currentTimeMillis();
                System.out.println("拉取数据..."+counter.get());
                System.out.println(records.isEmpty());
                Thread.sleep(6000);
                end= System.currentTimeMillis();
                System.out.println("应用开始处理..."+counter.getAndIncrement());

                //根据主题进行消费
                for (ConsumerRecord<String, Company> record : records.records("test")) {
                    System.out.println(record.offset() + "/" + record.partition() + "/" + record.key() + "/" + record.value().getCompanyName());
                }

                Iterator<ConsumerRecord<String,Company>> iterator=records.records("test").iterator();


                //iterator的方式遍历
                while(iterator.hasNext()) {
                    ConsumerRecord<String,Company> record=iterator.next();
                    System.out.println(record.offset() + "/" + record.partition() + "/" + record.key() + "/" + record.value().getCompanyName());
                }
                System.out.println("##########"+(end-start)/1000.0+"#############");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally
        {
            consumer.close();
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
        //自动提交每隔指定时间会进行提交，提交是通过coordinator来实现的，coordinator的调用是在poll方法里开始线程来执行的
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
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
