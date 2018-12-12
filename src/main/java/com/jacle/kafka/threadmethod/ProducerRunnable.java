package com.jacle.kafka.threadmethod;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;
import java.util.Random;
import java.util.UUID;

public class ProducerRunnable implements  Runnable {

    //消息生产者
    private Producer producer;

    private String topic;

    public ProducerRunnable()
    {

    }

    public ProducerRunnable(String topic)
    {
        this.producer=new KafkaProducer(config());
        this.topic=topic;
    }

    public Properties config()
    {
        Properties props = new Properties();
        props.put("bootstrap.servers", "10.1.12.202:9092,10.1.12.203:9092,10.1.12.204:9092");
        // 等待所有副本节点的应答,表示partitions写入后，同步到replications在返回ack
        props.put("acks", "all");
        // 消息发送最大尝试次数，当为0的时候表示，produce不会重发，retires重发，replication变为leader
        props.put("retries", 0);
        // 一批消息处理大小，16k，数据累积到一定量一次发送，推荐设置为512k
        props.put("batch.size", 16384);
        //心跳超时的时间阈值，超过阈值出发rebalance
        props.put("session.timeout.ms", 30000);
        // 增加服务端请求延时
        props.put("linger.ms", 1);
        // 当数据写入kafka的速度慢于接收的速度，会缓存至内存，这个就是内存的大小设置，默认是32M，一般设置为128M-1G
        props.put("buffer.memory", 33554432);
        // key序列化
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        // value序列化
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        return props;
    }


    @Override
    public void run()
    {
        try {
            //生产消息
            while(true)
            {
                String msg="msg_"+new Random().nextInt(100000);
                producer.send(new ProducerRecord(topic, msg), new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                        if(e!=null)
                        {
                            e.printStackTrace();
                        }else
                        {
                            System.out.println("##producer##--offset:"+recordMetadata.offset()+",partitions:"+recordMetadata.partition());
                        }
                    }
                });

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e)
        {
            //发生异常，关闭生产者
            e.printStackTrace();
            if(producer!=null)
            {
                producer.close();
            }
        }
    }

    public Producer getProducer() {
        return producer;
    }

    public void setProducer(Producer producer) {
        this.producer = producer;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }


}
