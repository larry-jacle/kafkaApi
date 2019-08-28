package com.jacle.java8.lambda;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ConsumerExp
{
    public static void main(String[] args)
    {
        Consumer<Integer> consumer=x->System.out.println("consumer1:"+x);
        consumer.accept(2);

        Consumer<Integer> consumer2=x->System.out.println("consumer2:"+x);
        consumer2.accept(2);

        //andThen是分别调用两个方法，因为没有返回值，所以两个Function没有关联
        consumer.andThen(consumer2).accept(3);

        BiConsumer<Integer,String> biConsumer=(x,y)->System.out.println(x+"-->"+y);
        biConsumer.accept(3,"6");

    }
}
