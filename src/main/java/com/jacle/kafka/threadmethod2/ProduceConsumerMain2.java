package com.jacle.kafka.threadmethod2;

import com.jacle.kafka.threadmethod.ProducerRunnable;

public class ProduceConsumerMain2
{
    public static void main(String[] args)
    {
        //生产者是线程安全的，消费者不是线程安全的。
        String topic="topicThread_3";
        Thread produceThread=new Thread(new ProducerRunnable(topic));
        ConsumerHandler consumerHandler=new ConsumerHandler(topic,"group1");


        produceThread.start();
        consumerHandler.execute(3);
    }
}
