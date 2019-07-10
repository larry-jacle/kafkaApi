package com.jacle.kafka.advance;


import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;

/**
 * kafka拦截器，它是在序列化之前执行
 * <p>
 * 继承接口的时候，指明了key-value的类型
 * interceptor只在io线程中执行，所以逻辑要简单，不能有耗时的操作
 * interceptor执行优先于callback
 */
public class KafkaInteceptor implements ProducerInterceptor<String, String> {

    @Override
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> producerRecord) {
        //执行拦截器的逻辑
        return new ProducerRecord<String, String>(producerRecord.topic(), producerRecord.partition(),
                producerRecord.timestamp(), producerRecord.key()
                , "interceptor-" + producerRecord.value(),producerRecord.headers());
    }


    //这个方法会在消息被应答之前或消失发送失败的时候执行
    @Override
    public void onAcknowledgement(RecordMetadata recordMetadata, Exception e) {

    }

    @Override
    public void close() {
        //发送完毕，关闭资源时，执行的方法;
    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}
