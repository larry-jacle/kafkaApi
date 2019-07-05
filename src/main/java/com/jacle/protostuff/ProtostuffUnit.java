package com.jacle.protostuff;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.jacle.lombok.Company;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Arrays;

public class ProtostuffUnit
{
    public static void main(String[] args)
    {
        Company company= Company.builder().companyName("国际有限公司").code("123").code2("1232").build();

        byte[] data=ProtostuffUtil.serialize(company);
        Company company_des=ProtostuffUtil.deserialize(Company.class,data);

        //数组的内容输出，使用Arrays工具类
        System.out.println(Arrays.toString(data));
        System.out.println(data.length);
        System.out.println(company_des.toString());
        System.out.println(company_des.getCompanyName());

        //字节和字符串的转换，对象和字节的转换
        ByteArrayOutputStream reader=new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(reader);
            objectOutputStream.writeObject(company);

            //java原生的序列化大小是protobuff的四倍的关系
            byte[] javaBytes=reader.toByteArray();
            System.out.println(javaBytes.length);

            objectOutputStream.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
