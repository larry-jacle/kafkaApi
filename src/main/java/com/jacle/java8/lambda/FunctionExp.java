package com.jacle.java8.lambda;

import java.util.function.BiFunction;
import java.util.function.Function;

public class FunctionExp
{
    public static void main(String[] args)
    {
        Function<Integer,Integer>  func1=x->x+3;
        System.out.println(func1.apply(20));

        //compse两个Function之间的联合输出
        Function<Integer,Integer>  func2=x->x*2;
        Integer s=func1.compose(func2).apply(2);
        System.out.println(s);

        //andThen跟compse的运算相反
        System.out.println(func2.andThen(func1).apply(2));

        //两个参数一个返回值
        BiFunction<Integer,Integer,Integer>  biFunction=(x,y)->x+y;
        System.out.println(biFunction.apply(2,3));

        Function<Integer,Integer> biFunction2=x->x*10;
        //andThen的参数是Function
        Integer result=biFunction.andThen(biFunction2).apply(2,3);
        System.out.println(result);
    }
}
