package JWT;


import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import utils.FileUtil;


import javax.crypto.SecretKey;
import java.io.IOException;


/**
 * @Author: LX
 * @Date: 2019/5/1 10:32
 * @Version: 1.0
 */
public class Main {
    public static void main(String[] args) throws IOException {
        // 我们需要一个签名密钥，因此我们将为本例创建一个。通常密钥将从您的应用程序配置中读取
        FileUtil fileUtil = new FileUtil();
        byte[] keyBytes = fileUtil.getFileContent("jwt/base64-secret.key");
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        /*SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        KeyPair keyPair = Keys.keyPairFor(signatureAlgorithm);*/

        String jws = Jwts.builder().setSubject("zx").signWith(key).compact();
        System.out.println(jws);
        assert Jwts.parser().setSigningKey(key).parseClaimsJws(jws).getBody().getSubject().equals("zx");
        try {

            Jwts.parser().setSigningKey(key).parseClaimsJws(jws);
            System.out.println(Jwts.parser().setSigningKey(key).parseClaimsJws(jws));

            //OK, we can trust this JWT
            System.out.println("验证成功");

        } catch (JwtException e) {

            //don't trust the JWT!
            System.out.println("验证失败");
        }
    }



}
