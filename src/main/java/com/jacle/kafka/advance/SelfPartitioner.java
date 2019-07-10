package com.jacle.kafka.advance;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.utils.Utils;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * key为null的消息只能发送到可用分区，修改默认的分区器，使其能够发送到所有分区
 */
public class SelfPartitioner implements Partitioner
{
    private AtomicInteger counter=new AtomicInteger(0);

    @Override
    public int partition(String s, Object key, byte[] bytes, Object o1, byte[] bytes1, Cluster cluster) {

        int partitionNum=cluster.partitionsForTopic(s).size();
        if(Objects.isNull(key))
        {
            return counter.getAndIncrement()%partitionNum;
        }else
        {
            return Utils.toPositive(Utils.murmur2(bytes))%partitionNum;
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}
