package com.jacle.redis;


import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * 通过redssion来实现分布式锁
 */
public class RedisRedssionManager {
    private static final String RAtomName = "gid_";
    private static Config config = new Config();
//    private static Redisson redisson = null;
    private static RedissonClient redisson=null;

    public static void main(String[] args)
    {
       System.out.println(getRedisson());
    }

    public static void init() {
        config.useSingleServer().setAddress("redis://10.1.12.206:6388")
                .setConnectionPoolSize(200)
                .setIdleConnectionTimeout(10000)
                .setTimeout(3000).setPingTimeout(30000).setRetryAttempts(3);
        redisson=Redisson.create(config);

        RAtomicLong atomlong=redisson.getAtomicLong(RAtomName);
        atomlong.set(1);
    }


    public static RedissonClient getRedisson()
    {
        init();
        return redisson;
    }


    /**
     * 获取原子Long对象
     * @return
     */
    public static Long nextId()
    {
        RAtomicLong atomlong=redisson.getAtomicLong(RAtomName);
        atomlong.incrementAndGet();
        return atomlong.get();
    }


}
