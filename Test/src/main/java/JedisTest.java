import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LX    
 * @description  Jedis操作测试
 * @date 2020/4/7 15:45  
 */
public class JedisTest {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("self.ali", 6379);
        jedis.auth("lpszx123");
        String a = jedis.get("a");
        System.out.println(a);
        System.out.println("测试连接成功！");
        JedisTest jedisTest = new JedisTest();
        boolean flag = jedisTest.needToCheckFaceByRedis(jedis, "340109000001", "010080000001");
        if (flag) {
            System.out.println("需要进行验证！");
        }
    }

    /**
     * 将map存入redis中的faceToStep
     * @param jedis jedis对象
     * @param map 需要进行人脸识别的交易格式为 trstrInsID：field
     */
    private void refreshFaceToStep(Jedis jedis, Map<String, String> map) {
        // Map<String, String> kv = new HashMap<>(50);
        map.forEach((trstrInsID, field) -> {
            String key = "faceToStepID:" + trstrInsID;
            jedis.hset(key, field, "1");
        });
    }

    /**
     *  根据委托人机构号和服务步骤号获取redis中是否有值
     * @param jedis jedis
     * @param trstrInsID 委托人机构号
     * @param stepID 服务步骤号
     * @return 服务步骤号是否需要进行人脸验证
     */
    public boolean needToCheckFaceByRedis(Jedis jedis, String trstrInsID, String stepID) {
        String key = "faceToStepID:" + trstrInsID;
        String result = jedis.hget(key, stepID);
        System.out.println("Key: " + key);
        System.out.println("Field: " + stepID);
        if ("1".equals(result)) {
            return true;
        }
        // 没有值 从数据库进行获取
        return needToCheckFaceByDataBase(trstrInsID, stepID);
    }

    /**
     *  从数据库获取服务步骤号
     * @param trstrInsID 委托人机构号
     * @param stepID 服务步骤号
     * @return 服务步骤号是否需要进行人脸验证
     */
    private boolean needToCheckFaceByDataBase(String trstrInsID, String stepID) {
        // TODO
        return true;
    }

}
