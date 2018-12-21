package com.liuxun.cache.hystrix.command;

import com.alibaba.fastjson.JSONObject;
import com.liuxun.cache.http.HttpClientUtils;
import com.liuxun.cache.model.ProductInfo;
import com.netflix.hystrix.*;

/**
 * @apiNote 获取商品信息 单条
 */

public class GetProductInfo3Command extends HystrixCommand<ProductInfo> {
    private Long productId;

    public GetProductInfo3Command(Long productId){
       // super(HystrixCommandGroupKey.Factory.asKey("GetProductInfoGroup"));
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ProductInfoService")) // group name
                .andCommandKey(HystrixCommandKey.Factory.asKey("GetProductInfo2Command"))    // command name
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("GetProductInfo2Pool")) // threadpool key
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withCircuitBreakerRequestVolumeThreshold(30) // 至少30个请求 (默认20个)
                        .withCircuitBreakerErrorThresholdPercentage(40) // 错误比例 40% 开启短路(默认50%)
                        .withCircuitBreakerSleepWindowInMilliseconds(3000) // 3秒后尝试恢复(默认5秒)
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.defaultSetter()
                        .withCoreSize(20)    // 执行的线程数量
                        .withQueueSizeRejectionThreshold(10) // 请求等待队列的最大数量
                )
        );

        this.productId = productId;
    }

    @Override
    protected ProductInfo run() throws Exception {
        System.out.println("调用接口数据 productId="+productId);
        if (productId.equals(-1L)){
            throw new Exception();
        }
        String url = "http://127.0.0.1:8082/getProductInfo?productId=" + productId;
        String response = HttpClientUtils.sendGetRequest(url);
        final ProductInfo productInfo = JSONObject.parseObject(response, ProductInfo.class);
        return productInfo;
    }

    @Override
    protected ProductInfo getFallback() {
        final ProductInfo productInfo = new ProductInfo();
        productInfo.setName("降级商品");
        return productInfo;
    }
}
