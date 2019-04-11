package com.jacle.redis;


import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * 通过redisson来实现分布式锁
 */
public class RedissonLock
{
   private static String lockPrefixName="Redis_LOCK_";
   private static RedissonClient redissonClient=RedisRedssionManager.getRedisson();

   public static void main(String[] args)
   {
      for(int i=0;i<100;i++)
      {
          new Thread(new Runnable() {
              @Override
              public void run() {
                  try {
                      String lockname="testRedissonLock";
                      getLock(lockname);
                      Thread.sleep(2000);
                      releaseLock(lockname);
                  } catch (InterruptedException e) {
                      e.printStackTrace();
                  }

              }
          }).start();
      }

      redissonClient.shutdown();

   }

   public static  void getLock(String lockName)
   {
       String lname=lockPrefixName+lockName;

       RLock lock=redissonClient.getLock(lname);
       lock.lock(2, TimeUnit.MINUTES);
       System.out.println(Thread.currentThread().getName()+"get lock");

   }

    public static  void releaseLock(String lockName)
    {
        String lname=lockPrefixName+lockName;

        RLock lock=redissonClient.getLock(lname);
        lock.unlock();
        System.out.println(Thread.currentThread().getName()+"release lock");

    }


}
