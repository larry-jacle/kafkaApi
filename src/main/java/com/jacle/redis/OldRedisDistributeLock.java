package com.jacle.redis;


import java.util.concurrent.TimeUnit;

/**
 * 旧版本的Redis分布式锁的实现
 * 通过setnx
 *     setget来实现
 *
 *
 *  缺点：
 *
 */
public class OldRedisDistributeLock
{
    public static void main(String[] args)
    {

    }

    public boolean getLock(long acquireTimeout,TimeUnit timeUnit)
    {

        long timeToMillis=timeUnit.toMillis(acquireTimeout);
        //计算过期时间
        long acquiteTime=System.currentTimeMillis()+timeToMillis;



        return false;
    }

    public void tryLock()
    {

    }
}
