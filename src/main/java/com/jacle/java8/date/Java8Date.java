package com.jacle.java8.date;


import java.time.*;

/**
 * 在java.time包中有许多的类，不过我们关心的主要有以下几个
     LocalDate，日期操作类
     LocalTime，时间操作类
     LocalDateTime，日期时间
     Duration，时间间隔
     Period，日期间隔

 jdk8:日期时间的处理api、lambda表达式、接口可以使用默认方法

 */
public class Java8Date
{
    public static void main(String[] args)
    {
        //日期处理类
        LocalDate localDate=LocalDate.of(2019,10,19);
        System.out.println(localDate);
        System.out.println(localDate.getYear()+"/"+localDate.getDayOfMonth()+"/"+localDate.getDayOfWeek());
        System.out.println(localDate.lengthOfMonth());
        System.out.println(localDate.isLeapYear());
        System.out.println(LocalDate.now());
        System.out.println(LocalDate.parse("2019-09-19"));

        //LocalTime时间处理类
        LocalTime localTime=LocalTime.now();
        System.out.println(localTime);
        System.out.println(localTime.getHour()+"/"+ localTime.getMinute()+"/"+localTime.getSecond()+"/"+localTime.getNano());


        LocalDateTime dt1 = LocalDateTime.of(LocalDate.now(), LocalTime.now());
        LocalDateTime dt2 = LocalDateTime.of(2018, Month.JULY, 24, 9, 32, 20);

        //日期和时间对象的互补
        LocalDateTime dt3 = LocalDate.now().atTime(13, 45, 20);
        LocalDateTime dt4 = LocalDate.now().atTime(LocalTime.now());
        LocalDateTime dt5 = LocalTime.now().atDate(LocalDate.now());
        LocalDate date = dt1.toLocalDate();
        LocalTime time = dt2.toLocalTime();

        System.out.println(dt1);
        System.out.println(dt2);
        System.out.println(dt3);

        //筛选需要字段
        System.out.println(date);
        System.out.println(time);

        //时间间隔对象
        Duration duration=Duration.ofMillis(3);
        System.out.println(Instant.now());

        //日期的间隔对象
        Period period=Period.ofDays(3);
        LocalDate startDate = LocalDate.of(2015, 2, 20);
        LocalDate endDate = LocalDate.of(2017, 1, 15);

        //计算两个日期的差值
        Period p1=Period.between(startDate,endDate);
        System.out.println("两个日期的差值:"+p1.getDays());
        //判断结束日期是否大于开始日期
        System.out.println(p1.isNegative());
        //可以通过解析文本来构建Period,其格式为“PnYnMnD”
        Period period1=Period.parse("P2Y3M5D");
        System.out.println(period1.getYears());
        System.out.println(period1.getDays());

        //Duration小时秒或者纳秒的时间间隔，适合高精度的时间处理
        Instant start = Instant.parse("2017-10-03T10:15:30.00Z");
        Instant end = Instant.parse("2017-10-03T10:16:30.00Z");

        Duration duration1 = Duration.between(start, end);
        System.out.println(duration1.isNegative());

        //我们可以通过Period或者Duration将日期或者时间，获取指定单位的数值
        //方法不能够将所有数值转换为同一个单位
        Duration fromChar1 = Duration.parse("P1DT1H10M10.5S");
        Duration fromChar2 = Duration.parse("PT10M");
        System.out.println(fromChar2.toHours());

    }
}
