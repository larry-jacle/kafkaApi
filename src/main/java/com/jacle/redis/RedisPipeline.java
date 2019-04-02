package com.jacle.redis;

import redis.clients.jedis.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * RedisPipeline的使用
 * 批处理提交命令;
 */
public class RedisPipeline {
    public static void main(String[] args) {
//            new RedisPipeline().pipeline();
              new RedisPipeline().pipelineGetAndKey();
    }


    /**
     * pipeline批量set
     * @throws InterruptedException
     */
    public void pipeline() throws InterruptedException {
        //设置连接池的参数
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(10);
        jedisPoolConfig.setMaxTotal(30);

        JedisPool pool = new JedisPool(jedisPoolConfig, "10.1.12.206", 6387);
        Jedis jedis = pool.getResource();

        //普通的提交命令的方式和延迟
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            jedis.set(String.valueOf(i), String.valueOf(i));
        }
        long end = System.currentTimeMillis();
        System.out.println("the jedis total time is:" + (end - start));


        //通过pipeline的方式来提交，两种方式耗时进行对比
        //多次设置kv，还是使用pipeline比较节省时间
        Pipeline pipeline = jedis.pipelined();
        start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            pipeline.set(String.valueOf(i), String.valueOf(i));
        }
        end = System.currentTimeMillis();
        System.out.println("the pipeline total time is:" + (end - start));
        //获取所有的responese
        //进行下一步操作的时候，要进行等待同步执行完成；
        pipeline.sync();

        //pipeline内部的实现，就是通过blockingqueue
        BlockingQueue<String> logQueue = new LinkedBlockingQueue<String>();
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            logQueue.put("i=" + i);
        }
        long stop = System.currentTimeMillis();
        System.out.println("the BlockingQueue total time is:" + (stop - begin));

        jedis.close();
        pool.destroy();
    }

    public void pipelineGet()
    {
        //设置连接池的参数
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(10);
        jedisPoolConfig.setMaxTotal(30);

        JedisPool pool = new JedisPool(jedisPoolConfig, "10.1.12.206", 6387);
        Jedis jedis = pool.getResource();

        Pipeline pipeline = jedis.pipelined();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            //pipeline通过字节的形式来发送，比较好
            //如果通过字符串来发送，返回的就是字符串
            //但是字节类型比较通用
            pipeline.get(java.lang.String.valueOf(i).getBytes());
        }
        long end = System.currentTimeMillis();
        System.out.println("the pipeline total time is:" + (end - start));
        //获取所有的responese
        //进行下一步操作的时候，要进行等待同步执行完成；
        List<Object> list=pipeline.syncAndReturnAll();
        byte[] tmp=null;
        for(Object obj:list)
        {
           tmp=(byte[])obj;
            System.out.println(new String(tmp));
        }

        jedis.close();
        pool.destroy();


    }


    /**
     * 通过map来构造有key的批量查询
     */
    public void pipelineGetAndKey()
    {
        //设置连接池的参数
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(10);
        jedisPoolConfig.setMaxTotal(30);

        JedisPool pool = new JedisPool(jedisPoolConfig, "10.1.12.206", 6387);
        Jedis jedis = pool.getResource();

        Pipeline pipeline = jedis.pipelined();
        Map<byte[],Response<byte[]>> resultMap=new HashMap<byte[],Response<byte[]>>();

        long start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            //pipeline通过字节的形式来发送，比较好
            //如果通过字符串来发送，返回的就是字符串
            //但是字节类型比较通用
            resultMap.put(java.lang.String.valueOf(i).getBytes(),pipeline.get(java.lang.String.valueOf(i).getBytes()));
        }

        long end = System.currentTimeMillis();
        System.out.println("the pipeline total time is:" + (end - start));
        //获取所有的responese
        //进行下一步操作的时候，要进行等待同步执行完成；
        pipeline.sync();

        for(byte[] key:resultMap.keySet())
        {
            System.out.println(new String(key)+","+new String(resultMap.get(key).get()));
        }

        jedis.close();
        pool.destroy();

    }

}
