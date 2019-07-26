package com.jacle.kafka.advance;

import org.apache.kafka.common.utils.Utils;

public class Cal_customerOffsets
{
    public static void main(String[] args)
    {
         new Cal_customerOffsets().calPartitionNo("consumerGroup1");
    }


    public void calPartitionNo(String groupId)
    {
        System.out.println(Utils.abs(groupId.hashCode())%50);
    }

}
