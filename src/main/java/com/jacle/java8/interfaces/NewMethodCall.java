package com.jacle.java8.interfaces;


import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * 通过::可以访问类的方法
 * 前提是方法在functionInterface中进行定义
 *
 * ::表示方法的引用，这个跟lambda表达式是联系在一起的；
 * 使用lambda表达式会创建匿名方法， 但有时候需要使用一个lambda表达式只调用一个已经存在的方法（不做其它）， 所以这才有了方法引用！
   以下是Java 8中方法引用的一些语法：
     静态方法引用（static method）语法：classname::methodname 例如：Person::getAge
     对象的实例方法引用语法：instancename::methodname 例如：System.out::println
     对象的超类方法引用语法： super::methodname
     类构造器引用语法： classname::new 例如：ArrayList::new
     数组构造器引用语法： typename[]::new 例如： String[]:new
 *
 */
public class NewMethodCall
{
    public static void main(String[] args)
    {
        List<String> list = Arrays.asList("aaaa", "bbbb", "cccc");

        //结合都实现了Iteratorable，这个是一个functionalinterface
        //方法的引用都是用在lambda表达式内部的
        list.forEach(x->System.out.println(x));
        list.forEach(new NewMethodCall()::print);
        Iterator<String> iter=list.iterator();
        iter.forEachRemaining(System.out::print);

        //同一个迭代器使用foreachremaining第二次不显示
        //第二次显示
        iter.forEachRemaining(x->System.out.print("remain:"+x+" "));
    }

    public void print(String content){
        System.out.println(content);
    }
}
