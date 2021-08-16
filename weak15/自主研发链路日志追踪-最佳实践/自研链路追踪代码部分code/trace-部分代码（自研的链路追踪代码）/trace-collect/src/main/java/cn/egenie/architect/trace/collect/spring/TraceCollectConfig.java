package cn.egenie.architect.trace.collect.spring;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import cn.egenie.architect.trace.collect.TraceCollector;
import cn.egenie.architect.trace.collect.aop.TraceAspect;

/**
 * @author lucien
 * @since 2021/08/06 2021/02/04
 */
@Configuration
@PropertySource("classpath:config/${spring.profiles.active:dev}/trace.properties")
public class TraceCollectConfig {

    /**
     * 启动trace aop扫描
     */
    @Bean
    @ConditionalOnMissingBean(TraceAspect.class)
    public TraceAspect traceAspect() {
        return new TraceAspect();
    }

    @Bean
    @ConditionalOnMissingBean(TraceCollector.class)
    public TraceCollector traceCollector() {
        return new TraceCollector();
    }
}
