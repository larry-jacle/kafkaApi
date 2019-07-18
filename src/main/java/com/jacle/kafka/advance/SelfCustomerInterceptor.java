package com.jacle.kafka.advance;

import com.jacle.lombok.Company;
import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.*;

public class SelfCustomerInterceptor implements ConsumerInterceptor<String,Company>
{
    @Override
    public ConsumerRecords<String, Company> onConsume(ConsumerRecords<String, Company> consumerRecords) {
        //poll之前执行
        //可以用来过滤一些消息
        //有消息的时候才会进行调用
        System.out.println("消费者拦截器onConsume方法调用...");
        Map<TopicPartition,List<ConsumerRecord<String,Company>>> newResult=new HashMap<>();
        Set<TopicPartition> tps=consumerRecords.partitions();
        for(TopicPartition tp:tps)
        {
            List<ConsumerRecord<String,Company>> newTpResult=new ArrayList<>();
            List<ConsumerRecord<String,Company>>  list=consumerRecords.records(tp);
            //遍历可以适用forEach，同时可以操作lambda表达式
            list.forEach(record->{if(record.value().getCompanyName().startsWith("x")){
                newTpResult.add(record);
            }});

            if(!newTpResult.isEmpty())
            {
                newResult.put(tp,newTpResult);
            }
        }

        return new ConsumerRecords<>(newResult);
    }

    @Override
    public void onCommit(Map<TopicPartition, OffsetAndMetadata> map) {
        //commit之后执行
        //显示提交的各个分区的offset
        map.forEach((tp,metadata)->{System.out.println("消费者拦截器回调方法："+tp+":"+metadata.offset());});
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}
