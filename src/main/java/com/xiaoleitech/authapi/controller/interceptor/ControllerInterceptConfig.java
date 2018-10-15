package com.xiaoleitech.authapi.controller.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class ControllerInterceptConfig extends WebMvcConfigurationSupport {
    private final AuthControllerInterceptor authControllerInterceptor;

    @Autowired
    public ControllerInterceptConfig(AuthControllerInterceptor authControllerInterceptor) {
        this.authControllerInterceptor = authControllerInterceptor;
    }

    /**
     * 针对异步的拦截器配置，拦截异步请求
     * @param configurer
     */
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        super.configureAsyncSupport(configurer);
        //比如如下给异步服务请求添加拦截器
        //configurer.registerCallableInterceptors((CallableProcessingInterceptor) timeInterceptor);
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authControllerInterceptor);
    }

}
