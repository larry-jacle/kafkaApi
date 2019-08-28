package com.jacle.java8.interfaces;

public interface Java8DefaultB
{
    //java可以定义默认方法，实现了的方法体
    public default void prt1()
    {
        System.out.println("java 8-default method");
    }

    //可以定义多个defaults
    public default void prt2()
    {
        System.out.println("java 8-default method");
    }
}
