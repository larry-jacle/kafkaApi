package com.jacle.java8.interfaces;

//extends的类跟接口有相同的方法，此时以类的方法为准
public class Java8DefaultExtendClass extends Java8DefaultClass implements Java8Default
{
   public static void main(String[] args)
   {
      new Java8DefaultExtendClass().prt();
   }
}
