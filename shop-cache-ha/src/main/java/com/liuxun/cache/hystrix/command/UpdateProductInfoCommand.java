package com.liuxun.cache.hystrix.command;

import com.liuxun.cache.model.ProductInfo;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class UpdateProductInfoCommand extends HystrixCommand<Boolean> {
    private Long productId;

    public UpdateProductInfoCommand(Long productId){
        super(HystrixCommandGroupKey.Factory.asKey("UpdateProductInfoGroup"));
        this.productId = productId;
    }

    @Override
    protected Boolean run() throws Exception {
        GetProductInfoCommand.flushCache(productId);
        return true;
    }
}
