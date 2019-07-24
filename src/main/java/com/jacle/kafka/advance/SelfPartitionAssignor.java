package com.jacle.kafka.advance;

import org.apache.kafka.clients.consumer.internals.AbstractPartitionAssignor;
import org.apache.kafka.clients.consumer.internals.PartitionAssignor;
import org.apache.kafka.common.TopicPartition;

import java.util.List;
import java.util.Map;

public class SelfPartitionAssignor extends AbstractPartitionAssignor {
    @Override
    public Map<String, List<TopicPartition>> assign(Map<String, Integer> map, Map<String, Subscription> map1) {
        return null;
    }

    @Override
    public String name() {
        return null;
    }
}
