package com.ejlerp.cache.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ejlerp.cache.api.SerialNoGenerator;
import com.ejlerp.cache.test.BaseTest;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CountDownLatch;

class SerialNoGeneratorImplTest extends BaseTest {
    private static final int threadNum = 200;
    @Autowired
    private SerialNoGenerator serialNoGenerator;
    private CountDownLatch cdl = new CountDownLatch(threadNum);


    //模拟短时间内的并发请求量

    @Test
    void generateSerialNumber() {
        JSONObject jsonObject = JSON.parseObject("{\"table_name\":\"wms_test_table\",\"createdAt\":1621906387295,\"lastUpdated\":1621906387295,\"usable\":true,\"tenantId\":-1}");
        String number1 = serialNoGenerator.generateSerialNumber(jsonObject);
        System.out.println(number1);

    }

    @Before
    public void init() {
        //执行前准备
    }

    @Test
    public void testMain() {
        for (int i = 0; i < threadNum; i++) {
            new Thread(new UserRequest()).start();
            //倒计时计数一次
            cdl.countDown();

        }
        try {
            //阻塞主线程，等待所有的子线程执行完毕

            Thread.currentThread().join();

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    private class UserRequest implements Runnable {
        @Override

        public void run() {
            try {
                cdl.await();

            } catch (InterruptedException e) {
                e.printStackTrace();

            }

            //此处执行对应的逻辑

            JSONObject jsonObject = JSON.parseObject("{\"table_name\":\"wms_receive_order\",\"createdAt\":1621906387295,\"lastUpdated\":1621906387295,\"usable\":true,\"tenantId\":-1}");
            String s = serialNoGenerator.generateSerialNumber(jsonObject);
            System.out.println("s=" + s);
        }

    }
}