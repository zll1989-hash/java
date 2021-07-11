package com.ejlerp.log.config;

import com.ejlerp.common.config.DruidConfig;
import com.ejlerp.common.config.DubboConfig;
import com.ejlerp.common.config.LogListenerConfig;
import com.ejlerp.common.constants.CommonConfigConstants;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

@Configuration
@Import({DubboConfig.class, DruidConfig.class, DubboConfig.class, LogListenerConfig.class})
@ImportResource(locations = {CommonConfigConstants.DUBBO_XML})
public class AppConfig {
    public AppConfig() {
    }
}