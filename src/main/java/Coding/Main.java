package Coding;

import java.util.Arrays;

import Coding.Utils.ListNode;

public class Main {

    public int lengthOfLIS(int[] nums) {
        int maxLen = 1;
        int[] dp = new int[nums.length];
        Arrays.fill(dp, 1);
        for (int end = 1; end <= nums.length - 1; end++) {
            for (int oldEnd = 0; oldEnd <= end - 1; oldEnd++) {
                if (nums[oldEnd] < nums[end]) {
                    dp[end] = Math.max(dp[oldEnd] + 1, dp[end]);
                }
            }
            maxLen = Math.max(maxLen, dp[end]);
        }
        return maxLen;
    }


    public void run() {

    }

    public static void main(String[] args) {
        new Main().run();
    }

}
