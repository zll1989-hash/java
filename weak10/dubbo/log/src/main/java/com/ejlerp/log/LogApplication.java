package com.ejlerp.log;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

/**
 * @author paul
 */
@Configuration
@EnableApolloConfig
@SpringBootApplication(scanBasePackages = {"com.ejlerp", "cn.egenie.mq"})
@MapperScan(basePackages = {"com.ejlerp.log.mapper"})
public class LogApplication {
    public static void main(String[] args) {
        SpringApplication.run(LogApplication.class);
    }
}
