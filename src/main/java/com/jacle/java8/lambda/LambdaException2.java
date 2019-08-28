package com.jacle.java8.lambda;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/**
 * 通过包装器来简化lambda表达式的异常处理
 * 此时方法的异常只进行抛出，不进行try-catch
 */
public class LambdaException2 {
    public static void main(String[] args) {
        List<String> urlsToCrawl = Arrays.asList("https://masterdevskills.com");
        LambdaException2 webCrawler = new LambdaException2();
        webCrawler.crawl2(urlsToCrawl);
    }


    /**
     * lambda中有必须要catch的异常，不处理编译报错
     *
     * @param urlsToCrawl
     */
     public void crawl(List<String> urlsToCrawl) {

         //通过lambda封装器来处理异常
        urlsToCrawl.stream()
                .map(ThrowingFunction.applyFunction(urlToCrawl -> new URL(urlToCrawl)))
                .forEach(this::save);

         //第二次进一步改写
         urlsToCrawl.stream()
                 .map(ThrowingFunction.applyFunction(urlToCrawl -> new URL(urlToCrawl)))
                 .forEach(ThrowingConsumer.applyFunction(url->save(url)));

         //我们还可以通过引用的方式简化代码
         urlsToCrawl.stream()
                 .map(ThrowingFunction.applyFunction(URL::new))
                 .forEach(ThrowingConsumer.applyFunction(this::save));
    }

    //try-catch代码块的情况，使用lambda并不简洁，主要还是通过lambda来调用类的方法（staitc和普通方法）
    //lambda为了调用简介，通过给lambda编写lambda包装器
    public void crawl2(List<String> urlsToCrawl)
    {
        //lambda中要使用异常处理封装类


    }


    //有异常抛出的火车
    public URL convertUrl(String urlToCrawl) throws MalformedURLException {
        return new URL(urlToCrawl);
    }

    private void save(URL url) {

        try {
            String uuid = UUID.randomUUID().toString();
            InputStream inputStream = null;
            inputStream = url.openConnection().getInputStream();
            Files.copy(inputStream, Paths.get(uuid + ".txt"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            throw  new RuntimeException(e);
        }


    }
}
