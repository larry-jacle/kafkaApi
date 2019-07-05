package com.jacle.lombok;

public class CompanyUnit
{
    public static void main(String[] args)
    {
        //使用lombook构建的对象的应用
        Company company=Company.builder().code("123").companyName("天子股份").code2("1234").build();
        System.out.println(company.toString());

    }
}
