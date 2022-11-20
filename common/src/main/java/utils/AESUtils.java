package utils;

import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * AES加密类
 */
public class AESUtils {

    private static final String key = "AAAAEmx4QERFU0tU";
    private static final String iv = "b3BlbnNzaC1rZXkt";


    /**
     * AES加密
     * @param str 待加密对象
     * @return 加密后的字符 base64编码
     * @throws Exception 加密失败
     */
    public static String encrypt(String str) throws Exception{
        if (StringUtils.isEmpty(str)) {
            return null;
        }

        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        int blockSize = cipher.getBlockSize();
        byte[] dataBytes = str.getBytes();
        int dataBytesLength = dataBytes.length;
        if (dataBytesLength % blockSize != 0) {
            dataBytesLength = dataBytesLength + (blockSize - (dataBytesLength % blockSize));
        }
        byte[] plainText = new byte[dataBytesLength];
        System.arraycopy(dataBytes, 0, plainText, 0, dataBytes.length);

        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec);
        byte[] encryptedStr = cipher.doFinal(plainText);

        return new String(Base64.getEncoder().encode(encryptedStr)).trim();
    }

    /**
     * 解密
     * @param encryptStr 待解密字符 base64
     * @return 解密后字符
     * @throws Exception 解密报错
     */
    public static String decrypt(String encryptStr) throws Exception{
        return decrypt(encryptStr, true);
    }

    /**
     * 解密
     * @param encryptStr 待解密字符 base64
     * @param isBase64 是否base64字符
     * @return 解密后字符
     * @throws Exception 解密报错
     */
    public static String decrypt(String encryptStr, boolean isBase64) throws Exception{
        byte[] encryptByte;
        if (isBase64) {
            encryptByte = Base64.getDecoder().decode(encryptStr);
        } else {
            encryptByte = encryptStr.getBytes();
        }

        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);

        byte[] decryptedByte = cipher.doFinal(encryptByte);

        return new String(decryptedByte).trim();
    }

    public static void main(String[] args) throws Exception {
        String str = "123123";
        System.out.println(str);
        String encrypted = encrypt(str);
        System.out.println(encrypted);
        final String decrypt = decrypt(encrypted);
        System.out.println(decrypt);
    }
}
