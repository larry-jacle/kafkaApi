package com.jacle.kafka;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;
import java.util.Random;

public class MyPartitioner implements Partitioner
{
    @Override
    public int partition(String s, Object o, byte[] bytes, Object o1, byte[] bytes1, Cluster cluster)
    {
        int partitionNums=cluster.partitionCountForTopic(s);
        int targetPartitionIndex=new Random().nextInt(100)%partitionNums;

        //返回目标index
        return targetPartitionIndex;
    }

    @Override
    public void close()
    {


    }

    @Override
    public void configure(Map<String, ?> map)
    {


    }
}
