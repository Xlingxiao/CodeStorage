import com.sun.corba.se.spi.orbutil.threadpool.Work;
import utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author LX
 * @description 测试多个线程同时读入一个文件
 * @date 2020/7/22 16:11
 */
public class MultiThreadFile {
    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            Worker worker = new Worker();
            new Thread(worker).start();
        }
    }

    static class Worker implements Runnable {

        String path = "E:\\Job\\Code\\testdoc\\敏捷开发脚本";

        @Override
        public void run() {
            String name = Thread.currentThread().getName();
            System.out.println(name + "已启动");
            FileUtil fileUtil = new FileUtil();
            String content = fileUtil.getFileContent(path, false);
            System.out.println(content);
            String outputFileName = path + UUID.randomUUID().toString().replace("-", "");
            fileUtil.writeString(content, outputFileName);
            File outputFile = new File(outputFileName);
            File file = new File(path + ".out");
            if (!outputFile.renameTo(file)) {
                outputFile.delete();
            }
        }
    }
}
