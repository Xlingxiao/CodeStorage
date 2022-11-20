package utils;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * RSA加密工具
 */
public class RSAUtils {
    private static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDmorb0FsMUdJwnKABOiKeSuEeU2Ulq0jXWPlGz2VhitO4pXqk61E92rbOnPnkt+niEs/KaReGngJlPHLtF4U2lRLIoqPOjC8Wlc8ogES3zNtpjYhanWUDSVmnuFumB6R7mZxJ1xVQYo0SULTlfWOcf+SrCwjPFdK3CvSwue8tPIwIDAQAB";
    private static String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOaitvQWwxR0nCcoAE6Ip5K4R5TZSWrSNdY+UbPZWGK07ileqTrUT3ats6c+eS36eISz8ppF4aeAmU8cu0XhTaVEsiio86MLxaVzyiARLfM22mNiFqdZQNJWae4W6YHpHuZnEnXFVBijRJQtOV9Y5x/5KsLCM8V0rcK9LC57y08jAgMBAAECgYBoWpU3ShlIZx7EZMgGwLQ5oaHZypr6d1SetKHZI21ndAASvd8KXEf8JcoU0SKZWCwxtw39GcNpO7miXxipHLE5SyFBbGB9TR1LkU9hMJqh5B9t5yb01eMPz5yqd6Wh8ls1SmZY0TXzPT09MU/lOQ/hOYq7EuLHCbre1Nt0DfE1SQJBAPta6ZPDsCiB5i9aerC6vMgJO3kVxwnrXeXfk3b+qj05CXL6/ghkVqj7mTDukmseK3qhs3wB7TTdaT+qJy1NLI0CQQDq5cjEn4SuC2R1TOkNb1CVvcYvof0p0s+2Z50FFIkgdj9ke08HGQDTXwRof/WFwQ/U+ttox9I4C3b+ArCS/HZvAkARk8b3zVx5SLQ3JYWk7DodZv/gtNQpbmsvkxX14VM4Ws+9Wa4AGjwxLv8Ee/qDQSXbOomZp2zWIJGmoRiCQ35VAkEApOmcUYbYxMRUzBGqEPeUsPxiNrDYp1NM5vfJgnaPa035ZZc4vXftX3h3BNLJUhVgAxv3jJG1YgmSifTjeN5sWwJBAKE7SQhZQ0EBhbL4W7wEcU9/r+XGDxMhQQmaQLKu49PYIliCW6bq3wX03Xal7PgUq5W9jZhDrcF93Auhp+anbgo=";

    private static String KEY_ALGORITHM = "RSA";

    private static final String PUBLIC_KEY_NAME = "publicKey";
    private static final String PRIVATE_KEY_NAME = "privateKey";

    /**
     * 生成密钥
     *
     * @return 密钥公钥 私钥
     * @throws Exception 生成失败
     */
    public static Map<String, Object> genKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);//2048
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        keyMap.put(PUBLIC_KEY_NAME, publicKey);
        keyMap.put(PRIVATE_KEY_NAME, privateKey);
        return keyMap;

    }


    /**
     * @param keyMap  密钥串
     * @param keyName 需要解密的密钥
     * @return 解码密钥为base64格式
     * @throws Exception 报错
     */
    public static String getKey(Map<String, Object> keyMap, String keyName) throws Exception {
        Key key = (Key) keyMap.get(keyName);
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }


    /**
     * 公钥加密普通字符串
     * <p>
     * 1. 原始字符传进来需要先转为Base64格式再转为byte数组进行加密
     * 2. 我们看到的公钥和私钥也是经过Base64编码的，使用前需要进行Base64解码为byte数组
     * 3. 加密后得到的byte数组，可以编码为Base64以供我们查看
     *
     * @param words 普通字符
     * @return 加密后转base64的字符
     * @throws Exception 报错
     */
    public static String encrypt(String words) throws Exception {
        // byte[] wordBytes = Base64.getEncoder().encode(words.getBytes());
         byte[] wordBytes = words.getBytes();

        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(RSAUtils.publicKey.getBytes()));
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey pKey = keyFactory.generatePublic(x509EncodedKeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());

        cipher.init(Cipher.ENCRYPT_MODE, pKey);

        final byte[] encryptBytes = cipher.doFinal(wordBytes);

        final String encodeBase64 = Base64.getEncoder().encodeToString(encryptBytes);

        return encodeBase64;
    }


    /**
     * 私钥解密
     * 1. 传进来的字符是base64编码的加密字符串
     * 2. 我们看到的公钥和私钥也是经过Base64编码的，使用前需要进行Base64解码为byte数组
     * 3. 解密前需要将字符进行Base64解码
     * 4. 解密后的byte数组需要进行Base64解码才能获取原文
     *
     * @param words 加密后转base64的字符
     * @return 正常字符
     * @throws Exception 报错
     */
    public static String decrypt(String words) throws Exception {

        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(RSAUtils.privateKey.getBytes()));

        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

        // 生成私钥
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());

        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        final byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(words.getBytes()));
        // return new String(Base64.getDecoder().decode(bytes));
        return new String(bytes);
    }


    public static void main(String[] args) throws Exception {
        String row = "123123";
        System.out.println("原文:" + row);
        final String encrypt = encrypt(row);
        System.out.println("rsa加密：" + encrypt);
        final String decrypt = decrypt("H1ww8gj0GQMwynk9dEPWnyrXJvtDk211HISAU+TzdWwk5y4SdVDohSOtefpBwkREJpj18ZhD5x95kRR9N5+6WX9/K4U5w4mf6eGY/Vwj3FM1c0olLEWSHJyoCQrV8uzOK4MOjlcryDLYTThvaR9v/xSzWab5h7b/WhMtirLhzLA=");
        System.out.println("rsa解密：" + new String(decrypt.getBytes()));


    }

}
