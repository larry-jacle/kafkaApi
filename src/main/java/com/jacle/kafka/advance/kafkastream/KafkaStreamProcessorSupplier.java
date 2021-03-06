package com.jacle.kafka.advance.kafkastream;

import org.apache.kafka.streams.processor.*;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.Optional;
import java.util.stream.Stream;


/**
 * processor的方式来编写kafka stream
 * 低级别的API
 *
 * 实现PeocessorSupplier的方式
 *
 * 是否可以使用引用，看类最终的实现类型；
 */
public class KafkaStreamProcessorSupplier implements ProcessorSupplier<String,String>
{
    private ProcessorContext processorContext;

    /*类似的map等无状态的transform是不需要statestore的*/
    private KeyValueStore<String,Long> keyValueStore;


    @Override
    public Processor<String, String> get() {

        //有多个实现方法的接口，无法使用lambda
        return new Processor<String, String>() {
            private ProcessorContext processorContext;
            private KeyValueStore<String,Long> keyValueStore;

            @Override
            public void init(ProcessorContext processorContext) {
                this.processorContext=processorContext;
                //PunctuationType.WALL_CLOCK_TIME 是设置的时间有效，定期轮训
                this.processorContext.schedule(1000, PunctuationType.STREAM_TIME, new Punctuator() {
                    @Override
                    public void punctuate(long l) {
                        KeyValueIterator<String,Long> iterator=keyValueStore.all();
                        iterator.forEachRemaining(entry->{
                            processorContext.forward(entry.key,entry.value.toString());
                            //keyValueStore.delete(entry.key);
                        });

                        //这里也可以将statestore的数据存储至数据库等其他持久设备中
                        iterator.close();
                    }
                });

                keyValueStore=(KeyValueStore)processorContext.getStateStore("count2");
            }

            @Override
            public void process(String s, String s2) {
                String[] strArray=s2.split(" ");

                //通过optional的方式来处理空值的if-else判断,借助map和Optional的方法来实现
                Stream.of(strArray).forEach(word->{
                    Optional<Long> wordcount=Optional.ofNullable(keyValueStore.get(word));
                    keyValueStore.put(word,wordcount.map(cout->cout+1).orElse(1L));
                });

                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.processorContext.commit();
            }

            @Override
            public void punctuate(long l) {

            }

            @Override
            public void close() {
               this.keyValueStore.close();
            }
        };
    }
}
