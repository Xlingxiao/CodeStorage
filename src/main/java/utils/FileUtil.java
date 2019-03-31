package utils;

import java.io.*;

/**
 * @Author: Administrator
 * @Date: 2019/3/21 13:12
 * @Version: 1.0
 */
@SuppressWarnings({"WeakerAccess", "unused", "ConstantConditions"})
public class FileUtil {

    /*获得文件内容消除换行，是否消除换行*/
    public String getFileContent(String path,boolean cleanLineCode) {
        StringBuilder sb = new StringBuilder();
        File file = new File(path);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String tmp;
            if(cleanLineCode)
                while ((tmp = br.readLine()) != null) {
                    sb.append(tmp);
                }
            else
                while ((tmp = br.readLine()) != null) {
                    sb.append(tmp).append("\n");
                }
            br.close();
        } catch (FileNotFoundException e) {
            System.out.println(String.format("找不到文件：%s", path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /*获得文件夹下所有文件内容*/
    public String getDirAllFileContent(String dir) {
        File dirFile = new File(dir);
        int len = dirFile.listFiles().length;
        StringBuilder sb = new StringBuilder();
        File tmpFile;
        for (int i = 0; i < len; i++) {
            tmpFile = new File(String.format("%s\\%d.txt", dir, i));
            sb.append(getFileContent(tmpFile.getAbsolutePath(), false));
        }
        return sb.toString();
    }

    /*将String写出到磁盘*/
    public void writeString(String data, String path) {
        File file = new File(path);
//        data = data.replaceAll("\\s+", "\n");
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(data.getBytes());
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*将数组内容写出磁盘*/
    public void writeNums(int[] nums, String path) {
        StringBuilder sb = new StringBuilder();
        for (int num : nums)
            sb.append(num).append("\n");
        writeString(sb.toString(), path);
    }
}
