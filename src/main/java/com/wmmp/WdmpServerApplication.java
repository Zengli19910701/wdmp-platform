package com.wmmp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/** WDMP 工业互联网平台 - 主启动类 */
@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableTransactionManagement
@MapperScan("com.wmmp.**.mapper")
public class WdmpServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(WdmpServerApplication.class, args);
    }
}
