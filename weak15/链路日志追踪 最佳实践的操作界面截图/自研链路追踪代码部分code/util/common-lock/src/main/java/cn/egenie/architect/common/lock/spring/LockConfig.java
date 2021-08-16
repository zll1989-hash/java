package cn.egenie.architect.common.lock.spring;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import cn.egenie.architect.common.lock.zk.ZkLockTemplate;

/**
 * @author lucien
 * @since 2021/03/01
 */
@Configuration
@PropertySource("classpath:config/${spring.profiles.active:dev}/zk.properties")
public class LockConfig {

    @Value("${zk_host}")
    private String zkHost;

    @Bean(initMethod = "start")
    public CuratorFramework curatorClient() {
        return CuratorFrameworkFactory.builder()
                .connectString(zkHost)
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
    }

    @Bean
    public ZkLockTemplate zkLockTemplate() {
        return new ZkLockTemplate();
    }
}
