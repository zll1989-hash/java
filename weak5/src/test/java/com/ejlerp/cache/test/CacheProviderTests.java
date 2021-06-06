package com.ejlerp.cache.test;

import com.ejlerp.cache.CacheProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CacheProvider.class)
public class CacheProviderTests {

    @Test
    public void contextLoads() {
    }

}
