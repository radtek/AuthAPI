package com.xiaoleitech.authapi;

import com.xiaoleitech.authapi.global.dictionary.SystemGlobalParams;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
//@ComponentScan(basePackages = {"com.xiaoleitech.authapi.mapper", "com.xiaoleitech.authapi.service", "com.xiaoleitech.authapi.controller"})
@MapperScan(basePackages = {"com.xiaoleitech.authapi.mapper", "com.xiaoleitech.authapi.Test.mapper", "com.xiaoleitech.authapi.global.dictionary"})
//@MapperScan(basePackages = {"com.xiaoleitech.authapi.mapper"})
public class AuthapiApplication {
//    @Bean
//    public HttpMessageConverters fastJsonHttpMessageConverters() {
//        // 1.定义一个converters转换消息的对象
//        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
//        // 2.添加fastjson的配置信息，比如: 是否需要格式化返回的json数据
//        FastJsonConfig fastJsonConfig = new FastJsonConfig();
//        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
//        // 3.在converter中添加配置信息
//        fastConverter.setFastJsonConfig(fastJsonConfig);
//        // 4.将converter赋值给HttpMessageConverter
//        HttpMessageConverter<?> converter = fastConverter;
//        // 5.返回HttpMessageConverters对象
//        return new HttpMessageConverters(converter);
//    }



    public static void main(String[] args) {
//        ApplicationContext applicationContext;
//        applicationContext = new AnnotationConfigApplicationContext(com.xiaoleitech.authapi.AuthapiApplication.class);
//        SystemGlobalParams systemGlobalParams = applicationContext.getBean(SystemGlobalParams.class);
//
////        SystemGlobalParams systemGlobalParams = new SystemGlobalParams();
//
//        systemGlobalParams.initGlobalDictionary();

        // TODO: 服务启动时载入全局系统参数

        SpringApplication.run(AuthapiApplication.class, args);
    }
}
