package com.jacle.kafka.advance.kafkastream;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;

public class MapperProcessor implements Processor<String, String> {
    private ProcessorContext processorContext;

    @Override
    public void init(ProcessorContext processorContext) {
        this.processorContext=processorContext;
    }

    @Override
    public void process(String s, String s2) {
       //如果无状态，这里直接传输到下游
       processorContext.forward(s,"map-"+s2);
    }

    @Override
    public void punctuate(long l) {

    }

    @Override
    public void close() {

    }
}
