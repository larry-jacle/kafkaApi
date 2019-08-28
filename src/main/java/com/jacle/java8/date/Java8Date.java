package com.jacle.java8.date;


import java.time.*;

/**
 * 在java.time包中有许多的类，不过我们关心的主要有以下几个
     LocalDate，日期操作类
     LocalTime，时间操作类
     LocalDateTime，日期时间
     Duration，时间间隔
     Period，日期间隔
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

    }
}
