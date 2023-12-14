package com.sky;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
//@SpringBootApplication 是一个非常关键的注解，用于启动一个基于 Spring Boot 的应用程序。这个注解实际上是一个复合注解，它组合了三个其它的 Spring 注解：
//@Configuration：标记一个类为配置类，表示其中定义了应用程序的bean。
//@EnableAutoConfiguration：告诉 Spring Boot 根据你添加的依赖自动配置你的 Spring 应用。例如，如果你在类路径中有 H2 数据库，那么 Spring Boot 会自动配置一个内存数据库。
//@ComponentScan：让 Spring 扫描当前包和子包中的组件、服务、配置等。
//通常，在 Spring Boot 应用程序的主类上使用 @SpringBootApplication 注解，这样 Spring Boot 就可以启动整个应用程序了。
@EnableTransactionManagement //开启注解方式的事务管理
@Slf4j
@EnableCaching//开启redis缓存注解的功能
@EnableScheduling//开启任务调度 执行定时任务
public class SkyApplication {
    public static void main(String[] args) {
        SpringApplication.run(SkyApplication.class, args);
        log.info("server started");
    }
}
