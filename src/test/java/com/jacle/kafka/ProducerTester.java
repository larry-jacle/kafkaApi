package com.jacle.kafka;

import org.apache.kafka.clients.producer.*;
import org.junit.Test;

import java.util.Properties;

public class ProducerTester
{

    @Test
    public void testProducer()
    {
        Properties props = new Properties();
        props.put("bootstrap.servers", "10.1.12.202:9092,10.1.12.203:9092,10.1.12.204:9092");
        // 等待所有副本节点的应答,表示partitions写入后，同步到replications在返回ack
        props.put("acks", "all");
        // 消息发送最大尝试次数，当为0的时候表示，produce不会重发，retires重发，replication变为leader
        props.put("retries", 0);
        // 一批消息处理大小，16k，数据累积到一定量一次发送，推荐设置为512k
        props.put("batch.size", 16384);
        // 增加服务端请求延时
        props.put("linger.ms", 1);
        // 当数据写入kafka的速度慢于接收的速度，会缓存至内存，这个就是内存的大小设置，默认是32M，一般设置为128M-1G
        props.put("buffer.memory", 33554432);
        // key序列化
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        // value序列化
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        Producer<String, String> producer = null;
        try {
            producer = new KafkaProducer<String, String>(props);
            for (int i = 0; i < 100; i++) {
                String msg = "Message " + i;
                final long  currtime=System.currentTimeMillis();
                producer.send(new ProducerRecord<String, String>("topic_1", msg), new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                        if(e!=null)
                        {
                            e.printStackTrace();
                        }else
                        {
                            System.out.println("curr offset:"+recordMetadata.offset()+"to partitions"+recordMetadata.partition()+" spend "+(System.currentTimeMillis()-currtime)+"ms"+recordMetadata.timestamp());
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            producer.close();
        }
    }


}
