package com.jacle.protostuff;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * protostuff避免了使用protocol buffer需要编写.proto文件的问题
 *
 */
public class ProtostuffUtil
{
    //避免每次序列化都重新申请空间
   private static LinkedBuffer linkedBuffer=LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

   private static Map<Class<?>,Schema<?>>  schemaCache=new ConcurrentHashMap<>();


   public static <T> byte[]  serialize(T obj)
   {
       Class<T> clazz=(Class<T>) obj.getClass();
       Schema<T>  schema=getSchema(clazz);
       byte[] data=null;

       try {
           data=ProtostuffIOUtil.toByteArray(obj,schema,linkedBuffer);
       }finally {
           linkedBuffer.clear();
       }


       return data;
   }


   public static <T> T deserialize(Class<T> clazz,byte[] data)
   {
       Schema<T> schema=getSchema(clazz);
       //根据schema生成结果对象
       T t=schema.newMessage();
       ProtostuffIOUtil.mergeFrom(data,t,schema);

       return t;
   }



   private static<T> Schema<T>  getSchema(Class<T> clazz)
   {
       Schema<T> schema=(Schema<T>) schemaCache.get(clazz);

       if(Objects.isNull(schema))//判断是否是null的工具类
       {
           //通过runtime来获取schema
           schema= RuntimeSchema.getSchema(clazz);
           if(Objects.nonNull(schema))
           {
               schemaCache.put(clazz,schema);
           }
       }

       return schema;
   }


}
