package com.jacle.java8.interfaces;


/**
 * 方法的引用
 *
 */
public class MethodRefered
{
    public String name="jacle";

    MethodRefered()
    {

    }

    MethodRefered(String name)
    {
        this.name=name;
    }

    public static void main(String[] args)
    {
        //所有的方法引用都是基于接口，实现接口内部的方法
        MR mr=MethodRefered::new;
        System.out.println(mr.create().name);


        //接口直接使用引用，调用构造方法
        MR2 mr2=MethodRefered::new;
        System.out.println(mr2.create("tony").name);
    }


    //加不加@FunctionalInterface对于接口是不是函数式接口没有影响，该注解知识提醒编译器去检查该接口是否仅包含一个抽象方法
    interface MR
    {
       MethodRefered create();
    }

    interface  MR2
    {
        MethodRefered create(String name);
    }


}
