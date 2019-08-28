package com.jacle.java8.lambda;


/**
 * lambda表达式使用会定义一个函数式接口，这个就是赋值给变量的变量类型；
 * 通过添加注解防止认为修改接口，引起其他连锁变化
 *
 */
@FunctionalInterface
public interface LambdaInterface
{
    public void show(String s);
}
