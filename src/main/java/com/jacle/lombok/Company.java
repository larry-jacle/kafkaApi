package com.jacle.lombok;


import com.sun.istack.internal.NotNull;
import lombok.*;

import java.io.*;

//equals默认是不适用静态、瞬态的变量来构成equal方法
@RequiredArgsConstructor()  //会生成常量和notnull的变量的构造方法
//@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(exclude = {"code"},callSuper=false)
@ToString(includeFieldNames = true,exclude = {"code","companyName"})
@Data
@Builder
public class Company  implements Serializable
{
    private String companyName;

    //控制生产的set方法
    @Setter(AccessLevel.PROTECTED)
    private String code;
    private final String bussinessCode="BSC123";
    @NotNull  private String code2;


    //非空的判断
    public String companyDesc(@NonNull String desc)
    {
        return desc;
    }

    public String cleanup(String filepath)
    {
        StringBuffer stringBuffer=new StringBuffer();
        try {
            @Cleanup  FileInputStream inputStream=new FileInputStream(new File(filepath));
            byte[] buff=new byte[512];

            int size=0;
            while((size=inputStream.read(buff))!=-1)
            {
                inputStream.read(buff,0,size);
                stringBuffer.append(new String(buff,0,size));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuffer.toString();
    }


}
