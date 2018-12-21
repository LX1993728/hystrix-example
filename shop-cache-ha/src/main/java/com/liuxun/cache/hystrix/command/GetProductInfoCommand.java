package com.liuxun.cache.hystrix.command;

import com.alibaba.fastjson.JSONObject;
import com.liuxun.cache.http.HttpClientUtils;
import com.liuxun.cache.model.ProductInfo;
import com.netflix.hystrix.*;

/**
 * @apiNote 获取商品信息 单条
 */

public class GetProductInfoCommand extends HystrixCommand<ProductInfo> {
    private Long productId;

    public GetProductInfoCommand(Long productId){
       // super(HystrixCommandGroupKey.Factory.asKey("GetProductInfoGroup"));
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ProductInfoService"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("GetProductInfoCommand"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("GetProductInfoPool"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.defaultSetter().withCoreSize(5))
        );

        this.productId = productId;
    }

    @Override
    protected ProductInfo run() throws Exception {
        String url = "http://127.0.0.1:8082/getProductInfo?productId=" + productId;
        String response = HttpClientUtils.sendGetRequest(url);
        final ProductInfo productInfo = JSONObject.parseObject(response, ProductInfo.class);
        return productInfo;
    }
}
