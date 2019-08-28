package com.jacle.java8.lambda;


/**
 * lambda表达式能够将一段方法赋值给一个变量，从而可以简化代码的编写
 * 对于一次性使用的一些接口方法，可以少定义类
 * 通过赋值给变量，从而可以将一个方法传入另外一个方法内部，实现闭包
 */
public class LambdaExp
{
    //一个labmda表达式就是一个接口的实现
    //匿名内部类也是一个接口的具体实现
    public static void main(String[] args)
    {
        //lambda表达式显示的是方法的大括号内部的代码内容
        LambdaInterface show=(s)->{System.out.println(s);System.out.println("code block 2");};
        show.show("123");
    }
}
