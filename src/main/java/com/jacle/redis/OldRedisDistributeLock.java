package com.jacle.redis;


import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 旧版本的Redis分布式锁的实现
 * 通过setnx
 * setget来实现
 * <p>
 * <p>
 * 缺点：
 *    无法区分锁的拥有者
 *    锁释放的时候可能会误删
 */
public class OldRedisDistributeLock {
    private String lockKey=UUID.randomUUID().toString();
    private long expireTime;
    private boolean lockState;
    private Thread threadWithLock;
    private Jedis jedis;


    public Thread getThreadWithLock() {
        return threadWithLock;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Jedis jedis = new Jedis("10.1.12.206", 6388);
                    OldRedisDistributeLock oldRedisDistributeLock = new OldRedisDistributeLock(jedis, 2000);
                    try {
                        oldRedisDistributeLock.getDistributeLockWithRetry(3000, TimeUnit.MILLISECONDS);
                        System.out.println(oldRedisDistributeLock.getThreadWithLock().getName() + "获得锁");
                    } finally {
                        oldRedisDistributeLock.unlock();
                    }

                }
            }).start();
        }
    }

    public OldRedisDistributeLock() {


    }

    public OldRedisDistributeLock(Jedis jedis, long expireTime) {
        this.jedis = jedis;
        this.expireTime = expireTime;
    }


    /**
     * 阻塞式获取锁，支持重试
     *
     * @return
     */
    public boolean getDistributeLockWithRetry(long timeout, TimeUnit timeoutunit) {
        long currentTime = System.currentTimeMillis();
        long lockTryTimeout = timeoutunit.toMillis(timeout);

        //开始进行时间判断循环进行重试
        while (System.currentTimeMillis() - currentTime < lockTryTimeout) {
            //计算lock的expiretime
            String expireTimeStr = System.currentTimeMillis() + expireTime + "";

            //通过setnx来设置，检测是否获取锁
            if (jedis.setnx(lockKey, expireTimeStr) ==1) {
                lockState = true;
                threadWithLock = Thread.currentThread();

                return true;
            }

            //如果没有获取到锁，1、下次进行重试 2、检测获取锁的线程未释放锁的情况
            //当读取的时候，expire了，线程get的可能为nil
            String lockVal = jedis.get(lockKey);


            if (lockVal != null && System.currentTimeMillis() > Long.parseLong(lockVal)) {
                //处理原来获取锁的线程未释放锁的情况,重新设置expireTime，并修改当前线程的锁状态
                //getset的方法使用是为了区分哪个Thread是第一个执行getset的方法

                //这里出现问题，虽然第一个执行getset的方法能够被区分，但是expiretimestr会被覆盖，但是没有影响ss
                String oldVal = jedis.getSet(lockKey, expireTimeStr);

                //第一个执行getset方法的Thread
                if (oldVal != null && oldVal.equals(lockVal)) {
                    lockState = true;
                    jedis.expire(lockKey, (int) (expireTime / 1000));
                    threadWithLock = Thread.currentThread();
                    return true;
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


        return false;
    }


    /**
     * 应该要添加锁的标识，只能删除自己获得的锁，防止误删，下面的写法，可能会在超时的时候误删锁
     */
    public void unlock() {
        if (Thread.currentThread() != threadWithLock) {
            System.out.println("锁并非当前线程持有，非当前线程释放;");
        }else {
            jedis.del(lockKey);
            threadWithLock = null;
        }
    }


    /**
     * 非阻塞，立即返回是否获取到锁
     * 只进行一次的竞争
     * @return
     */
    public boolean tryLock(String lockVal) {
        if (setnx(lockKey, lockVal)) { // 获取到锁
            //  成功获取到锁, 设置相关标识
            lockState= true;
            //setExclusiveOwnerThread(Thread.currentThread());
            threadWithLock = Thread.currentThread();
            return true;
        }
        return false;
    }

    private boolean setnx(String lockKey, Object val) {
        if (jedis.setnx(lockKey, String.valueOf(val))==1) {
            jedis.expire(lockKey,(int)(expireTime/1000) );
            return true;
        }
        return false;
    }

    public boolean getLockState()
    {
        return this.lockState;
    }

}
