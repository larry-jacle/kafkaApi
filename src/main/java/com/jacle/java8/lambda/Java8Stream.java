package com.jacle.java8.lambda;


import java.util.*;
import java.util.stream.*;

/**
 * java8引进了Stream，改进了对集合的操作
 *
 */
public class Java8Stream
{
    public static void main(String[] args)
    {
        //array变为Stream
        String[] arrays = {"a1", "a2", "a3"};
        Stream<String>  arrStream=Arrays.stream(arrays);


        //集合变为Steram，调用Collection接口的stream()方法
        List<String> list=new ArrayList<>();
        Stream<String> stream=list.stream();

        //通过Stream的子类来获取Stream
        IntStream intStream = IntStream.of(1, 2, 3);
        OptionalDouble average = intStream.average();
        System.out.println(average);//OptionalDouble[2.0]

        LongStream longStream = LongStream.of(1000, 2000, 3000);
        OptionalLong first = longStream.findFirst();
        System.out.println(first);//OptionalLong[1000]

        DoubleStream doubleStream = DoubleStream.of(1.0, 2.0, 3.0);
        OptionalDouble max = doubleStream.max();
        System.out.println(max);//OptionalDouble[3.0]

        Stream<String> streamString = Stream.of("a", "b", "c", "d");
        long count = streamString.count();
        System.out.println(count);//4

        List<String> list2 = Stream.of("Java", "Android", "Php", "IOS")//获得Stream对象
                .filter(s -> s.length() > 3)//过滤，只保留长度大于3的
                .sorted()//排序
                .collect(Collectors.toList());//将结果输出到List中
        System.out.println(list2);//[Android, Java]

    }
}
