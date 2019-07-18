package com.jacle.kafka.advance;

import com.jacle.kafka.NewVersionApi;
import com.jacle.lombok.Company;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SelfSerializerProducer {

    public static void main(String[] args)
    {
        try {
            produceMsg();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void  produceMsg() throws IOException, ExecutionException, InterruptedException {
        Properties ps = new Properties();
        //区分发送者
        //通过producerConfig变量来替代之前的key，防止书写错误
        ps.setProperty(ProducerConfig.CLIENT_ID_CONFIG,"producer0");
        ps.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"10.1.12.202:9092");
        ps.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
//        ps.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,SelfSerializer.class.getName());

        ps.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,ProtobuffSerializer.class.getName());
        //设定分区器
        ps.setProperty(ProducerConfig.PARTITIONER_CLASS_CONFIG,SelfPartitioner.class.getName());
        //设定拦截器
//        ps.setProperty(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG,SelfInterceptor.class.getName());

        //KafkaProducer是线程安全的，可以用来被多个线程共享这个变量，引用
        KafkaProducer<String, Company> producer = new KafkaProducer<>(ps);

        //可以设置headers，表示的是消息的头
//        ProducerRecord<String,Company> record=new ProducerRecord<>("test",0,"key-0", Company.builder().companyName("xcompany1").code("code123").build());
        ProducerRecord<String,Company> record=new ProducerRecord<>("test",Company.builder().companyName("xcompany1").code("code123").build());


        //带有回调函数的发送者
        //消息发送分为三种形式：发送不管返回、同步、异步


        //直接使用set方法是异步发送
        //send方法会返回future，可以通过get方法来阻塞，从而实现同步
        //异步发送消息
        //Future<RecordMetadata> future = getRecordMetadataFuture(producer, record);

        //同步发送
        for(int i=0;i<3;i++)
        {
            RecordMetadata recordMetadata=producer.send(record).get();
        }


        //System.out.println(future.toString()+"##"+recordMetadata.offset());

        //发送者要进行close，才会真正发送出去
        producer.close();
        System.out.println("发送动作完成.");
    }



    //异步发送消息
    private static Future<RecordMetadata> getRecordMetadataFuture(KafkaProducer<String, Company> producer, ProducerRecord<String, Company> record) {
        return producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    if(e!=null)
                    {
                        e.printStackTrace();
                    }else
                    {
                        //获取metadata，可能需要里面的信息
                        System.out.println("current offset is "+recordMetadata.offset());
                        System.out.println("消息发送完毕!");
                    }
                }
            });
    }
    

}
