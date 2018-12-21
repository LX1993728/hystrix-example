package com.liuxun.cache.hystrix.command;

import com.liuxun.cache.local.cache.LocationCache;
import com.netflix.hystrix.*;

public class GetCityNameCommand extends HystrixCommand<String> {
    private Long cityId;
    public GetCityNameCommand(Long cityId){
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GetCityNameGroup"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(15) // 并发量限制
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)));
        this.cityId = cityId;
    }

    @Override
    protected String run() throws Exception {
        return LocationCache.getCityName(cityId);
    }
}
