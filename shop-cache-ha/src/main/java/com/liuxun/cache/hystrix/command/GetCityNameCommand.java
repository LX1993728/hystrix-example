package com.liuxun.cache.hystrix.command;

import com.liuxun.cache.local.cache.LocationCache;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

public class GetCityNameCommand extends HystrixCommand<String> {
    private Long cityId;
    public GetCityNameCommand(Long cityId){
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GetCityNameGroup"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)));
        this.cityId = cityId;
    }

    @Override
    protected String run() throws Exception {
        return LocationCache.getCityName(cityId);
    }
}
