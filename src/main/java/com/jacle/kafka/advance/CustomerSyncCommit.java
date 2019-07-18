package com.jacle.kafka.advance;

import com.jacle.lombok.Company;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


/**
 *消费者手动提交：同步提交
 */
public class CustomerSyncCommit
{
    //这个值其他类会操作，要考虑线程安全；
    public static AtomicBoolean isRunnig=new AtomicBoolean(true);
    public static AtomicInteger counter=new AtomicInteger(0);

    public static void main(String[] args) {
        consumeMsg();
    }

    public static void consumeMsg()
    {
        Properties properties = getConfig("consumerGroup1");

        KafkaConsumer<String, Company> consumer = new KafkaConsumer<>(properties);
//        List<PartitionInfo> partitionInfos=consumer.partitionsFor("test");

//        TopicPartition tp=new TopicPartition(partitionInfos.get(0).topic(),partitionInfos.get(0).partition());
        //消费者可以订阅一个队列的一个分区
        //consumer.assign(Arrays.asList(tp));
        consumer.subscribe(Arrays.asList("test"));
//        long lastOffset=-1;


        //优雅的关闭消费者,kafka安全的关闭消费者通过wake方法，抛出的wakeup线程不需要处理
        //如果while循环是在主线程，可以通过runtime的hook来进行

        try {
            while (isRunnig.get()) {
                try {
                    System.out.println("poll之前...");
                    //每次进行poll的时候，内部是执行提交上一次的offset
                    ConsumerRecords<String, Company> records = consumer.poll(6000);
                    //records的方法
                    //System.out.println(records.count());
                    //System.out.println(records.isEmpty());

                    System.out.println("##########" + counter.getAndIncrement() + "#############");
                    Thread.sleep(2000);

                    if (records.count() > 0) {
                        //根据主题进行消费
//                        List<ConsumerRecord<String, Company>> result = records.records(tp);
//                        lastOffset = result.get(result.size() - 1).offset();
                        System.out.println("有消息拉取...");
                        records.forEach(record->{System.out.println(record.value().getCompanyName());});
                        //手动提交,同步提交，无参数方法
                        //提交错误会进行重试,重试的时候，此分区是阻塞的，如果不是出现严重的错误，不会出现无法提交的情况
                        consumer.commitSync();
                    }else
                    {
                        System.out.println("本次无数据...");
//                        consumer.commitSync(); 如果拉取的消息为空，提交offset，不会有响应
                    }

                    System.out.println("##########" + counter.get() + "#############");
                }catch (Exception e)
                {
                    e.printStackTrace();
                }finally {
                    consumer.commitSync();
                }
            }

            /*

            //最后输出position,获取分区已经提交的offset
            OffsetAndMetadata metadata=consumer.committed(tp);
            System.out.println("消息最大的位置："+lastOffset);
            System.out.println("提交的位置:"+metadata.offset());
            //将偏移量一定是针对分区的,肯定要指定分区号
            System.out.println("提交的位置："+consumer.position(tp));

            */

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
        props.put(ConsumerConfig.CLIENT_ID_CONFIG,"consumer01");

        //消费者拦截器会影响提交的位移，原因：最大位移可能会被消费者拦截器过滤掉，这个问题要特别注意
        props.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG,SelfCustomerInterceptor.class.getName());

        return props;
    }
}
