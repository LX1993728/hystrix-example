package com.liuxun.cache;

import com.liuxun.cache.filter.HystrixRequestContextFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ShopCacheHaApplication {

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registration = new FilterRegistrationBean(new HystrixRequestContextFilter());
        registration.addUrlPatterns("/*");
        return registration;
    }

    public static void main(String[] args) {
        SpringApplication.run(ShopCacheHaApplication.class, args);
    }

}

