package com.jacle.java8.lambda;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * collect方法跟collectors的joining、mapping方法
 */
public class JoiningAndMapping
{
    public static void main(String[] args)
    {
        //joing 将集合中的数据变为字符串，设置前缀、后缀、分隔符
        List<String> list= Arrays.asList("1","2","3");
        String result=list.stream().collect(Collectors.joining(","));
        System.out.println(result);

        result=list.stream().collect(Collectors.joining(",","{","}"));
        System.out.println(result);

        //Mapping 将集合中的元素进行处理，指定到List中
        List resultList=list.stream().collect(Collectors.mapping(x->"prefix-"+x, Collectors.toList()));
        System.out.println(resultList);

        //mapping其实跟map、collect效果一样
        resultList=list.stream().map(x->"prefix-"+x).collect( Collectors.toList());
        System.out.println(resultList);

    }
}
