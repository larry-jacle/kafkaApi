package com.jacle.kafka.threadmethod;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;

public class ConsumerRunnable  implements  Runnable
{
    private Consumer consumer;
    private String topic;
    private String groupid;
    private int groupindex;

    public ConsumerRunnable()
    {

    }

    public ConsumerRunnable(String topic,String groupid,int groupIndex)
    {
        this.topic=topic;
        this.groupid=groupid;
        this.groupindex=groupIndex;
        consumer=new KafkaConsumer(getConfig());
        consumer.subscribe(Arrays.asList(topic));
    }


    public Properties getConfig()
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
        props.put("max.poll.records", "19");
        /* key的序列化类 */
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        /* value的序列化类 */
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        return props;
    }


    @Override
    public void run() {

        try {
            while(true)
            {
                ConsumerRecords<String,String> records=consumer.poll(100);
                for(ConsumerRecord r: records)
                {
                    System.out.println("##consumer"+groupindex+"##--offset:"+r.offset()+",partitions:"+r.partition());
                }

                //手动提交offset
                consumer.commitSync();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e)
        {
           e.printStackTrace();
           if(consumer!=null)
           {
               consumer.close();
           }
        }

    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

}
