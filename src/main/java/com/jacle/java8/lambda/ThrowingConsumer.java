package com.jacle.java8.lambda;


import java.util.function.Consumer;
import java.util.function.Function;

@FunctionalInterface
public interface ThrowingConsumer<T,E extends Throwable>
{
    //这个接口的方法定义，跟Funtion一样，所有lambda实现的方法体相当于Function的方法体;
    void apply(T t) throws E;

    //Function是另外的函数值接口
    static <T,E extends Throwable> Consumer<T> applyFunction(ThrowingConsumer<T, E> func)
    {
        //lambda中try catch，一定要return;
        //第一个return，return的是一个方法体，这点一定要注意，return lambda的时候，lamgda表达式不执行；
        return t->{
            try {
                //void不需要加return
                func.apply(t);
            } catch (Throwable e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        };
    }
}
