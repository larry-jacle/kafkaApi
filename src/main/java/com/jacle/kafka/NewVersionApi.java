package com.jacle.kafka;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.io.InputStream;
import java.util.Properties;

public class NewVersionApi
{
    public void newApi() throws  Exception
    {
        Properties ps=new Properties();
        InputStream in=NewVersionApi.class.getClassLoader().getResourceAsStream("kafka.producer.properties");
        ps.load(in);

        KafkaProducer<String,String> producer=new KafkaProducer<String, String>(ps);
        ProducerRecord<String,String> record=null;
        for(int i=0;i<100;i++)
        {
            record=new ProducerRecord<String, String>("test_queue","key","msg"+i);
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e)
                {
                    if(e!=null)
                    {
                        e.printStackTrace();
                    }else
                    {
                        System.out.println("current offset is "+recordMetadata.offset());
                    }
                }
            });
            System.out.println("消息发送..");
        }

        producer.close();
    }

}
