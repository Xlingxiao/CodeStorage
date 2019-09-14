package file;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;

public class JudgePicture {
    public static void main(String[] args) {
        String path = "F:\\工作\\ExcelToSql\\14\\20190902Update.sql";
        boolean flag = isImage(new File(path));
        String msg = flag ? "是" : "不是";
        System.out.printf("文件：%s %s 图片\n", path, msg);
        path = "E:\\图片\\PS\\IMG_4416.jpg";
        flag = isImage(new File(path));
        msg = flag ? "是" : "不是";
        System.out.printf("文件：%s %s 图片\n", path, msg);
    }

    public static boolean isImage(File imageFile) {
        if (!imageFile.exists()) {
            return false;
        }
        Image img = null;
        try {
            img = ImageIO.read(imageFile);
            if (img == null || img.getWidth(null) <= 0 || img.getHeight(null) <= 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            img = null;
        }
    }
}
