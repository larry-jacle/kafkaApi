package com.jacle.java8.lambda;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * 函数式接口Predicate，返回一个boolean
 */
public class PredicateExp
{
    public static void main(String[] args)
    {

        //predicate单个参数输入，返回boolean，可以进行多个逻辑条件的判断；
        //多个参数输入，可以拆解多个单predicate
        Predicate<Integer>  p1=(x)->x>5;
        Predicate<Integer>  p2=(x)->x<10;

        System.out.println(p1.test(10));
        System.out.println(p2.test(9));

        System.out.println(p1.and(p2).test(11));
        System.out.println(p1.or(p2).test(11));
        System.out.println(p1.negate().test(11));
        System.out.println(p2.negate().test(11));

        //传递两个参数的predicate,返回的是boolean
        BiPredicate<Integer,Integer>  biPredicate=(x,y)->x+y>10;
        System.out.println("biPredicate:"+biPredicate.test(20,10));


    }
}
