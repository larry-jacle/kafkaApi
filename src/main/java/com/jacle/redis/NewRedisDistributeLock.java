package com.jacle.redis;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 新版本的Redis分布式锁的实现
 * 通过setnx
 * setget来实现
 * <p>
 * <p>
 *
 */
public class NewRedisDistributeLock {
    private String lockKey=UUID.randomUUID().toString();
    private long expireTime;
    private boolean lockState;
    private Thread threadWithLock;
    private Jedis jedis;
    private static final Long RELEASE_SUCCESS = 1L;
    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";

    public Thread getThreadWithLock() {
        return threadWithLock;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            final int index=i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Jedis jedis = new Jedis("10.1.12.206", 6388);
                    NewRedisDistributeLock newRedisDistributeLock = new NewRedisDistributeLock(jedis, 2000);
                    try {
                        boolean flag=newRedisDistributeLock.getDistributeLockWithRetry(3000,TimeUnit.MILLISECONDS,"request-"+index);
                        System.out.println("request-"+index+(flag?"获得锁":"未获得锁"));
                    } finally {
                        newRedisDistributeLock.unlock("request-"+index);
                    }

                }
            }).start();
        }
    }

    public NewRedisDistributeLock() {


    }

    public NewRedisDistributeLock(Jedis jedis, long expireTime) {
        this.jedis = jedis;
        this.expireTime = expireTime;
    }


    /**
     * 阻塞式获取锁，支持重试
     *
     * @return
     */
    public boolean getDistributeLockWithRetry(long timeout, TimeUnit timeoutunit,String requestId) {
        long currentTime = System.currentTimeMillis();
        long lockTryTimeout = timeoutunit.toMillis(timeout);

        //开始进行时间判断循环进行重试
        while (System.currentTimeMillis() - currentTime < lockTryTimeout) {
            //计算lock的expiretime
            long expireTimeWithLong = System.currentTimeMillis() + expireTime ;

            //这里如果使用setnx出现网络问题，无法设置过期时间
            String returnStr=jedis.set(lockKey,requestId,SET_IF_NOT_EXIST,SET_WITH_EXPIRE_TIME,expireTimeWithLong);

            if(LOCK_SUCCESS.equals(returnStr))
            {
                //获取锁成功
                return true;
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

    public boolean unlock(String requestId)
    {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));

        if(RELEASE_SUCCESS.equals(result))
        {
            return true;
        }

        return false;

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
