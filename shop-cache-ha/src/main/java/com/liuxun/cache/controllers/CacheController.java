package com.liuxun.cache.controllers;

import com.alibaba.fastjson.JSON;
import com.liuxun.cache.http.HttpClientUtils;
import com.liuxun.cache.hystrix.command.*;
import com.liuxun.cache.model.ProductInfo;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixObservableCommand;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import rx.Observable;
import rx.Observer;

import java.util.concurrent.Future;

/**
 * 缓存服务的接口
 * @author Administrator
 *
 */
@Controller
public class CacheController {

	/**
	 * @apiNote 模拟订阅消息队列 接收商品服务发送商品变更的通知
	 * @param productId
	 * @return
	 */
	@RequestMapping("/change/product")
	@ResponseBody
	public String changeProduct(Long productId) {
		// 拿到一个商品id
		// 调用商品服务的接口，获取商品id对应的商品的最新数据
		// 用HttpClient去调用商品服务的http接口
		String url = "http://127.0.0.1:8082/getProductInfo?productId=" + productId;
		String response = HttpClientUtils.sendGetRequest(url);
		System.out.println(response);  
		
		return "success";
	}

	/**
	 * @apiNote 场景: 由nginx开始，各级缓存都失效了，nginx发送很多的请求直接到缓存服务要求拉取最原始的数据
	 * @param productId
	 * @return
	 */
	@RequestMapping("/getProductInfo")
	@ResponseBody
	public String getProductInfo(Long productId) throws InterruptedException {
		// 拿到一个商品id
		// 调用商品服务的接口，获取商品id对应的商品的最新数据
		// 用HttpClient去调用商品服务的http接口
        HystrixCommand<ProductInfo> getProductInfoCommand = new GetProductInfoCommand(productId);
        ProductInfo productInfo = getProductInfoCommand.execute(); // 同步执行

//        final Future<ProductInfo> future = getProductInfoCommand.queue(); // 异步执行
//        Thread.sleep(1000);
//        ProductInfo productInfo = future.get();

        //基于信号量
        final Long cityId = productInfo.getCityId();
        HystrixCommand<String> getCityNameCommand = new GetCityNameCommand(cityId);
        final String cityName = getCityNameCommand.execute();
        productInfo.setCityName(cityName);

        System.out.println(JSON.toJSONString(productInfo));
        return "success";
	}

	// 模拟一次性批量查询多个商品的请求
    @RequestMapping("/getProductInfos")
    @ResponseBody
    public String getProductInfos(String productIds) {
        // 拿到一个商品id
        // 调用商品服务的接口，获取商品id对应的商品的最新数据
        // 用HttpClient去调用商品服务的http接口
        HystrixObservableCommand<ProductInfo> getProductInfosCommand = new GetProductInfosCommand(productIds.split(","));
        final Observable<ProductInfo> observable = getProductInfosCommand.observe(); // 立即执行
//        final Observable<ProductInfo> observable = getProductInfosCommand.toObservable(); // 延迟执行
        observable.subscribe(new Observer<ProductInfo>() { // toObservable只有等到调用此subscribe方法时才执行
            @Override
            public void onCompleted() {
                System.out.println("获取完了所有的商品数据");
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(ProductInfo productInfo) {
                System.out.println(JSON.toJSONString(productInfo));
            }
        });
        return "success";
    }

    /**
     * @apiNote 测试Request cache
     * @param productIds
     * @return
     */
    @RequestMapping("/getProductInfos2")
    @ResponseBody
    public String getProductInfos2(String productIds) {
        for(String productId : productIds.split(",")){
            GetProductInfoCommand getProductInfoCommand = new GetProductInfoCommand(Long.valueOf(productId));
            final ProductInfo productInfo = getProductInfoCommand.execute();
            System.out.println(JSON.toJSONString(productInfo));
            System.out.println(getProductInfoCommand.isResponseFromCache());

        }
        return "success";
    }

    /**
     * @apiNote 测试获取品牌名称的fall-back降级
     * @param productId
     * @return
     */
    @RequestMapping("/getProductInfo2")
    @ResponseBody
    public String getProductInfo2(Long productId) {
        HystrixCommand<ProductInfo> getProductInfoCommand = new GetProductInfoCommand(productId);
        ProductInfo productInfo = getProductInfoCommand.execute();
        final Long cityId = productInfo.getCityId();

        HystrixCommand<String> getCityNameCommand = new GetCityNameCommand(cityId);
        final String cityName = getCityNameCommand.execute();
        productInfo.setCityName(cityName);

        GetBrandNameCommand getBrandNameCommand = new GetBrandNameCommand(productInfo.getBrandId());
        String brandName = getBrandNameCommand.execute();
        productInfo.setBrandName(brandName);

        System.out.println(JSON.toJSONString(productInfo));

        return "success";
    }


    /**
     * @apiNote 测试短路器
     * @param productId
     * @return
     */
    @RequestMapping("/getProductInfo3")
    @ResponseBody
    public ProductInfo getProductInfo3(Long productId) {
        HystrixCommand<ProductInfo> getProductInfo3Command = new GetProductInfo3Command(productId);
        ProductInfo productInfo = getProductInfo3Command.execute();
        System.out.println(JSON.toJSONString(productInfo));

        return productInfo;
    }

    /**
     * @apiNote 限流实验测试
     * @param productId
     * @return
     */
    @RequestMapping("/getProductInfo4")
    @ResponseBody
    public ProductInfo getProductInfo4(Long productId) {
        HystrixCommand<ProductInfo> getProductInfo4Command = new GetProductInfo4Command(productId);
        ProductInfo productInfo = getProductInfo4Command.execute();
        System.out.println(JSON.toJSONString(productInfo));

        return productInfo;
    }


}
