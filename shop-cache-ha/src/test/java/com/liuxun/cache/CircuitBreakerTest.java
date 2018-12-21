package com.liuxun.cache;

import com.liuxun.cache.http.HttpClientUtils;

public class CircuitBreakerTest {
    public static void main(String[] args) throws Exception{
        for (int i = 0; i < 15; i++) {
           String response =  HttpClientUtils.sendGetRequest("http://127.0.0.1:8081/getProductInfo3?productId=1");
           System.out.println("第"+(i+1)+"次请求,结果为: "+response);
        }
        for (int i = 0; i < 25; i++) {
            String response =  HttpClientUtils.sendGetRequest("http://127.0.0.1:8081/getProductInfo3?productId=-1");
            System.out.println("第"+(i+1)+"次请求,结果为: "+response);
        }
        Thread.sleep(5000L);
        // 等待了5秒后，时间窗口统计，发现异常比例太多就短路了
        for (int i = 0; i < 10; i++) {
            String response =  HttpClientUtils.sendGetRequest("http://127.0.0.1:8081/getProductInfo3?productId=-1");
            System.out.println("第"+(i+1)+"次请求,结果为: "+response);
        }
        // 统计单位，有一个时间窗口，我们必须等到那个时间窗口过了以后，hystrix才会看一下最近时间段
        // 比如说 最近的10秒内有多少条数据，其中异常的数据有没有一定的比例
        // 如果到了一定的比例才会去短路
        System.out.println("尝试等待5秒钟");
        Thread.sleep(5000L);
        for (int i = 0; i < 10; i++) {
            String response =  HttpClientUtils.sendGetRequest("http://127.0.0.1:8081/getProductInfo3?productId=1");
            System.out.println("第"+(i+1)+"次请求,结果为: "+response);
        }

    }
}
