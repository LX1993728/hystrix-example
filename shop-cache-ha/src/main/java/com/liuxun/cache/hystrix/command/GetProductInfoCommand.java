package com.liuxun.cache.hystrix.command;

import com.alibaba.fastjson.JSONObject;
import com.liuxun.cache.http.HttpClientUtils;
import com.liuxun.cache.model.ProductInfo;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

/**
 * @apiNote 获取商品信息 单条
 */

public class GetProductInfoCommand extends HystrixCommand<ProductInfo> {
    private Long productId;

    public GetProductInfoCommand(Long productId){
        super(HystrixCommandGroupKey.Factory.asKey("GetProductInfoGroup"));
        this.productId = productId;
    }

    @Override
    protected ProductInfo run() throws Exception {
        String url = "http://127.0.0.1:8082/getProductInfo?productId=" + productId;
        String response = HttpClientUtils.sendGetRequest(url);
//        System.out.println(response);
        return JSONObject.parseObject(response,ProductInfo.class);
    }
}
