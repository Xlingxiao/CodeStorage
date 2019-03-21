package sort.DiskSort;

import org.junit.jupiter.api.Test;
import utils.DataUtil;
import utils.FileUtil;

import java.io.*;
import java.util.Arrays;

/**
 * @Author： Administrator
 * @Date: 2019/3/21 13:11
 * @Version: 1.0
 * 100万条数据
 * 切分文件用时：72ms
 * 小文件排序用时：840ms
 * 合并文件排序用时：946ms
 *
 * 1000万条数据
 * 切分文件用时：545ms
 * 小文件排序用时：4464ms
 * 合并文件排序用时：8116ms
 */
@SuppressWarnings({"SameParameterValue", "Duplicates", "WeakerAccess"})
public class DiskSort {
    @Test
    void main() throws IOException {
        DataUtil util = new DataUtil();
        String path = "D:\\LX\\target.txt";
        int min = -100000;
        int max = 100000;
        int eachLineCount = 100;
        int lineCount = 100000;
        util.generatorToFile(min, max, eachLineCount, lineCount, path);
        start(path);
    }

    /*开始排序*/
    void start(String target) {
        File file = new File(target);
        if (file.isDirectory()) return;
        String parent = file.getParent();
        String dirPath = parent + "\\result";
        File dir = new File(dirPath);
        if (!dir.mkdir()) System.out.println(String.format("文件夹已存在:%s", dirPath));
        long length = file.length();
        int fileNum;
        if (length > 1024 * 1024 * 100) {
            fileNum = 2000;
        } else if (length > 1024 * 1024 * 10) {
            fileNum = 10;
        } else {
            fileNum = 2;
        }
        long startTime = System.currentTimeMillis();
        splitFile(file, fileNum, dirPath);
        System.out.println(String.format("切分文件用时：%sms",System.currentTimeMillis() - startTime));
        mapSort(dirPath);
        System.out.println(String.format("小文件排序用时：%sms",System.currentTimeMillis() - startTime));
        int len = mergeSort(dirPath);
        System.out.println(String.format("排序后文件路径：%s\\%d.txt", dirPath, len));
        System.out.println(String.format("合并文件排序用时：%sms",System.currentTimeMillis() - startTime));
    }

    /*将排序的小文件归并为大文件*/
    int mergeSort(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.isDirectory()) return 0;
        File[] files = dir.listFiles();
        if (files == null) return 0;
        int len = files.length;
        int rowLen = len;
        while (len > 1) {
            int count = len / 2;
            for (int i = 0; i < count; i++) {
                int current = i * 2;
                sort(files[current], files[current + 1], new File(String.format("%s\\%d.txt",dirPath,rowLen++)));
            }
            files = dir.listFiles();
            if (files == null) return 0;
            len = files.length;
        }
        return rowLen - 1;
    }

    /**
     * 将两个文件归并为一个文件
     * @param first 第一个文件
     * @param second 第二个文件
     * @param assist 输出的大文件
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void sort(File first, File second, File assist) {
        if (first == second) return;
        String f;
        String s;
        try {
            BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(first)));
            BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream(second)));
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(assist));
            int i = 0;
            StringBuilder sb = new StringBuilder();
            f = br1.readLine();
            s = br2.readLine();
            while (f != null || s != null) {
                if(s == null){
                    sb.append(f).append('\n');
                    f = br1.readLine();
                } else if (f == null){
                    sb.append(s).append('\n');
                    s = br2.readLine();
                }else if (f.compareTo(s) < 0) { //走到这里说明s/f都不为空，只需要判断s和f谁大即可
                    sb.append(f).append('\n');
                    f = br1.readLine();
                }else {
                    sb.append(s).append('\n');
                    s = br2.readLine();
                }
                i++;
                if (i >= 10000) {
                    bos.write(sb.toString().getBytes());
                    bos.flush();
                    sb.delete(0, sb.length());
                    i = 0;
                }
            }
            bos.write(sb.toString().getBytes());
            bos.flush();
            bos.close();
            br1.close();
            br2.close();
            first.delete();
            second.delete();
        } catch (FileNotFoundException e) {
            System.out.println("没找到文件");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*所有小文件排序*/
    void mapSort(String dir) {
        File dirFile = new File(dir);
        File[] files = dirFile.listFiles();
        if (files != null) {
            for (File file : files) {
                aloneSort(file);
//                System.out.println(String.format("文件排序成功:%s", file.getAbsolutePath()));
            }
        }
    }

    /*单个小文件排序*/
    void aloneSort(File file) {
        FileUtil util = new FileUtil();
        String string = util.getFileContent(file.getAbsolutePath(),true);
        String[] strings = string.split(" ");
        int[] nums = new int[strings.length];
        for (int i = 0; i < nums.length; i++) {
            nums[i] = Integer.parseInt(strings[i]);
        }
        Arrays.sort(nums);
        util.writeNums(nums, file.getAbsolutePath());
    }

    /*将文件切分*/
    @SuppressWarnings({"ResultOfMethodCallIgnored", "LoopConditionNotUpdatedInsideLoop"})
    void splitFile(File file,int fileNum,String initPath) {
        try {
            long len = file.length();
            if(len < fileNum) return;
            File tFile;
            //每个文件读取segmentIncludeNum个数字
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            StringBuilder sb = new StringBuilder();
//            每个文件中都有count个字符
            int count = (int) (len / fileNum);
            char[] chars = new char[count];
            char[] c1 = new char[1];
            for (int i = 0; i < fileNum; i++) {
                br.read(chars);
                if (len > count)
                    sb.append(chars, 0, count);
                else
                    sb.append(chars, 0, (int) len);
                 len -= count;
                char c = sb.charAt(sb.length() - 1);
//                防止将数字切分
                while (c >= 48 && c <= 57 || c == '-') {
                    br.read(c1);
                    len--;
                    //如果自己是数字下一个也是数字,将这个数组添加到sb中
                    if (c1[0] >= 48 && c1[0] <= 57) sb.append(c1[0]);
                    else break;
                }
                tFile = new File(String.format("%s\\%d.txt", initPath, i));
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tFile));
                bos.write(sb.toString().getBytes());
                bos.flush();
                bos.close();
                sb.delete(0, sb.length());
            }
            br.close();

        } catch (FileNotFoundException e) {
            System.out.println("文件不存在");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
