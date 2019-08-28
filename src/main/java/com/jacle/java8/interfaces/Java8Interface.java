package com.jacle.java8.interfaces;


/**
 * java8的接口中可以定义静态方法
 *
 */
public interface Java8Interface
{
    public void prt();
    public static void printLines(){
        System.out.println("interfaces static method...");
    };

}
