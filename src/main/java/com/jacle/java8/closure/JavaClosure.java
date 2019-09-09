package com.jacle.java8.closure;

import java.util.ArrayList;
import java.util.List;

interface Action {
    void Run();
}


/**
 * java中的闭包主要有两种方式：接口和内部类,接口叫做回调接口，方法叫做回调方法
 * 闭包广泛使用在回调函数和函数式编程中
 */
public class JavaClosure {

    List<Action> list = new ArrayList<Action>();

    public void Input() {
        for (int i = 0; i < 10; i++) {

            //final表示闭包绑定本地的一个变量
            final int copy = i;

            //匿名内部类实现的闭包是在初始化时编译，此时绑定了变量
            list.add(new Action() {
                @Override
                public void Run() {
                    System.out.println(copy);
                }
            });
        }
    }

    public void Output() {
        for (Action a : list) {
            a.Run();
        }
    }

    public static void main(String[] args) {
        JavaClosure sc = new JavaClosure();
        sc.Input();
        sc.Output();

    }

}
