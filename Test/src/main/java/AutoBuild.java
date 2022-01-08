import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author LX
 * @description
 * @date 2020/7/16 16:50
 */
public class AutoBuild {

    static int BUFFER_SIZE = 2 * 1024;

    public static void main(String[] args) {
        AutoBuild autoBuild = new AutoBuild();
//        String path = args[0];
        String path = "E:\\Job\\Code\\Fontend\\gjj\\trunk\\gjj-pc";
        String cmd = "npm run uat";
        System.out.println(cmd);
        autoBuild.runCMD(cmd, path);
        String sourceFilePath = path + "\\dist";
//        String zipFilePath = "D:\\tmp";
        String fileName = "dist";
        boolean flag = fileToZip(sourceFilePath, path, fileName);
        if(flag){
            System.out.println("文件打包成功!");
        }else{
            System.out.println("文件打包失败!");
        }
    }

    /**
     * 执行cmd命令
     *
     * @param path 工作路径
     * @param cmd 命令
     */
    private void runCMD(String path, String cmd) {
        try {
            Process process = Runtime.getRuntime().exec(cmd, new String[]{""}, new File(path));
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "GBK");
            BufferedReader br = new BufferedReader(isr);
            String content = br.readLine();
            while (content != null) {
                System.out.println(content);
                content = br.readLine();
            }
            System.out.println("执行完毕:" + cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 将存放在sourceFilePath目录下的源文件，打包成fileName名称的zip文件，并存放到zipFilePath路径下
     * @param sourceFilePath :待压缩的文件路径
     * @param zipFilePath :压缩后存放路径
     * @param fileName :压缩后文件的名称
     * @return 执行结果
     */
    public static boolean fileToZip(String sourceFilePath,String zipFilePath,String fileName){
        boolean flag = false;
        File sourceFile = new File(sourceFilePath);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        ZipOutputStream zos = null;

        if(!sourceFile.exists()){
            System.out.println("待压缩的文件目录："+sourceFilePath+"不存在.");
        }else{
            try {
                File zipFile = new File(zipFilePath + "/" + fileName +".zip");
                if(zipFile.exists()){
                    System.out.println(zipFilePath + "目录下存在名字为:" + fileName +".zip" +"打包文件.");
                }else{
                    File[] sourceFiles = sourceFile.listFiles();
                    if(null == sourceFiles || sourceFiles.length<1){
                        System.out.println("待压缩的文件目录：" + sourceFilePath + "里面不存在文件，无需压缩.");
                    }else{
                        fos = new FileOutputStream(zipFile);
                        zos = new ZipOutputStream(new BufferedOutputStream(fos));
                        byte[] bufs = new byte[1024*10];
                        for (File file : sourceFiles) {
                            //创建ZIP实体，并添加进压缩包
                            ZipEntry zipEntry = new ZipEntry(file.getName());
                            zos.putNextEntry(zipEntry);
                            //读取待压缩的文件并写进压缩包里
                            fis = new FileInputStream(file);
                            bis = new BufferedInputStream(fis, 1024 * 10);
                            int read = 0;
                            while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
                                zos.write(bufs, 0, read);
                            }
                        }
                        flag = true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally{
                //关闭流
                try {
                    if(null != bis) bis.close();
                    if(null != zos) zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
        return flag;
    }
}
