package com.jacle.java8.lambda;

import com.jacle.lombok.Company;

import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Optional;

/**
 * lambda结合optional来处理null的情况，不适用optional来处理的时候，一样，没什么优势
 *
 */
public class LambdaNullDeal
{
    public static void main(String[] args)
    {
        Company c=null;
        c=new Company("company","","9");

        //Optional可以用来包装对象，处理null的情况，变得更加优雅，python中是有对应处理的
        Optional<Company> optionalCompany=Optional.ofNullable(c);
        System.out.println(optionalCompany);

        optionalCompany.ifPresent(System.out::println);
        optionalCompany.orElse(new Company("company2","","26"));

        //Optional通过map来代替if-else的编写，简化代码
        optionalCompany.map(c1->c1.getCompanyName()).map(name->name.toUpperCase()).orElse(null);


        //通过lambda来代替接口的实现，这个是最长用的
        //lambda代替匿名内部类，减少类的初始化和开销，但是要拆分为多个lambda表达式
        //一个类里面有多个方法，要拆分为多个lambda表达式
        //new 一个接口就是实现了一个匿名内部类
        ActionListener actionListener=event->System.out.println(new Date());

        //写线程的时候也可以大量使用lambda表达式
        new Thread(()->{System.out.println("thread runnable");}).start();
    }
}
