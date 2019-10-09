package com.jacle.kafka.advance.kafkastream;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;


/**
 * 模拟温度\湿度数据发送
 */
public class TempeartureDataGenerate
{
    private static Random random=new Random();

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
                    String key = "temp_humidty_1";
                    String value = "{\"temp\":"+random.nextInt(40)+",\"humidity\":"+random.nextInt(30)+"}";
                    producer.send(new ProducerRecord<>("temperature", key, value));

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
