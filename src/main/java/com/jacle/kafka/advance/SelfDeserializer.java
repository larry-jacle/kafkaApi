package com.jacle.kafka.advance;

import com.google.gson.Gson;
import com.jacle.lombok.Company;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.UnsupportedEncodingException;
import java.util.Map;


/**
 * 反序列化
 */
public class SelfDeserializer implements Deserializer<Company>
{
    private String encoding="UTF-8";

    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public Company deserialize(String topic, byte[] bytes) {
        try {
            return new Gson().fromJson(new String(bytes,encoding),Company.class);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void close() {

    }
}
