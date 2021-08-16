package com.sankuai.inf.leaf.server.Algo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p> Description:NumberAndNumberPlus </p>
 *
 * @Author Mike
 * @create 2021/8/13 18:29
 */
public class NumberAndNumberPlus {

    private static final Logger logger = LoggerFactory.getLogger(NumberAndNumberPlus.class);

    public static int addDigits(int num) {
        return (num - 1) % 9 + 1;
    }

    public static void main(String[] args) {

        int num = 12345;
        int mod = addDigits(num);
        logger.info("输出结果mod:{}",mod);

    }

}
