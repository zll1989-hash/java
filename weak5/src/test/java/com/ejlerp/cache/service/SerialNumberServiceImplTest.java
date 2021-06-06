package com.ejlerp.cache.service;

import com.ejlerp.cache.api.SerialNumberService;
import com.ejlerp.cache.test.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SerialNumberServiceImplTest extends BaseTest {
    @Autowired
    private SerialNumberService serialNumberService;

    @Test
    void addDefaultRule() {
        serialNumberService.addDefaultRule("wms_receive_order");
    }
}