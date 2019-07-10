package com.jacle.kafka.advance;

import org.apache.kafka.common.utils.Utils;
import org.junit.Test;

public class PartitionTester
{
    @Test
    public void testUtils()
    {
        //变为正数，通过给跟Integer_max进行与操作
        System.out.println(Utils.toPositive(-1232));
        System.out.println(Utils.toPositive(1232));
        System.out.println(Utils.toPositive(0));

        //负数进行与的时候，会变为正数,正数和0进行与，数值不变
        System.out.println(-1232&2147483647);
        System.out.println(1232&2147483647);
        System.out.println(0&2147483647);
    }
}
