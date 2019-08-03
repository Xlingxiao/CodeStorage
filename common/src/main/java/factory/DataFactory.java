package factory;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

@SuppressWarnings("unused")
public class DataFactory {

    /**
     * @param min 生成的最小值
     * @param max 生成的最大值
     * @param count 随机数数量
     * @return 生成的随机数数组
     */
    public int[] getArrays(int min, int max, int count) {
        int[] nums = new int[count];
        int a = max - min;
        for (int i = 0; i < count; i++) {
            nums[i] = (int) ((Math.random() * a) + min);
        }
        return nums;
    }

    /**
     * 同上，返回数组前先打印
     * @param min 生成的最小值
     * @param max 生成的最大值
     * @param count 随机数数量
     * @return 生成的随机数数组
     */
    public int[] getArraysAndDisplay(int min, int max, int count) {
        int[] nums = getArrays(min, max, count);
        System.out.println(Arrays.toString(nums));
        return nums;
    }

    /**
     * @param msg 需要切割为数组的字符串
     * @param splitChar 用什么字符进行切割
     * @return 切割生成的数组
     */
    public int[] getArrays(String msg, String splitChar) {
        String[] ss = msg.split(splitChar);
        int[] nums = new int[ss.length];
        for (int i = 0; i < nums.length; i++)
            nums[i] = Integer.parseInt(ss[i]);
        return nums;
    }

    /**
     * 返回数组类型为long
     * @param min 生成的最小值
     * @param max 生成的最大值
     * @param count 随机数数量
     * @return 生成的随机数数组
     */
    public long[] getArrays(long min, long max, int count) {
        long[] nums = new long[count];
        long a = max - min;
        for (int i = 0; i < count; i++) {
            nums[i] = (long) ((Math.random() * a) + min);
        }
        return nums;
    }

    /**
     * 将随机数生成到一个文件中
     * @param min 最小数
     * @param max 最大数
     * @param eachLineCount 每行多少数
     * @param lineCount 总共多少行
     * @param path 输出到什么位置
     * @throws IOException 文件输入输出异常
     */
    public void generatorToFile(int min, int max, int eachLineCount, int lineCount, String path) throws IOException {
        FileOutputStream fos = new FileOutputStream(path);
        BufferedOutputStream bf = new BufferedOutputStream(fos);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lineCount; i++) {
            for (int j = 0; j < eachLineCount; j++) {
                int tmp = (int) (Math.random() * (max - min) + min);
                sb.append(tmp).append(' ');
            }
            sb.append("\n");
            bf.write(sb.toString().getBytes());
            bf.flush();
            sb.delete(0, sb.length());
        }
        bf.close();
    }

    public void display(int[] nums) {
        System.out.println(Arrays.toString(nums));
    }
}
