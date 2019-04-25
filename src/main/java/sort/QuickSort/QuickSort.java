package sort.QuickSort;

import org.junit.jupiter.api.Test;
import factory.DataFactory;

public class QuickSort {
    @Test
    void main() {
        int min = -10;
        int max = 20;
        int count = 10;
        DataFactory util = new DataFactory();
        int[] nums = util.getArrays(min, max, count);
        display(nums);
        quickSort(nums);
        display(nums);
    }

    public int[] quickSort(int[] nums) {
        sort(nums, 0, nums.length - 1);
        return nums;
    }

    private void sort(int[] nums, int left, int right) {
        if (left >= right) return ;
        int partition = partition(nums, left, right, nums[right]);
        sort(nums, left, partition - 1);
        sort(nums, partition + 1, right);
    }

    private int partition(int[] nums, int left, int right, int value) {
        int l = left - 1;
        int r = right;
        while (l < r) {
            while (nums[++l] < value);
            while (r > 0 && nums[--r] > value) ;
            if (l >= r) break;
            swap(nums, l, r);
        }
        swap(nums, l, right);
        return l;
    }

    private void swap(int[] nums, int a, int b) {
        int t = nums[a];
        nums[a] = nums[b];
        nums[b] = t;
    }

    private void display(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            System.out.print(nums[i] + " ");
        }
        System.out.println();

    }

}
