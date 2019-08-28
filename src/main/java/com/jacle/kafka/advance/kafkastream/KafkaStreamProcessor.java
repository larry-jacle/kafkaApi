package com.jacle.kafka.advance.kafkastream;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;


/**
 * processor的方式来编写kafka stream
 */
public class KafkaStreamProcessor implements Processor<String,String>
{
    //初始化，可以设置事件窗口的周期
    @Override
    public void init(ProcessorContext processorContext) {

    }

    //收到的数据执行对仓库状态的操作,每读取到一条数据，这个方法都会执行一遍
    @Override
    public void process(String s, String s2) {

    };


    //周期的执行,周期性的执行该方法，周期时间在init方法中调用schedule方法设置。
    @Override
    public void punctuate(long l) {

    }


    //关闭资源
    @Override
    public void close() {

    }
}
