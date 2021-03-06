package com.jacle.redis;

import redis.clients.jedis.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * java访问redis的方式
 */
public class RedisConn {
    public static void main(String[] args) {
        new RedisConn().testGetSet();
    }


    /**
     * 原生API方法
     */
    public void nativeMethod() {
        Jedis jedis = new Jedis("10.1.12.206", 6379);
        jedis.auth("redis");

        String word = jedis.get("haha");
        System.out.println(word);

        jedis.close();
    }

    /**
     * redis连接池的方式
     */
    public void poolMethod() {
        //设置连接池的参数
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(10);
        jedisPoolConfig.setMaxTotal(30);

        JedisPool pool = new JedisPool(jedisPoolConfig, "10.1.12.206", 6379);
        Jedis jedis = pool.getResource();
        jedis.auth("redis");

        jedis.set("word", "wordValue");
        //及时提交
        jedis.flushDB();
        String word = jedis.get("haha");
        System.out.println(word);

        jedis.close();
        pool.close();
    }

    public void connCluster() throws IOException {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        // 最大连接数
        poolConfig.setMaxTotal(100);
        // 最大空闲数
        poolConfig.setMaxIdle(10);
        // 最大允许等待时间，如果超过这个时间还未获取到连接，则会报JedisException异常：
        // Could not get a resource from the pool
        poolConfig.setMaxWaitMillis(1000);

        Set<HostAndPort> set = new HashSet<HostAndPort>();
        set.add((new HostAndPort("10.1.12.206", 6380)));
        set.add((new HostAndPort("10.1.12.206", 6381)));
        set.add((new HostAndPort("10.1.12.206", 6382)));
        set.add((new HostAndPort("10.1.12.206", 6383)));
        set.add((new HostAndPort("10.1.12.206", 6384)));
        set.add((new HostAndPort("10.1.12.206", 6385)));

        JedisCluster cluster = new JedisCluster(set, poolConfig);
        System.out.println(cluster);
        cluster.set("name", "jemdsdfsd");
        String name = cluster.get("name");
        System.out.println(name);

        cluster.close();
    }

    public void testsetnx() {


        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                Jedis jedis = new Jedis("10.1.12.206", 6388);
                //阻塞，setnx是一个原子操作
                long returnVal = jedis.setnx("testsetnx19", String.valueOf(123));
                System.out.println(Thread.currentThread().getName()+"#"+returnVal+","+jedis.get("testsetnx19"));
                jedis.close();
            }
        };

        ExecutorService pool= Executors.newFixedThreadPool(100);
        for(int i=0;i<100;i++)
        {
            pool.submit(runnable);
        }

        pool.shutdown();
    }

    public void testGetSet() {

        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                Jedis jedis = new Jedis("10.1.12.206", 6388);
                //阻塞，getset也是原子操作
                String returnVal = jedis.getSet("testsetnx19", String.valueOf(Thread.currentThread().getName()));
                System.out.println(Thread.currentThread().getName()+"#returnVal:"+returnVal+","+jedis.get("testsetnx19"));
                jedis.close();
            }
        };

        ExecutorService pool= Executors.newFixedThreadPool(100);
        for(int i=0;i<100;i++)
        {
            pool.submit(runnable);
        }

        pool.shutdown();
    }
}
