package com.jacle.java8.date;

import java.util.concurrent.CountDownLatch;

/**
 * 使用时间计算时间差，最好使用nanos
 * 并行调用，大批量线程调用，currentTimeMillis是串行调用的100倍之多
 *
 */
public class TimeMillis
{
    public static void main(String[] args)
    {
        //一种解决方法，依赖jdk8+
        System.out.println(System.nanoTime());

        long beginTimeMillis=System.currentTimeMillis();
        //串行调用
        for(int i=0;i<100;i++)
        {
            System.currentTimeMillis();
        }
        System.out.println("serial:"+(System.currentTimeMillis()-beginTimeMillis));

        //多线程并行调用
        CountDownLatch beginLatch=new CountDownLatch(1);
        CountDownLatch endLatch=new CountDownLatch(100);

        for(int i=0;i<100;i++)
        {
            new Thread(()->{
                try {
                    beginLatch.await();
                    System.currentTimeMillis();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    endLatch.countDown();
                }
            }).start();
        }

        long begintimeNanos=System.nanoTime();
        beginLatch.countDown();
        try {
            endLatch.await();
            System.out.println("parallel:"+(System.nanoTime()-begintimeNanos));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

}
