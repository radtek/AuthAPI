package com.xiaoleitech.authapi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@ComponentScan(basePackages = {"com.xiaoleitech.authapi.mapper", "com.xiaoleitech.authapi.service", "com.xiaoleitech.authapi.controller"})
@MapperScan(basePackages = {"com.xiaoleitech.authapi.mapper", "com.xiaoleitech.authapi.Test.mapper"})
//@MapperScan(basePackages = {"com.xiaoleitech.authapi.mapper"})
public class AuthapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthapiApplication.class, args);
    }
}
