package com.xin.xinojbackendjudgeservice;

import com.xin.xinojbackendjudgeservice.message.InitRabbitMQ;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.xin")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.xin.xinojbackendserviceclient.service"})
public class XinojBackendJudgeServiceApplication {

    public static void main(String[] args) {
        // 项目启动前执行一次！
        InitRabbitMQ.doInit();;
        SpringApplication.run(XinojBackendJudgeServiceApplication.class, args);
    }

}
