package com.jacle.java8.date;

import java.util.concurrent.TimeUnit;

public class TimeUnitExp
{
    public static void main(String[] args)
    {
        try {
            System.out.println("before");

            //java8可以通过TimeUnit来设置延迟
            TimeUnit.SECONDS.sleep(9);
            System.out.println("end");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
