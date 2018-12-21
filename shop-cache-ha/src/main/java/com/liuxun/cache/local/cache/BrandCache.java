package com.liuxun.cache.local.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * @apiNote 品牌缓存
 */
public class BrandCache {
    private static Map<Long,String> brandMap = new HashMap<>();
    static {
        brandMap.put(1L,"iphone X");
    }

    public static String getBrandName(Long brandId){
        return brandMap.get(brandId);
    }
}
