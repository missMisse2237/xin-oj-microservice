package com.xin.xinojbackenduserservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication()
@MapperScan("com.xin.xinojbackenduserservice.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.xin")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.xin.xinojbackendserviceclient.service"})
public class XinojBackendUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(XinojBackendUserServiceApplication.class, args);
    }

}
