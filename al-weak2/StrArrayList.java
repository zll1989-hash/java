package com.sankuai.inf.leaf.server.Algo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p> Description:StrArrayList </p>
 *
 * @Author Mike
 * @create 2021/8/13 18:01
 */


public class StrArrayList {

    private static final Logger logger = LoggerFactory.getLogger(StrArrayList.class);

    /**
     *  简单的方法
     * @param n
     * @return
     */
    public static List<String> fizzBuzz(int n) {

        List<String> ans = new ArrayList<String>();

        for (int num = 1; num <= n; num++) {

            boolean divisibleBy3 = (num % 3 == 0);
            boolean divisibleBy5 = (num % 5 == 0);

            if (divisibleBy3 && divisibleBy5) {
                // Divides by both 3 and 5, add FizzBuzz
                ans.add("FizzBuzz");
            } else if (divisibleBy3) {
                // Divides by 3, add Fizz
                ans.add("Fizz");
            } else if (divisibleBy5) {
                // Divides by 5, add Buzz
                ans.add("Buzz");
            } else {
                // Not divisible by 3 or 5, add the number
                ans.add(Integer.toString(num));
            }
        }
        return ans;
    }

    /**
     *使用散列表进行处理
     * {
     *   3: 'Fizz',
     *   5: 'Buzz',
     *   7: 'Jazz'
     * }
     * @param n
     *
     */
    public static List<String> fizzBuzzByMap(int n) {

        List<String> ans = new ArrayList<String>();
        HashMap<Integer, String> fizzBizzDict = new HashMap<Integer, String>(5) {
            {
                put(3, "Fizz");
                put(5, "Buzz");
                put(7, "lucien");
            }
        };

        for (int num = 1; num <= n; num++) {
            String numAnsStr = "";
            for (Integer key : fizzBizzDict.keySet()) {
                if (num % key == 0) {
                    numAnsStr += fizzBizzDict.get(key);
                }
            }
            if (numAnsStr.equals("")) {
                numAnsStr += Integer.toString(num);
            }
            // Append the current answer str to the ans list
            ans.add(numAnsStr);
        }
        return ans;

    }

    public static void main(String[] args) {

        // do test cast
        int n = 15;
        List<String> resultList = null;
        resultList = fizzBuzz(n);
        logger.info("n输出数组的长度length:{},结果resultList:{}", resultList.size(), resultList);
        // use  map  to  do
        int m = 28;
        resultList  = fizzBuzzByMap(m);
        logger.info("m输出数组的长度length:{},结果resultList:{}", resultList.size(), resultList);

    }

}
