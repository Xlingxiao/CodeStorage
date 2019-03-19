package utils;

@SuppressWarnings("unused")
public class DataUtil {

    public int[] getArrays(int min, int max, int count) {
        int[] nums = new int[count];
        int a = max - min;
        for (int i = 0; i < count; i++) {
            nums[i] = (int) ((Math.random() * a) + min);
        }
        return nums;
    }

    public int[] getArraysAndDisplay(int min, int max, int count) {
        int[] nums = getArrays(min, max, count);
        display(nums);
        return nums;
    }

    public int[] getArrays(String msg, String splitChar) {
        String[] ss = msg.split(splitChar);
        int[] nums = new int[ss.length];
        for (int i = 0; i < nums.length; i++)
            nums[i] = Integer.parseInt(ss[i]);
        return nums;
    }

    public long[] getArrays(long min, long max, int count) {
        long[] nums = new long[count];
        long a = max - min;
        for (int i = 0; i < count; i++) {
            nums[i] = (long) ((Math.random() * a) + min);
        }
        return nums;
    }

    public void display(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            System.out.print(nums[i] + " ");
        }
        System.out.println();

    }
}
