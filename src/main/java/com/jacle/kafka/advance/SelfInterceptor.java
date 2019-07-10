package com.jacle.kafka.advance;

import com.jacle.lombok.Company;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;

public class SelfInterceptor implements ProducerInterceptor<String,Company>
{
    @Override
    public ProducerRecord<String, Company> onSend(ProducerRecord<String, Company> producerRecord) {

        Company company=producerRecord.value();
        company.setCompanyName("prefix-"+company.getCompanyName());
        return new ProducerRecord<String, Company>(producerRecord.topic(),producerRecord.partition(),producerRecord.timestamp(),
                producerRecord.key(),company,producerRecord.headers());
    }

    @Override
    public void onAcknowledgement(RecordMetadata recordMetadata, Exception e) {
        if(e==null)
        {
            System.out.println("interceptor:确认收到!");
        }else
        {
            System.out.println("发送失败!");
        }
    }

    @Override
    public void close()
    {
        System.out.println("发送完成关闭拦截器的时候执行...");
    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}
