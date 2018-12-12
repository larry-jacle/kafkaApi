package com.jacle.kafka.threadmethod;

import java.util.ArrayList;
import java.util.List;

public class ConsumerGroup
{
    //消费组
    private String groupId;
    private String topic;
    private int nums;
    private List<Runnable> list=new ArrayList<Runnable>();

    public ConsumerGroup(String groupId,int nums,String topic)
    {
        for (int i=0;i<nums;i++)
        {
            ConsumerRunnable consumerRunnable=new ConsumerRunnable(topic,groupId,i);
            list.add(consumerRunnable);
        }

    }

    public ConsumerGroup()
    {

    }

    public void start()
    {
        for (Runnable runnable:list)
        {
            //这里还是会出现两个线程抢同一个分片的风险
            Thread t=new Thread(runnable);
            t.start();
        }
    }

}
