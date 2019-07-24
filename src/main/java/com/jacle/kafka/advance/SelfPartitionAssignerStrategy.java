package com.jacle.kafka.advance;

import org.apache.kafka.clients.consumer.internals.PartitionAssignor;
import org.apache.kafka.common.Cluster;

import java.util.Map;
import java.util.Set;


/**
 * 自定义分区策略包含用户数据
 */
public class SelfPartitionAssignerStrategy  implements PartitionAssignor {
    @Override
    public Subscription subscription(Set<String> set) {
        return null;
    }

    @Override
    public Map<String, Assignment> assign(Cluster cluster, Map<String, Subscription> map) {
        return null;
    }

    //每个消费者收到分区leader的回调函数
    @Override
    public void onAssignment(Assignment assignment) {

    }

    //策略命名
    @Override
    public String name() {
        return null;
    }
}
