package com.jacle.java8.interfaces;

public interface Java8Default
{
    //java可以定义默认方法，实现了的方法体
    //default只能用在接口中
    public default void prt()
    {
        System.out.println("java 8-default method");
    }

    //可以定义多个defaults
    public default void prt2()
    {
        System.out.println("java 8-default method");
    }
}
