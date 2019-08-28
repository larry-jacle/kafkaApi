package com.jacle.java8.lambda;


import java.util.function.Function;

@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Throwable> {
    //这个接口的方法定义，跟Funtion一样，所有lambda实现的方法体相当于Function的方法体;
    R apply(T t) throws E;

    //Function是另外的函数值接口
    static <T, R, E extends Throwable> Function<T, R> applyFunction(ThrowingFunction<T, R, E> func) {
        //lambda中try catch，一定要return;
        //第一个return，return的是一个方法体，这点一定要注意，return lambda的时候，lamgda表达式不执行；
        return t -> {
            try {
                return func.apply(t);
            } catch (Throwable e) {
                //这里的e就是E的实例化对象,只是这个泛型是不需要输入的类型
                //如果要将异常进行转换，知道具体的异常的子类，此时可以在输入参数引入E
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        };
    }

    //Function是另外的函数值接口
    static <T, R, E extends Throwable> Function<T, R> applyExceptionFunction(ThrowingFunction<T, R, E> func, Class<E> clazz) {
        //lambda中try catch，一定要return;
        //第一个return，return的是一个方法体，这点一定要注意，return lambda的时候，lamgda表达式不执行；
        return t -> {
            try {
                return func.apply(t);
            } catch (Throwable e) {
                //这里的e就是E的实例化对象,只是这个泛型是不需要输入的类型
                //如果要将异常进行转换，知道具体的异常的子类，此时可以在输入参数引入E
                E ex = clazz.cast(e);
                System.out.println(ex.getMessage());
                throw new RuntimeException(ex);
            }
        };
    }
}
