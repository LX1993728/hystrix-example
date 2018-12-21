package com.liuxun.cache.hystrix.command;

import com.alibaba.fastjson.JSONObject;
import com.liuxun.cache.http.HttpClientUtils;
import com.liuxun.cache.model.ProductInfo;
import com.netflix.hystrix.*;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategyDefault;

/**
 * @apiNote 获取商品信息 单条
 */

public class GetProductInfoCommand extends HystrixCommand<ProductInfo> {
    private Long productId;

    private static final HystrixCommandKey KEY = HystrixCommandKey.Factory.asKey("GetProductInfoCommand");

    public GetProductInfoCommand(Long productId){
        // super(HystrixCommandGroupKey.Factory.asKey("GetProductInfoGroup"));
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ProductInfoService"))
                .andCommandKey(KEY)
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
        System.out.println("调用接口，查询商品数据, productId="+productId);
        return productInfo;
    }

    // request cache 根据标识，去命中缓存
    @Override
    protected String getCacheKey() {
        return "product_info_"+productId;
    }

    // 封装手动清理缓存的方法
    public static void flushCache(Long productId) {
        HystrixRequestCache.getInstance(KEY,
                HystrixConcurrencyStrategyDefault.getInstance()).clear("product_info_"+productId);
    }


}
