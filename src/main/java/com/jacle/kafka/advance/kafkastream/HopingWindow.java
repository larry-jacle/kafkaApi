package com.jacle.kafka.advance.kafkastream;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;

import java.time.Duration;
import java.time.Instant;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 跳跃窗口,窗口之间有重叠
 *
 */
public class HopingWindow
{
    private static final CountDownLatch latch = new CountDownLatch(1);
    private static final long WINDOWSTIME=9000L;
    private static final long ADVANCED=3000L;

    //窗口关闭的延迟时间
    private static final long GRACE=0L;

    public static void main(String[] args)
    {
        //北京时间需要多加8个小时,LocalDate等本地时间类，没有此类问题
        Instant now=Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8));

        Properties props = new Properties();

        //每次部署的时候，需要更改ApplicationId
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "ks2-trupling");
        //日志级别
        props.put(StreamsConfig.METRICS_RECORDING_LEVEL_CONFIG, "INFO");
        //每个任务的副本数，当任务失败的时候，会切换到副本机器执行，减少初始化时间
        props.put(StreamsConfig.NUM_STANDBY_REPLICAS_CONFIG, "2");
        //设置线程的个数，同时可以开启多个实例，从而形成多实例部署
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, "2");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "s203:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "500");
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);

        //位移复位设置
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KStream<String, String> source = streamsBuilder.stream("truplingwindow");

        Instant currTime=Instant.now();
        //Ktable变为Kstream，使用to


        //时间窗口的时间戳是取的消息的时间，而不是根据建立的时候获取的时间
        //多个流同时处理，每个时间窗口的处理都是并行的

        source.selectKey((k, v)->k).groupByKey().windowedBy(TimeWindows.of(HopingWindow.WINDOWSTIME).advanceBy(HopingWindow.ADVANCED))
//                .count(Materialized.with(Serdes.String(),Serdes.Long()))
                //可以通过aggrate来聚合窗口
                .aggregate(()-> 0L,(key,val,curr)->{curr++;System.out.println("聚合计算：key=" + key + ",value=" + val + "\nafter aggr, curValue=" + curr);return curr;},Materialized.with(Serdes.String(),Serdes.Long()))
                .toStream()
                //根据消息的入库时间，来进行窗口的处理，防止程序暂停止之后重启，消费之前的数据
                //历史数据消费之后，不会再重新读取
                .filterNot((key,value)->KafkaStreamUtil.isOld(key,value,currTime))
//                .print(Printed.toSysOut());
                //有参数的引用，输入参数就是lambda的所有参数
                .foreach(KafkaStreamUtil::showWindowedInfo);

        final Topology topology = streamsBuilder.build();
        KafkaStreams kafkaStreams = new KafkaStreams(topology, props);

        //正常关闭
        Runtime.getRuntime().addShutdownHook(new Thread("streamShutThread") {
            @Override
            public void run() {
                kafkaStreams.close();
                latch.countDown();
                System.out.println("程序关闭...");
            }
        });


        try {
            kafkaStreams.start();

            //latch让程序不停止，跟while的方式一致，但是比while的方式优雅
            //阻塞程序
            System.out.println("程序已启动");
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.exit(0);

    }


}
