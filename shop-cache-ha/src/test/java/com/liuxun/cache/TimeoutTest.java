package com.liuxun.cache;

import com.liuxun.cache.http.HttpClientUtils;

/**
 * 测试超时
 */
public class TimeoutTest {
    public static void main(String[] args) throws Exception{
        String response =  HttpClientUtils.sendGetRequest("http://127.0.0.1:8081/getProductInfo5?productId=-3");

    }
}
