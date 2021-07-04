package com.itcast.rpc.server.startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.itcast"})
@SpringBootApplication
public class RpcServerApplication implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServerApplication.class);

    @Autowired
    private RpcServerRunner znsServerRunner;

    public static void main(String[] args) {
        SpringApplication.run(RpcServerApplication.class, args);

        LOGGER.info("Zns service provider application startup successfully");

    }

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        znsServerRunner.run();
    }
}

