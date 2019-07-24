package com.jacle.kafka.advance;

import org.apache.kafka.clients.consumer.internals.AbstractPartitionAssignor;
import org.apache.kafka.clients.consumer.internals.PartitionAssignor;
import org.apache.kafka.common.TopicPartition;

import java.util.*;

public class SelfPartitionAssignor extends AbstractPartitionAssignor {

    //分区分配方法
    @Override
    public Map<String, List<TopicPartition>> assign(Map<String, Integer> topicPars, Map<String, Subscription> consumerSubscription) {

        Map<String,List<TopicPartition>>  result=new HashMap<>();

        Map<String,List<String>>  topicMapConsumer=new HashMap<>();
        //获取topic对应的consumer列表
        for(String consumer:consumerSubscription.keySet())
        {
            for(String topic:consumerSubscription.get(consumer).topics())
            {
                if(topicMapConsumer.get(topic)==null)
                {
                    List<String>  consumerList=new ArrayList<>();
                    consumerList.add(consumer);
                    topicMapConsumer.put(topic,consumerList);
                }else
                {
                    topicMapConsumer.get(topic).add(consumer);
                }
            }

        }

        //获取topic对应的所有分区
        //因为没有consumer，所有通过获取分区数来new
        for(String tp:topicMapConsumer.keySet())
        {
            Integer tpParNums=topicPars.get(tp);
            int consumerSize=topicMapConsumer.get(tp).size();
            if(tpParNums==null)
            {
                continue;
            }else
            {
               List<TopicPartition> tpPartitions=AbstractPartitionAssignor.partitions(tp,tpParNums);
               for(TopicPartition topicPartition:tpPartitions)
               {
                   int randomInt=new Random().nextInt(consumerSize);
                   String consumer=topicMapConsumer.get(tp).get(randomInt);

                   if(result.get(consumer)==null)
                   {
                       List<TopicPartition>  partitionList=new ArrayList<>();
                       partitionList.add(topicPartition);
                       result.put(consumer,partitionList);

                   }else
                   {
                       result.get(consumer).add(topicPartition);
                   }

               }
            }
        }

        return result;
    }

    @Override
    public String name() {
        return "random";
    }
}
