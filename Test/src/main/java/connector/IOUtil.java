package connector;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * @Description
 * @Author syzhen
 * @CreateTime 2017年4月25日 上午11:22:46
 * @CodeReviewer
 * @ReviewTime
 * @Company CFCA
 */
public class IOUtil {
    public static final int BUFFER_SIZE = 2048;

    public static String read(InputStream inputStream, int dataLen) throws Exception {
        if (dataLen <= 0) {
            throw new Exception("data length[" + dataLen + "]");
        }
        int bufferSize = dataLen > BUFFER_SIZE ? BUFFER_SIZE : dataLen;
        BufferedInputStream bis = new BufferedInputStream(inputStream, bufferSize);
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream(bufferSize);
        byte[] tmpData = new byte[bufferSize];
        int totalReadLen = 0;
        int readLen = -1;
        while (totalReadLen < dataLen && (readLen = bis.read(tmpData)) != -1) {
            byteArray.write(tmpData, 0, readLen);
            totalReadLen += readLen;
        }
        if (dataLen != totalReadLen) {
            throw new Exception("data length[" + dataLen + "], total read length[" + totalReadLen + "]");
        }
        return new String(byteArray.toByteArray(), "UTF-8");
    }

    public static byte[] readFile(File file) throws FileNotFoundException {
        return readFile(new FileInputStream(file));
    }

    public static String readFile(String filePath) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"));
        StringBuffer sbf = new StringBuffer("");
        String line = null;
        while ((line = br.readLine()) != null) {
            sbf.append(line).append("\r\n");
        }
        br.close();
        return sbf.toString();
    }

    public static byte[] readFile(InputStream inputStream) {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            byte[] buff = new byte[BUFFER_SIZE];
            int r = 0;
            while ((r = inputStream.read(buff)) != -1) {
                baos.write(buff, 0, r);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static boolean writeFile(String fileServicePath, String filePath, byte[] fileEntity) {
        File file = new File(fileServicePath);
        if (!file.exists()) {
            file.mkdirs();
        }

        try (OutputStream os = new FileOutputStream(new File(filePath))) {
            os.write(fileEntity);
            os.flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
