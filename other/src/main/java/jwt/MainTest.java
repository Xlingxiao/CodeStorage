package jwt;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import utils.FileUtil;


import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * @Author: LX
 * @Date: 2019/5/1 10:32
 * @Version: 1.0
 */
public class MainTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        // 我们需要一个签名密钥，因此我们将为本例创建一个。通常密钥将从您的应用程序配置中读取
        FileUtil fileUtil = new FileUtil();
        byte[] keyBytes = fileUtil.getFileContent("jwt/base64-secret.key");
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        /*SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        KeyPair keyPair = Keys.keyPairFor(signatureAlgorithm);*/
        Map<String, Object> params = new HashMap<>(8);
        params.put("A", "zzz");
        params.put("B", "yyy");
        params.put("C", "xxx");

        String jws = Jwts
                .builder()
                .setClaims(params)
                .setSubject("admin")
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key)
                .compact();

        System.out.println(jws);

        System.out.println("睡眠" + 1000 + "ms");
        Thread.sleep(1000);
        mainTestFunction(key,jws);
        System.out.println("睡眠" + 5000 + "ms");
        Thread.sleep(5000);
        mainTestFunction(key, jws);
//        MainTest mainTest = new MainTest();
//        SecretKey key = Keys.hmacShaKeyFor("Xq6SGWpwwEFbKlKeLcGvlaDzrlKGCaNVZVgIpHYpSz4=".getBytes());
//        String token = "eyJhbGciOiJIUzI1NiJ9.eyJsb2dpbl91c2VyIjp7InRyc3RyX2luc2lkIjoiMzQwMTA5MDAwMDAxIiwiZHd6aCI6bnVsbCwiZHdtYyI6bnVsbCwiZ3J6aCI6bnVsbCwiY3VzdG9tZXJpZExpc3QiOm51bGwsInhpbmdtaW5nIjpudWxsLCJncnpoenQiOm51bGwsImprcnhtIjpudWxsLCJzamhtIjpudWxsLCJ6amx4IjpudWxsLCJ6amhtIjpudWxsLCJjdXN0b21lcmlkIjoiMzQwMTA5MDAwMDAxQWhlaWZlaTEiLCJsdjFfYnJfbm8iOiIzNDAwMDAwMDAiLCJ0eG5fY2hubF9pZCI6IjMwMDEiLCJoc3JmbV9mbmNfYWNjX3RwY2QiOm51bGwsImhzcmZfYnNuX3RwY2QiOm51bGwsInBieXFmcyI6W10sImlzTW9kUHNkIjoiZmFsc2UiLCJwZXJzSW5mbyI6bnVsbCwiZHd6aEluZm8iOlt7ImhzcmZCc25UcENkIjoiMDEiLCJkd3poIjoiMDAwMDIyMTEwIiwiZHdtYyI6IuWQiOiCpea5lua7qOeJqeS4mueuoeeQhuaciemZkOWFrOWPuCIsImFwcnZBbnVsIjpudWxsLCJqem55IjpudWxsLCJkd2pjYmwiOm51bGwsImJyamNibCI6bnVsbCwiY29sbEFuZFB5UE51bSI6bnVsbCwiY29sbEFuZFB5QW10IjpudWxsLCJoc3JmbUZuY0hhbmdBY2NCYWwiOm51bGwsImhzcmZtRm5jVHJyRGVwQm5rQnJObyI6bnVsbCwidHNPcEFjSW5zTm8iOiIzNDAxMDAwMTAwIiwidW5pdFN0Q2QiOm51bGwsImlkUHlDc3RJRCI6bnVsbCwiY250ckVuZElkQ3N0SUQiOm51bGwsImNvcnBwYXBlcnR5cGUiOm51bGwsImNvbXBhbnlubyI6bnVsbH1dLCJob3VzZUluZm9MaXN0IjpudWxsLCJ6d3dzamhtIjpudWxsLCJ6d19zamhtIjpudWxsLCJ1c2VyQWNjIjpudWxsLCJ4bGgiOm51bGwsImRhbndlaU5hbWUiOm51bGwsImRhbndlaVR5cGVDb2RlIjpudWxsLCJoU1JGQnNuVHBDZCI6bnVsbCwiaFNSZm1GbmNBY2NUcENkIjpudWxsLCJ3eHpqRHdJbmZvTGlzdCI6bnVsbCwiY250ckVuZElkY3N0SWQiOm51bGx9LCJsb2dpbl9zdGF0ZSI6IlNVQ0NFU1MiLCJsb2dpbl9yb2xlIjoiMDIiLCJzdWIiOiJ1c2VyIiwiZXhwIjoxNTgzOTE2NDM5fQ.UMj_YrQV0mPqms3BTQZg8kXMyYfbJXQ_QkoJUmMb1hM";
//        System.out.println(mainTest.getBody(token,key));

    }

    private static void mainTestFunction(SecretKey key,String jws) {
        try {
            // 解析token
            System.out.println(Jwts.parser().setSigningKey(key).parseClaimsJws(jws));
            // 获取token中body的内容
            Map<String, Object> map = Jwts.parser().setSigningKey(key).parseClaimsJws(jws).getBody();
            System.out.println(map);
            String role = "qqq";
            map.put("sub", role);
            String pulsarToken = Jwts.builder().setSubject(role).setClaims(map).signWith(key).compact();
            System.out.println(pulsarToken);
            System.out.println(Jwts.parser().setSigningKey(key).parseClaimsJws(pulsarToken));
            //OK, we can trust this JWT
            System.out.println("验证成功");
        } catch (ExpiredJwtException jwtException) {
            System.out.println("Token 识别失败");
        }
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
        String admin = "admin";
        if (admin.equals(userName)) {
            return true;
        }
        String authorities = (String) map.get("authorities");
        return authorities.contains("ADMIN");
    }

    public String getTokenByUser(String user) {
        JwtBuilder builder = Jwts.builder().signWith(key).setSubject(user);
        return builder.compact();
    }

    public Map<String,Object> getBody(String jws,SecretKey key) {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(jws).getBody();
    }

    public Map<String, Object> getHead(String jws, SecretKey key) {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(jws).getHeader();
    }

    void TestMain() {
        SecretKey key = Keys.hmacShaKeyFor("Xq6SGWpwwEFbKlKeLcGvlaDzrlKGCaNVZVgIpHYpSz4=".getBytes());
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJsb2dpbl91c2VyIjp7InRyc3RyX2luc2lkIjoiMzQwMTA5MDAwMDAxIiwiZHd6aCI6bnVsbCwiZHdtYyI6bnVsbCwiZ3J6aCI6bnVsbCwiY3VzdG9tZXJpZExpc3QiOm51bGwsInhpbmdtaW5nIjpudWxsLCJncnpoenQiOm51bGwsImprcnhtIjpudWxsLCJzamhtIjpudWxsLCJ6amx4IjpudWxsLCJ6amhtIjpudWxsLCJjdXN0b21lcmlkIjoiMzQwMTA5MDAwMDAxQWhlaWZlaTEiLCJsdjFfYnJfbm8iOiIzNDAwMDAwMDAiLCJ0eG5fY2hubF9pZCI6IjMwMDEiLCJoc3JmbV9mbmNfYWNjX3RwY2QiOm51bGwsImhzcmZfYnNuX3RwY2QiOm51bGwsInBieXFmcyI6W10sImlzTW9kUHNkIjoiZmFsc2UiLCJwZXJzSW5mbyI6bnVsbCwiZHd6aEluZm8iOlt7ImhzcmZCc25UcENkIjoiMDEiLCJkd3poIjoiMDAwMDIyMTEwIiwiZHdtYyI6IuWQiOiCpea5lua7qOeJqeS4mueuoeeQhuaciemZkOWFrOWPuCIsImFwcnZBbnVsIjpudWxsLCJqem55IjpudWxsLCJkd2pjYmwiOm51bGwsImJyamNibCI6bnVsbCwiY29sbEFuZFB5UE51bSI6bnVsbCwiY29sbEFuZFB5QW10IjpudWxsLCJoc3JmbUZuY0hhbmdBY2NCYWwiOm51bGwsImhzcmZtRm5jVHJyRGVwQm5rQnJObyI6bnVsbCwidHNPcEFjSW5zTm8iOiIzNDAxMDAwMTAwIiwidW5pdFN0Q2QiOm51bGwsImlkUHlDc3RJRCI6bnVsbCwiY250ckVuZElkQ3N0SUQiOm51bGwsImNvcnBwYXBlcnR5cGUiOm51bGwsImNvbXBhbnlubyI6bnVsbH1dLCJob3VzZUluZm9MaXN0IjpudWxsLCJ6d3dzamhtIjpudWxsLCJ6d19zamhtIjpudWxsLCJ1c2VyQWNjIjpudWxsLCJ4bGgiOm51bGwsImRhbndlaU5hbWUiOm51bGwsImRhbndlaVR5cGVDb2RlIjpudWxsLCJoU1JGQnNuVHBDZCI6bnVsbCwiaFNSZm1GbmNBY2NUcENkIjpudWxsLCJ3eHpqRHdJbmZvTGlzdCI6bnVsbCwiY250ckVuZElkY3N0SWQiOm51bGx9LCJsb2dpbl9zdGF0ZSI6IlNVQ0NFU1MiLCJsb2dpbl9yb2xlIjoiMDIiLCJzdWIiOiJ1c2VyIiwiZXhwIjoxNTgzODIzMDU0fQ.XCaaYwX8NWJgKAHTbjKjyVwZCtM5Gl0XsyVEkfIot0E";
        System.out.println(getBody(token,key));
    }

}
