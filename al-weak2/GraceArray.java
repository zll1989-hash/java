package com.sankuai.inf.leaf.server.Algo;

/**
 * @Author Mike
 * @create 2021/6/22 8:56
 */
public class GraceArray {

    public static int numberOfSubarrays(int[] nums, int k) {
        int n = nums.length;
        int[] s = new int[n + 1];
        int[] count = new int[n + 1];
        count[s[0]]++;
        for (int i = 1; i <= n; i++) {
            s[i] = s[i - 1] + nums[i - 1] % 2;
            count[s[i]]++;
        }
        int ans = 0;
        for (int i = 1; i <= n; i++) {
            if (s[i] - k >= 0) {
                ans += count[s[i] - k];
            }
        }
        return ans;
    }

    public static void main(String[] args) {
        int[] a = {1, 1, 2, 1, 1};
        int k = 3;
        System.out.println(numberOfSubarrays(a,k));
    }

}

// s[i] - s[j] = k, 求j的数量
// s[j] = s[i] - k

