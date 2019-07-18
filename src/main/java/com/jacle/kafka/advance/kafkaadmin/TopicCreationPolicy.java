package com.jacle.kafka.advance.kafkaadmin;

import org.apache.kafka.common.errors.PolicyViolationException;
import org.apache.kafka.server.policy.CreateTopicPolicy;

import java.util.Map;


/**
 * 对创建队列，检验合法性，通常运维应该统一通过页面来提供创建队列，其他口子及时关闭，这样运作更专业
 * create.topic.policy.class.name
 *
 */
public class TopicCreationPolicy implements CreateTopicPolicy {
    @Override
    public void validate(RequestMetadata requestMetadata) throws PolicyViolationException {
        //对topic的设置参数进行验证，是否建立了符合队列规范的topic
        if(requestMetadata.numPartitions()!=null && requestMetadata.replicationFactor()!=null)
        {
            if(requestMetadata.numPartitions()<2)
            {
                throw  new PolicyViolationException("topic should have at least 2 partitions,received"+requestMetadata.numPartitions());
            }

            if(requestMetadata.replicationFactor()<2)
            {
                throw  new PolicyViolationException("topic should have at least 2 replication factor,received"+requestMetadata.replicationFactor());
            }
        }
    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}
