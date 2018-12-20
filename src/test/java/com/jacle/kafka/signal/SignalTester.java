package com.jacle.kafka.signal;

import org.junit.Test;

public class SignalTester
{
    @Test
    public void testThreadShutDownHandler()
    {
        // 定义线程1
        final Thread thread1 = new Thread() {
            public void run() {
                System.out.println("thread1...");
            }
        };

        // 定义线程2
        Thread thread2 = new Thread() {
            public void run() {
                while(true)
                {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("thread2...");
                }

            }
        };

        // 定义关闭线程
        Thread shutdownThread = new Thread() {
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("shutdownThread...");
            }
        };

        //增加关闭jvm的钩子程序
        //这个函数是在jvm关闭的时候调用，也就是main方法执行完的时候
        Runtime.getRuntime().addShutdownHook(new Thread(shutdownThread));

        thread1.start();
        thread2.start();

    }
}
