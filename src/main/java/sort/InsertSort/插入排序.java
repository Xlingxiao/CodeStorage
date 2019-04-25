package sort.InsertSort;

import org.junit.jupiter.api.Test;
import factory.DataFactory;

public class 插入排序 {
    @Test
    void main() {
        DataFactory util = new DataFactory();
        int[] nums = util.getArrays(0, 20, 10);
        util.display(nums);
        util.display(insertSort(nums));
    }

    int[] insertSort(int[] nums) {
        int len = nums.length;
        for (int i = 1; i < len; i++) {
            int p = i;
            int t = nums[p];
            for (int j = i - 1; j >= 0; j--) {
                /*从小到大排用 <
                * 反之用 > */
                if(t < nums[j]){
                    nums[p--] = nums[j];
                }
                else break;
            }
            nums[p] = t;
        }
        return nums;
    }

    void swap(int[] nums, int l, int r) {
        int t = nums[l];
        nums[l] = nums[r];
        nums[r] = nums[l];
    }

}
