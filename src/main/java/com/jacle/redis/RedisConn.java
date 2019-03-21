package com.jacle.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


/**
 * java访问redis的方式
 */
public class RedisConn
{
    public static void main(String[] args)
    {
        new RedisConn().nativeMethod();
    }


    /**
     * 原生API方法
     */
    public void nativeMethod()
    {
        Jedis jedis=new Jedis("10.1.12.206",6379);
        jedis.auth("redis");

        String word=jedis.get("haha");
        System.out.println(word);

        jedis.close();
    }

    /**
     * redis连接池的方式
     */
    public void poolMethod()
    {
        //设置连接池的参数
        JedisPoolConfig jedisPoolConfig=new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(10);
        jedisPoolConfig.setMaxTotal(30);

        JedisPool pool=new JedisPool(jedisPoolConfig,"10.1.12.206",6379);
        Jedis jedis=pool.getResource();
        jedis.auth("redis");

        jedis.set("word","wordValue");
        //及时提交
        jedis.flushDB();
        String word=jedis.get("haha");
        System.out.println(word);

        jedis.close();
        pool.close();
    }
}
