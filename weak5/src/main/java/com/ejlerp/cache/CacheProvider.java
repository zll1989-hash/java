package com.ejlerp.cache;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.ejlerp.common.constants.CommonConfigConstants;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * @author tandan
 */
@Configuration
@EnableCaching
@EnableApolloConfig
@EnableEncryptableProperties
@DubboComponentScan(basePackages = {
        CommonConfigConstants.PROJECT_CACHE})
@SpringBootApplication(scanBasePackages = {
        CommonConfigConstants.PROJECT_CACHE})
@MapperScan(CommonConfigConstants.PROJECT_CACHE + ".mapper")
public class CacheProvider {
    public static void main(String[] args) {
        SpringApplication.run(CacheProvider.class, args);
    }
}
