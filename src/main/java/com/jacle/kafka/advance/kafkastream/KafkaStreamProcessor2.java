package com.jacle.kafka.advance.kafkastream;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.Optional;
import java.util.stream.Stream;


/**
 * processor的方式来编写kafka stream
 * 低级别的API
 */
public class KafkaStreamProcessor2 implements Processor<String,String>
{
    private ProcessorContext processorContext;
    private KeyValueStore<String,Long> keyValueStore;

    //初始化，可以设置事件窗口的周期
    @Override
    public void init(ProcessorContext processorContext) {
       this.processorContext=processorContext;
       this.processorContext.schedule(1000);
       //定义了名称为count的状态存储器
       keyValueStore=(KeyValueStore)processorContext.getStateStore("count");
    }

    //收到的数据执行对仓库状态的操作,每读取到一条数据，这个方法都会执行一遍
    //计算的方法写到这里
    @Override
    public void process(String s, String s2) {
        String[] strArray=s2.split(" ");
        /*
        for(String w:strArray)
        {
            Long old=this.keyValueStore.get(w);

            //对于这种非空值的处理，使用optional
            if (old == null) {
                this.keyValueStore.put(w, new Long(1));
            } else {
                keyValueStore.put(w, old + 1);
            }
        }
        */

        //通过optional的方式来处理空值的if-else判断,借助map和Optional的方法来实现
        Stream.of(strArray).forEach(word->{
            Optional<Long> wordcount=Optional.ofNullable(keyValueStore.get(word));
            keyValueStore.put(word,wordcount.map(cout->cout+1).orElse(1L));
        });


    };


    //周期的执行,周期性的执行该方法，周期时间在init方法中调用schedule方法设置。
    @Override
    public void punctuate(long l) {
        //周期性执行显示遍历结果
        KeyValueIterator<String,Long> iterator=keyValueStore.all();
//        while(iterator.hasNext())
//        {
//            KeyValue<String,Long> entry=iterator.next();
//            //System.out.println(entry.value);
//            processorContext.forward(entry.key,entry.value.toString());
//        };

        iterator.forEachRemaining(entry->{
            processorContext.forward(entry.key,entry.value.toString());
            //keyValueStore.delete(entry.key);
        });


        iterator.close();
        processorContext.commit();

    }


    //关闭资源
    @Override
    public void close() {
        this.keyValueStore.close();
    }
}
