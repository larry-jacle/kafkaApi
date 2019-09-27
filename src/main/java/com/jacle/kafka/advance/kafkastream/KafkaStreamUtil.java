package com.jacle.kafka.advance.kafkastream;

import org.apache.kafka.streams.kstream.Windowed;

import java.time.Instant;
import java.time.ZoneId;

public class KafkaStreamUtil
{
    public static boolean isOld(Windowed<String>  windowedKey, Long value, Instant instant)
    {
       long endTime=windowedKey.window().end();
//       System.out.println("endTime:"+Instant.ofEpochMilli(endTime));
//        System.out.println("now:"+instant);

        return Instant.ofEpochMilli(endTime).isBefore(instant);
    }

    /**
     * instant转换为日期字符串
     * @param i
     * @return
     */
    public static String toLocalTimeStr(Instant i) {
        return i.atZone(ZoneId.systemDefault()).toLocalDateTime().toString();
    }


    //获取自定义窗口的输出信息
    public static Windowed<String>  getWindowedInfo(Windowed<String> key,Long value)
    {
        return new Windowed<String>(key.key(),key.window()){
            @Override
            public String toString() {
                String startTimeStr = toLocalTimeStr(Instant.ofEpochMilli(window().start()));
                String endTimeStr = toLocalTimeStr(Instant.ofEpochMilli(window().end()));

                return "[" + key() + "@" + startTimeStr + "/" + endTimeStr + ",value:"+value+"]";
            }
        };
    }

    public static void showWindowedInfo(Windowed<String> key,Long value)
    {
        Windowed<String> windowedKey=getWindowedInfo(key,value);
        System.out.println(windowedKey);
    }

    public static  void dealWithTimeWindowAggrValue(Windowed<String> key, Long value) {
        Windowed<String> windowed = getReadableWindowed(key);
        System.out.println("处理聚合结果：key=" + windowed + ",value=" + value);
    }

    public static Windowed<String> getReadableWindowed(Windowed<String> key) {
        return new Windowed<String>(key.key(), key.window()) {
            @Override
            public String toString() {
                String startTimeStr = toLocalTimeStr(Instant.ofEpochMilli(this.window().start()));
                String endTimeStr = toLocalTimeStr(Instant.ofEpochMilli(this.window().end()));
                return "[" + key() + "@" + startTimeStr + "/" + endTimeStr + "]";
            }
        };
    }

}
