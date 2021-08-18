package com.sankuai.inf.leaf.server.Algo.weak1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * <p> Description:CatchRain </p>
 *
 * @Author Lucien
 * @create 2021/8/17 16:35
 */
public class CatchRain {

    private static final Logger logger = LoggerFactory.getLogger(CatchRain.class);

    private static final Integer CRITICAL_VALUE = 10;

    /**
     * 统计蓄水量
     * @param height
     * @return
     */
    public static int trapRain(int[] height) {
        int ans = 0;
        Deque<Integer> stack = new LinkedList<Integer>();
        int n = height.length;
        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && height[i] > height[stack.peek()]) {
                int top = stack.pop();
                if (stack.isEmpty()) {
                    break;
                }
                int left = stack.peek();
                int currWidth = i - left - 1;
                int currHeight = Math.min(height[left], height[i]) - height[top];
                ans += currWidth * currHeight;
            }
            stack.push(i);
        }
        return ans;
    }

    /**
     * 加1
     * @param digits
     * @return
     */
    public static int[] plusOne(int[] digits) {

        // 遍历数组元素
        for (int i = digits.length - 1; i >= 0; i--) {
            digits[i]++;
            digits[i] = digits[i] % CRITICAL_VALUE;
            if (digits[i] != 0) {
                return digits;
            }
        }

        digits = new int[digits.length + 1];
        digits[0] = 1;
        return digits;

    }

    /**
     * 移动0
     * @param nums
     */
    public static int [] moveZeroes(int[] nums) {
        if (null == nums) {
            return new int[0];
        }
        //两个指针i和j
        int j = 0;
        for (int i = 0; i < nums.length; i++) {
            //当前元素!=0，就把其交换到左边，等于0的交换到右边
            if (nums[i] != 0) {
                int tmp = nums[i];
                nums[i] = nums[j];
                nums[j++] = tmp;
            }
        }

        return nums;
    }

    /**
     *
     * @param nums
     * @param target
     * @return
     */
    public static int[] twoSum(int[] nums, int target) {

        // 设置hastTable
        Map<Integer, Integer> hashtable = new HashMap<Integer, Integer>();
        for (int i = 0; i < nums.length; i++) {
            if (hashtable.containsKey(target - nums[i])) {
                return new int[]{hashtable.get(target - nums[i]), i};
            }
            hashtable.put(nums[i], i);
        }

        return new int[0];

    }

    /**
     * 旋转数组
     * @param nums
     * @param k
     */
    public static int[] rotateArr(int[] nums, int k) {
        int n = nums.length;
        int[] newArr = new int[n];
        for (int i = 0; i < n; ++i) {
            newArr[(i + k) % n] = nums[i];
        }
        System.arraycopy(newArr, 0, nums, 0, n);
        return nums;
    }


    public static void main(String[] args) {

        logger.info("-------------split line------------------");

        logger.info(" catch rain begin");
        int[] arr = new int[]{4, 3, 2, 0, 1, 1, 5};
        int result = trapRain(arr);
        logger.info("out put result---->{}", result);
        logger.info(" catch rain end");

        logger.info("-------------split line------------------");

        logger.info(" plus + 1  begin");
        int[]  brr = new int[]{0};
        logger.info("out put plus result---->{}",plusOne(brr));
        int[]  crr = new int[]{4,3,2,1};
        logger.info("out put plus result---->{}",plusOne(crr));
        int[]  drr = new int[]{9,9,9,9};
        logger.info("out put plus result---->{}",plusOne(drr));
        logger.info(" plus + 1  end");

        logger.info("-------------split line------------------");

        logger.info(" move zero  begin");
        int[]  zeroA = new int[]{9,0,1,8,0,1,2,3,4};
        logger.info("move zero zeroA{}",moveZeroes(zeroA));
        int[]  zeroB = new int[]{0,0,0,0,0,0};
        logger.info("move zero zeroB{}",moveZeroes(zeroB));
        int[]  zeroC = new int[]{0,0,0,0,2,1};
        logger.info("move zero zeroC{}",moveZeroes(zeroC));
        logger.info(" move zero  end");

        logger.info("-------------split line------------------");

        logger.info("two nums target  begin");
        int[] twoArr  = new int[]{3,2,4};
        logger.info("two nums target:{}",twoSum(twoArr,6));
        logger.info("two nums target  end");

        logger.info("-------------split line------------------");
        logger.info("two rotateArr k  begin");
        int[] rotArr  = new int[]{1,2,3,4,5,6,7};
        logger.info("two nums target:{}",rotateArr(rotArr,3));
        logger.info("two rotateArr k   end");
        logger.info("-------------split line------------------");

    }

}
