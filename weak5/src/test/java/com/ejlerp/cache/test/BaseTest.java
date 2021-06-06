package com.ejlerp.cache.test;

import com.ejlerp.cache.CacheProvider;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * BaseTest
 *
 * @author Eric
 * @date 16/5/13
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CacheProvider.class)
@WebAppConfiguration
public class BaseTest {

}
