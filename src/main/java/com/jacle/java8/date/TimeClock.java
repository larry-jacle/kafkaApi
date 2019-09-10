package com.jacle.java8.date;


import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class TimeClock {
    private long now;

    private static TimeClock timeClock=new TimeClock();

    public TimeClock() {
        this.now = System.currentTimeMillis();

        new ScheduledThreadPoolExecutor(1, runnable -> {
            Thread t = new Thread(runnable,"time");
            t.setDaemon(true);
            return t;
        }).scheduleAtFixedRate(() -> {
        }, 1, 1, TimeUnit.MILLISECONDS);
    }

    public long now()
    {
        return now;
    }

    public static TimeClock getTimeClock()
    {
        return timeClock;

    }

    //函数式接口作为lambda的返回值，或者使用predicate，function等函数式接口作为返回值
    //lambda省略的依据
    /**
     * 1. 小括号内参数的类型可以省略；
       2. 如果小括号内有且仅有一个参，则小括号可以省略；
       3. 如果大括号内有且仅有一个语句，则无论是否有返回值，都可以省略大括号、return关键字及语句分号。

    ThreadFactory lambdaRunnable= (runnable)->{
        Thread t = new Thread("time");
        t.setDaemon(true);
        return t;
    };
     */

    public static void main(String[] args)
    {
        long currTimestamp=TimeClock.getTimeClock().now();
        System.out.println(currTimestamp);
    }

}
