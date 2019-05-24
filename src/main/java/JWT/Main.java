package JWT;


import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import utils.FileUtil;


import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * @Author: LX
 * @Date: 2019/5/1 10:32
 * @Version: 1.0
 */
public class Main {
    public Main() throws IOException {
    }

    public static void main(String[] args) throws IOException {
        // 我们需要一个签名密钥，因此我们将为本例创建一个。通常密钥将从您的应用程序配置中读取
        FileUtil fileUtil = new FileUtil();
        byte[] keyBytes = fileUtil.getFileContent("jwt/base64-secret.key");
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        /*SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        KeyPair keyPair = Keys.keyPairFor(signatureAlgorithm);*/

        String jws = Jwts.builder().setSubject("admin").signWith(key).compact();
        String yuit = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0MyJ9.wGFd0RslYqT4IKAFAq-1FXcFlSM0VEvK109GpZBXzOY";
        System.out.println(jws);
        System.out.println(yuit);

            Jwts.parser().setSigningKey(key).parseClaimsJws(jws);
            System.out.println(Jwts.parser().setSigningKey(key).parseClaimsJws(jws));
            System.out.println(Jwts.parser().setSigningKey(key).parseClaimsJws(yuit));
            Map<String, Object> map = Jwts.parser().setSigningKey(key).parseClaimsJws(yuit).getBody();
            System.out.println(map);
            System.out.println(map.get("user_name"));
            String role = (String) map.get("user_name");
            map.put("sub", role);
            String pulsarToken = Jwts.builder().setSubject(role).setClaims(map).signWith(key).compact();
            System.out.println(pulsarToken);
            System.out.println(Jwts.parser().setSigningKey(key).parseClaimsJws(pulsarToken));
            //OK, we can trust this JWT
            System.out.println("验证成功");
    }

    private byte[] keyBytes = "3j/UKPW5S/9088B9DaJJ1cVINRCVqqQuE7rH/q9JuDQ=".getBytes();
    private SecretKey key = Keys.hmacShaKeyFor(keyBytes);

    /*根据token获取用户名*/
    public String decodeUser(String jws) {
        Map<String, Object> map = Jwts.parser().setSigningKey(key).parseClaimsJws(jws).getBody();
        System.out.println(map);
        return (String) map.get("user_name");
    }

    public boolean isAdmin(String jws) {
        Map<String, Object> map = Jwts.parser().setSigningKey(key).parseClaimsJws(jws).getBody();
        String userName = (String) map.get("user_name");
        if (userName.equals("admin")) return true;
        String authorities = (String) map.get("authorities");
        return authorities.contains("ADMIN");
    }

    public String getTokenByUser(String user) {
        JwtBuilder builder = Jwts.builder().signWith(key).setSubject(user);
        return builder.compact();
    }

    @Test
    void Main() {
        String user = "admin";
        String token = getTokenByUser(user);
        System.out.println(token);


    }

}
