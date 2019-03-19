package sort.Heapsort;

import org.junit.jupiter.api.Test;
import utils.DataUtil;

@SuppressWarnings({"NonAsciiCharacters", "WeakerAccess"})
public class 堆排序 {
    @Test
    void main() {
        DataUtil util = new DataUtil();
//        int[] nums = util.getArrays("16 -5 0 2 5 2 11 16 1 18", " ");
        int[] nums = util.getArrays(-10,20,10);
        util.display(nums);
        buildHeap(nums);
        util.display(nums);
        heapSort(nums);
        util.display(nums);

    }

    void heapSort(int[] nums) {
        int len = nums.length;
        if (len < 2) return;
        for (int i = 0; i < len; i++) {
            int t = nums[0];
            nums[0] = nums[len - 1 - i];
            nums[len - 1 - i] = t;
            trickDown(nums, 0, len - i - 1);
        }
    }

    /*将数组构建为堆*/
    void buildHeap(int[] nums) {
        int len = nums.length;
        for (int i = len / 2 - 1; i >= 0; i--)
            trickDown(nums, i,len);
    }

    /*找到最大的儿子，与其比较，比儿子大就和大儿子交换并继续往下*/
    void trickDown(int[] nums,int p,int length) {
        int value = nums[p];
        int minChild;
        while (p < length / 2) {
            int leftC = p * 2 + 1;
            int rightC = leftC + 1;
            if (rightC < length && nums[rightC] < nums[leftC])
                minChild = rightC;
            else minChild = leftC;
            if (value <= nums[minChild])
                break;
            nums[p] = nums[minChild];
            p = minChild;
        }
        nums[p] = value;
    }



}
