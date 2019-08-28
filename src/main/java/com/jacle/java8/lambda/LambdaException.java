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
 * 一般的处理方法,如下：
 */
public class LambdaException
{
    public static void main(String[] args) {
        List<String> urlsToCrawl = Arrays.asList("https://masterdevskills.com");
        LambdaException webCrawler = new LambdaException();
        webCrawler.crawl2(urlsToCrawl);
    }


    /**
     * lambda中有必须要catch的异常，不处理编译报错
     * @param urlsToCrawl
     */
  /*  public void crawl(List<String> urlsToCrawl) {
        urlsToCrawl.stream()
                .map(urlToCrawl -> new URL(urlToCrawl))
                .forEach(url -> save(url));
    }*/

    //try-catch代码块的情况，使用lambda并不简洁，主要还是通过lambda来调用类的方法（staitc和普通方法）
    public void crawl2(List<String> urlsToCrawl) {
        urlsToCrawl.stream()
                .map(this::convertUrl)
                .forEach(this::save);
    }


    //有异常抛出的火车
    public URL convertUrl(String urlToCrawl)
    {
        try {
            return new URL(urlToCrawl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void save(URL url)
    {
        try {
            String uuid = UUID.randomUUID().toString();
            InputStream inputStream = url.openConnection().getInputStream();
            Files.copy(inputStream, Paths.get(uuid + ".txt"), StandardCopyOption.REPLACE_EXISTING);
        }catch (IOException e)
        {
            //当try-catc无法进行异常处理的时候，要向上抛出RuntimeException
            throw new RuntimeException(e);
        }

    }
}
