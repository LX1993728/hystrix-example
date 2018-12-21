package com.liuxun.cache.hystrix.command;

import com.liuxun.cache.local.cache.BrandCache;
import com.netflix.hystrix.*;

/**
 * @apiNote 获取品牌名称的command
 * @author liuxun
 */
public class GetBrandNameCommand extends HystrixCommand<String> {
    private Long brandId;

    public GetBrandNameCommand(Long brandId){
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("BrandInfoService"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("GetBrandNameCommand"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("GetBrandInfoPool"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.defaultSetter().withCoreSize(5))
                // 通过信号量控制 执行fallback的最大线程数不能超过15
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withFallbackIsolationSemaphoreMaxConcurrentRequests(15))
        );
        this.brandId = brandId;
    }

    @Override
    protected String run() throws Exception {
        // 正常逻辑是 这里会调用一个品牌服务的接口
        // 如果调用失败报错，那么就会调用fall-back降级机制
        throw new Exception("调用品牌服务失败！！！");
    }

    @Override
    protected String getFallback() {
        // 从本地缓存获取一份过期的品牌数据，返回
        System.out.println("从本地缓存获取过期的品牌数据，brandId="+brandId);
        return BrandCache.getBrandName(brandId);
    }
}
