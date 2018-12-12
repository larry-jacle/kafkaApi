package com.jacle.kafka.threadmethod;

public class ProduceConsumerMain1
{
    public static void main(String[] args)
    {
        //生产者是线程安全的，消费者不是线程安全的。
        String topic="topicThread_1";
        Thread produceThread=new Thread(new ProducerRunnable(topic));
        ConsumerGroup consumerGroup=new ConsumerGroup("group1",1,topic);

        //开启线程
        produceThread.start();
        consumerGroup.start();

    }
}
