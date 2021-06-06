package com.ejlerp.cache.config;

import com.ejlerp.common.config.DruidConfig;
import com.ejlerp.common.config.DubboConfig;
import com.ejlerp.common.config.LogListenerConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({DubboConfig.class, DruidConfig.class, LogListenerConfig.class})
public class AppConfig {
}
