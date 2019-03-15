package sort.mergeSort;

import org.junit.jupiter.api.Test;
import utils.DataUtil;


public class MergeSort {
    @Test
    void Main() {
        DataUtil util = new DataUtil();
        int min = -10;
        int max = 20;
        int count = 10;
        int[] nums = util.getArraysAndDisplay(min, max, count);
        mergeSort(nums);
        util.display(nums);
    }

    public int[] mergeSort(int[] nums) {
        merge(nums, new int[nums.length], 0, nums.length - 1);
        return nums;
    }

    private void merge(int[] nums, int[] assistSpace, int left, int right) {
        if (left >= right) return;
        int mid = (left + right) / 2;
        merge(nums,assistSpace, left, mid);
        merge(nums, assistSpace, mid + 1, right);
        sort(nums, assistSpace, left, mid + 1, right);
    }

    private void sort(int[] nums, int[] assistSpace, int firstStart, int secondStart, int secondEnd) {
        int firstEnd = secondStart - 1;
        int firstBound = firstStart;
        int i = 0;
        while (firstStart <= firstEnd && secondStart <= secondEnd) {
            if (nums[firstStart] < nums[secondStart]) assistSpace[i++] = nums[firstStart++];
            else assistSpace[i++] = nums[secondStart++];
        }
        while (firstStart <= firstEnd)
            assistSpace[i++] = nums[firstStart++];
        while (secondStart <= secondEnd)
            assistSpace[i++] = nums[secondStart++];
        int n = secondEnd - firstBound + 1;
        for (int j = 0; j < n; j++) {
            nums[firstBound + j] = assistSpace[j];
        }
    }



}
