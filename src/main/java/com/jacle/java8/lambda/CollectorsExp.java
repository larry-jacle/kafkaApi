package com.jacle.java8.lambda;

import com.jacle.lombok.Company;

import java.util.*;
import java.util.stream.Collectors;

/**
 * lambda的collect的统计
 *
 */
public class CollectorsExp
{
    public static void main(String[] args)
    {
        //maxBy,minBy
        List<Integer>  list= Arrays.asList(1,2,3,5,7,11,1,232);

        //jdk1.8的list提供了sort方法，可以直接进行排序，不需要使用collections
        //comparator接口比较器也可以使用lambda来处理
        //list.sort((x,y)->x.compareTo(y));
        //comparing是一个Function，一个输入，一个输出s

        //list.stream().collect(Collectors.maxBy(Comparator.comparing(x->x)));
        //Comparator.comparing会自动提取跟数值一致的构造器，也可以指定构造器
        Optional<Integer> maxInt=list.stream().collect(Collectors.maxBy(Comparator.comparing(Integer::intValue)));
        System.out.println(list);
        System.out.println(maxInt.isPresent()?maxInt.get():"-none-");

        //可以通过comparator chain来进行复杂的比较
        List<Company>  companyList=new ArrayList<>();
        Company c1=new Company("name1","1","2");
        Company c2=new Company("name2","2","3");
        companyList.add(c1);
        companyList.add(c2);

        //注意返回的是Optional，反解析的时候要注意
        Optional<Company> company=companyList.stream().collect(Collectors.maxBy(Comparator.comparing(x->x.getCompanyName())));
        System.out.println(company.get().getCompanyName());

        //求平均数
        double avgInt=list.stream().collect(Collectors.averagingInt(Integer::intValue));
        double avgInt2=list.stream().collect(Collectors.averagingInt(x->x));

        System.out.println(avgInt);
        System.out.println(avgInt2);

        Long counting=list.stream().collect(Collectors.counting());
        System.out.println(counting);

        //partitionBy和groupingBy
        Map<Boolean,List<Integer>>  partitionMap=list.stream().collect(Collectors.partitioningBy(x->x>32));
        System.out.println(partitionMap);
        //map的forEach接收的是BiConsumer
        partitionMap.forEach((x,y)->System.out.println(x+":"+y));

        //grouping分组,类似sql中的groupby
        Map<Integer,List<Integer>>  listMap=list.stream().collect(Collectors.groupingBy(x->x));
        listMap.forEach((k,v)->System.out.println(k+":"+v));

        //sorted排序
        List<Integer> sortedList=list.stream().sorted().collect(Collectors.toList());
        System.out.println(sortedList);

    }
}
