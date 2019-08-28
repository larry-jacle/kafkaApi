package com.jacle.java8.lambda;

import com.jacle.lombok.Company;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 通过lambda的特性来简化程序编写
 * lambda有两种引用方法的方式，对象方法引用和方法类静态引用
 */
public class LambdaUnit
{
    //通过函数接口包,可以不需要定义接口类
    //predict返回boolean，Comsumer返回void,Consumer接收一个参数
    public void showCompany(List<Company> companyFactory, Predicate<Company> predicate, Consumer<Company> consumer)
    {
        companyFactory.forEach(c->{if(predicate.test(c)){consumer.accept(c);}});
    }

    public static void main(String[] args)
    {
        //通过函数接口包来实现简化
         List<Company>  list=new ArrayList<Company>();
         list.add(new Company("companyName","","123"));
         new LambdaUnit().showCompany(list,c->true,c->System.out.println(c.getCompanyName()));

         //使用java的高阶函数来处理,DSL处理方式
        //只有一个参数的时候使用System.out::println
        list.stream().filter(c->true).forEach(System.out::println);
        //通过方法引用来简化输出,一般使用静态method referenced的比较多
        list.stream().filter(c->true).forEach(LambdaUnit::showItem);
        list.stream().filter(c->true).forEach(new LambdaUnit()::showItemMsg);

        //多个参数的时候，要指定显示的元素
        InterfaceC interfaceC=(a,b,c)->{System.out.println(a+","+b+","+c);};
        interfaceC.test("start",1,2);
        //直接调用lambda的方法，先要进行转换；
        ((InterfaceC)(a,b,c)->{System.out.println(a+","+b+","+c);}).test("end",1,2);

    }


    public static void showItem(Company cc)
    {
        System.out.println(cc.getCompanyName()+"..."+cc.getCode2());
    }

    public void showItemMsg(Company cc)
    {
        System.out.println(cc.getCompanyName()+"..."+cc.getCode2());
    }

}
