package com.jacle.kafka.advance.kafkaadmin;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.logging.log4j.core.config.ConfigurationSource;

import java.util.*;
import java.util.concurrent.ExecutionException;


/**
 * 可以通过AdminClient来对topic、broker、配置、acl
 *
 */
public class KafkaManageClient
{
    private AdminClient adminClient;
    private static String brokerList="s204:9092";
    private String topicName="newtopic";
    private String topicName_assignment="newtopic-assignment";

     {
        Properties ps=new Properties();
        ps.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,brokerList);
        ps.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG,30000);

        //构建admin管理对象
        adminClient=AdminClient.create(ps);

    }

    //自动副本策略分配分区
    public void createTopic()
    {
        NewTopic newTopic=new NewTopic(topicName,3,(short) 2);
        //自动分配副本
        CreateTopicsResult topicsResult=adminClient.createTopics(Collections.singleton(newTopic));
        try {
            topicsResult.all().get();
        } catch (InterruptedException e) {

        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        adminClient.close();
    }


    public void createTopicByAssignment()
    {
        Map<Integer,List<Integer>>  replicasAssignment=new HashMap<>();

        //手动指定各个分区的副本存放地（brokerId）
        replicasAssignment.put(0,Arrays.asList(158));
        replicasAssignment.put(1,Arrays.asList(157));
        replicasAssignment.put(2,Arrays.asList(159));

        NewTopic newTopic=new NewTopic(topicName_assignment,replicasAssignment);

        //手动分配副本
        CreateTopicsResult topicsResult=adminClient.createTopics(Collections.singleton(newTopic));
        try {
            topicsResult.all().get();
        } catch (InterruptedException e) {

        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        adminClient.close();
    }


    //显示资源配置的参数,列出的不仅仅是覆盖的配置，列出的是所有的配置信息
    public void getResource()
    {
        ConfigResource configResource=new ConfigResource(ConfigResource.Type.TOPIC,topicName);
        DescribeConfigsResult result=adminClient.describeConfigs(Collections.singleton(configResource));
        try {
            Config config=result.all().get().get(configResource);
            System.out.println(config);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        adminClient.close();
    }


    //更新配置
    public void alterConfig()
    {
        ConfigResource configResource=new ConfigResource(ConfigResource.Type.TOPIC,topicName);

        //config
        ConfigEntry configEntry=new ConfigEntry("cleanup.policy","compact");
        Config config=new Config(Arrays.asList(configEntry));

        Map<ConfigResource,Config> configs=new HashMap<>();
        configs.put(configResource,config);

        AlterConfigsResult result=adminClient.alterConfigs(configs);
       /* try {
            result.all().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/

        adminClient.close();
    }


    public static void main(String[] args)
    {
//        new KafkaManageClient().createTopic();
//        new KafkaManageClient().createTopicByAssignment();
          new KafkaManageClient().alterConfig();
          new KafkaManageClient().getResource();
    }
}
