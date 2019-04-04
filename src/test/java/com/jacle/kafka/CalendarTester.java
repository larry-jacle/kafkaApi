package com.jacle.kafka;

import org.junit.Test;
import java.util.Calendar;

public class CalendarTester
{
    @Test
    public void testCalendar()
    {
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.SECOND,3);

        //得到的是瞬时事件
        System.out.println(calendar.getTimeInMillis()/1000);
    }
}
