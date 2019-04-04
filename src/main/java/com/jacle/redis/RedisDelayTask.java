package com.jacle.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Tuple;

import java.util.Calendar;
import java.util.Set;

/**
 * 通过zset的score设置事件，定时获取score最小的数值，判断是否满足触发事件的条件
 *
 * 还可以通过redis的pub、sub机制，但是这种机制不是很稳定，主要是通过失效key的回调函数来实现delay的控制
 *
 */
public class RedisDelayTask
{
    private Jedis jedis;
    {
        JedisPoolConfig jedisPoolConfig=new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(10);
        jedisPoolConfig.setMaxTotal(30);

        JedisPool pool=new JedisPool(jedisPoolConfig,"10.1.12.206",6388);
        jedis=pool.getResource();
    }
    public static void main(String[] args)
    {
        RedisDelayTask redisDelayTask=new RedisDelayTask();
        redisDelayTask.delayTask();
        redisDelayTask.runDelayTask();
    }

    public void delayTask()
    {
        for(int i=0;i<5;i++)
        {
            //添加当时任务
            Calendar calendar=Calendar.getInstance();
            calendar.add(Calendar.SECOND,3);

            jedis.zadd("delayTask",calendar.getTimeInMillis()/1000,"task-"+i);
            System.out.println(System.currentTimeMillis()+"--order:task-"+i);
        }
    }

    public void runDelayTask()
    {
        //去除当前的第一个zset的元素，进行判断是否执行
        while(true)
        {
              Set<Tuple> zset=jedis.zrangeWithScores("delayTask",0,0);
              if(zset==null||zset.isEmpty())
              {
                  try {
                      Thread.sleep(500);
                  } catch (InterruptedException e) {
                      e.printStackTrace();
                  }
                  continue;
              }else
              {
                  int score=(int)((Tuple)zset.toArray()[0]).getScore();
                  Calendar calendar=Calendar.getInstance();
                  int nowSecond=(int) (calendar.getTimeInMillis()/1000);
                   if(nowSecond>=score)
                   {
                       String orderId=((Tuple)zset.toArray()[0]).getElement();

                       //因为redis的删除是单线程阻塞的，所以这里可以不适用锁
                       long returnCode=jedis.zrem("delayTask",orderId);
                       if(returnCode>0)
                       {
                           System.out.println("删除了orderId:"+orderId);
                       }
                   }
              }
        }

    }

}
