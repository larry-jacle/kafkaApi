package com.jacle.kafka.advance;

import com.google.gson.Gson;
import com.jacle.lombok.Company;
import com.jacle.protostuff.ProtostuffUtil;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;


/**
 * Kafka中自定义序列化器
 */
public class ProtobuffSerializer implements Serializer<Company>
{
    private String encoding="UTF-8";

    @Override
    public void configure(Map<String, ?> map, boolean b) {
        //对配置进行一些预设置

    }

    @Override
    public byte[] serialize(String s, Company company) {

        return ProtostuffUtil.serialize(company);
    }


    //一般保留为空
    @Override
    public void close() {

    }
}
