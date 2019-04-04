package com.jacle.redis;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 通过redis的乐观锁来实现秒杀
 */
public class RedisSecondKill {
    private static String key = "counter";
    private static JedisPool jedisPool;

    static
    {
        //设置连接池的参数
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(10);
        jedisPoolConfig.setMaxTotal(30);
        jedisPool=new JedisPool(jedisPoolConfig,"10.1.12.206",6388);

//        Jedis jedis=new Jedis("10.1.12.206",6388);
        Jedis jedis= jedisPool.getResource();

        //redis的key-value，其中value只能为String
        jedis.set(key, "0");
        jedis.close();
    }

    public static void main(String[] args) {

        ExecutorService executorService= Executors.newFixedThreadPool(200);
        for(int i=0;i<200;i++)
        {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    secondKill();
                }
            });
        }
        executorService.shutdown();
    }


    /**
     * 秒杀
     * <p>
     * 借助redis的事务机制
     */
    public static void secondKill() {
//        Jedis jedis =new Jedis("10.1.12.206",6388);
        Jedis jedis=jedisPool.getResource();

        jedis.watch(key);
        String valNum=jedis.get(key);

        int val=Integer.parseInt(valNum);
        if(val>=10)
        {
//            System.out.println(Thread.currentThread().getName()+" failed");
            return;
        }else
        {
            Transaction tx = jedis.multi();
            tx.incr(key);
            List<Object> result=tx.exec();

            if(result==null||result.size()==0)
            {
//                System.out.println(Thread.currentThread().getName()+" failed");
            }else
            {
                System.out.println(Thread.currentThread().getName()+" succeed");
            }

        }

        jedis.close();
    }

}
