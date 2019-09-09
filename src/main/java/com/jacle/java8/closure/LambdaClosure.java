package com.jacle.java8.closure;

/**
 * lambda很好的解决了闭包的作用域访问问题，不需要final来限制
 *
 * 闭包简单说就是能够用变量来存储方法，从jdk1.8之后，都要通过lambda来处理，方便快捷
 *
 */
public class LambdaClosure
{
    public String var="class variable";

    public static void main(String[] args)
    {
        new LambdaClosure().test();
    }

    public void test()
    {
        String var="method variable";

        //lambda来代替接口，里面的回调方法没有方法名，只有实现体
        new Thread(()->{
            //直接调用外部的变量，不需要使用final
            System.out.println(var);
            System.out.println(this.var);
        }).start();
    }

}
