package com.jacle.kafka.signalHandler;

public class TestThread {
    public static boolean shutdownFlag = false;
    public static void main(String[] args) {

        AppSignalHandler killHandler = new AppSignalHandler();
        killHandler.registerSignal(AppSignalHandler.TERM_15);

        //通过信号量的配置，来选择main方法的执行代码块
        int i = 0;
        while (true) {
            System.out.println(i);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
            if(shutdownFlag)break;
        }
        try {
            System.out.println("main Thread sleep 5s");
            Thread.sleep(1000*5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("main Thread exit");
    }
}
