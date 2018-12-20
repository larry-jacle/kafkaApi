package com.jacle.kafka.threadmethod2;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.sql.Time;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.*;

public class ConsumerHandler
{
    private KafkaConsumer consumer;
    private String groupid;
    private ExecutorService executors;

    public ConsumerHandler()
    {

    }

    public ConsumerHandler(String topic, String groupid) {
        this.groupid = groupid;
        this.consumer = new KafkaConsumer(getConfig());
        this.consumer.subscribe(Arrays.asList(topic));
    }

    public Properties getConfig()
    {
        Properties props = new Properties();
        /* 定义kakfa 服务的地址，不需要将所有broker指定上 */
        props.put("bootstrap.servers", "10.1.12.202:9092,10.1.12.203:9092,10.1.12.204:9092");
        /* 制定consumer group */
        props.put("group.id", groupid);
        /* 是否自动确认offset */
        //没有使用jps，所以这里采用的是手动提交
        props.put("enable.auto.commit", "false");
        /* 自动确认offset的时间间隔 */
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        /* key的序列化类 */
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        /* value的序列化类 */
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        return props;
    }

    public void execute(int workerNum)
    {
        executors = new ThreadPoolExecutor(workerNum, workerNum, 0L, TimeUnit.MILLISECONDS,new ArrayBlockingQueue(1000), new ThreadPoolExecutor.CallerRunsPolicy());

        //两种方式一样，这是一个consumer，多个worker，也可以多个consumer，多个worker
        while(true)
        {
            ConsumerRecords<String,String> records=consumer.poll(100);

//            for(ConsumerRecord record:records)
            {
                executors.submit(new Worker(records));
            }

        }

    }


    /**
     * 关闭
     */
    public void shutdown() {
        if (consumer != null) {
            consumer.close();
        }
        if (executors != null) {
            executors.shutdown();
        }
        try {
            if (!executors.awaitTermination(10, TimeUnit.SECONDS)) {
                System.out.println("Timeout.... Ignore for this case");
            }
        } catch (InterruptedException ignored) {
            System.out.println("Other thread interrupted this shutdown, ignore for this case.");
            Thread.currentThread().interrupt();
        }
    }

}
