package com.jacle.kafka.threadmethod2;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.internals.Topic;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 处理records上报offset
 */
public class Worker implements Runnable {
    private ConsumerRecords<String, String> records;
    private Map<TopicPartition, OffsetAndMetadata> offsets;

    public Worker() {

    }

    public Worker(ConsumerRecords consumerRecords) {
        this.records = consumerRecords;
    }


    @Override
    public void run() {

        Set<TopicPartition> partitions = records.partitions();

        for (TopicPartition tp : partitions) {
             List<ConsumerRecord<String,String>> prs=records.records(tp);
            for (ConsumerRecord r : prs) {
                // 插入消息处理逻辑，本例只是打印消息
                System.out.println(String.format("topic=%s, partition=%d, offset=%d",
                        r.topic(), r.partition(), r.offset()));
            }

            //本次消息处理的最大offset
            long lastOffset = prs.get(prs.size() - 1).offset();
            //上报offset
            synchronized (offsets) {
                if (!offsets.containsKey(tp)) {
                    offsets.put(tp, new OffsetAndMetadata(lastOffset + 1));
                } else {
                    long curr = offsets.get(tp).offset();
                    if (curr <= lastOffset + 1) {
                        offsets.put(tp, new OffsetAndMetadata(lastOffset + 1));
                    }
                }
            }
        }


    }

}
