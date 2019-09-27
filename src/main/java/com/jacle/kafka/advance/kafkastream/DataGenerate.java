package com.jacle.kafka.advance.kafkastream;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class DataGenerate
{
    public static void main(String[] args)
    {
       generateValue();
    }

    public static void generateValue() {

        Properties props = new Properties();
        props.put("bootstrap.servers", "s203:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("request.required.acks", "0");

        new Thread(() -> {
            Producer<String, String> producer = new KafkaProducer<>(props);
            try {
                while (true) {
                    TimeUnit.SECONDS.sleep(1L);
                    Instant now = Instant.now();
                    String key = "service_1";
                    String value = key + "@" + toLocalTimeStr(now);
                    producer.send(new ProducerRecord<>("truplingwindow", key, value));

//                    String key2 = "service_2"; // 模拟另一个服务也在发送消息
//                    producer.send(new ProducerRecord<>("truplingwindow", key2, value));
                }
            } catch (Exception e) {
                e.printStackTrace();
                producer.close();
            }
        }).start();
    }
    private static String toLocalTimeStr(Instant i) {
        return i.atZone(ZoneId.systemDefault()).toLocalDateTime().toString();
    }
}
