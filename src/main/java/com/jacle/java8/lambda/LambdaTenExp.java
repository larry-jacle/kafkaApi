package com.jacle.java8.lambda;

import javax.swing.*;
import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LambdaTenExp
{
    public static void main(String[] args)
    {
        //1、替代线程的runnable接口的实现
        new Thread(()->{System.out.println("lambda thread");}).start();

        //2、替代事件接口的实现
        new JButton().addActionListener(event->{System.out.println("event");});

        //3、遍历
        List features = Arrays.asList("Lambdas", "Default Method", "Stream API",
                "Date and Time API");
        features.forEach(p->System.out.println(p));
        features.forEach(System.out::println);

        //4、函数编程,函数作为参数传入方法
        new LambdaTenExp().methodFx("jacle",s->false);

        //5、map reduce
        List<Integer> costBeforeTax = Arrays.asList(100, 200, 300, 400, 500,500,500);
        Integer totalSum=costBeforeTax.stream().map(x->x*10).reduce((sum,x)->sum=sum+x).get();
        System.out.println(totalSum);

        //6、filter
        List<Integer>  filtered=costBeforeTax.stream().filter(x->x>300).collect(Collectors.toList());
        System.out.println(filtered);
        String filteredJoin=costBeforeTax.stream().filter(x->x>200).map(x->x+"").collect(Collectors.joining(","));
        System.out.println(filteredJoin);
        String filteredJoinDistinct=costBeforeTax.stream().filter(x->x>200).distinct().map(x->x+"").collect(Collectors.joining(","));
        System.out.println(filteredJoinDistinct);

        //7、statistics,注意选择合适的map
        IntSummaryStatistics integerStream=costBeforeTax.stream().filter(x->x>300).mapToInt(x->x).summaryStatistics();
        System.out.println(integerStream);


    }

    public boolean methodFx(String name,Predicate<String> predicate)
    {
        return predicate.test(name);
    }
}
