import com.alibaba.fastjson.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 *  
 *
 * @author LX    
 * @description     
 * @date 2020/4/16 15:14  
 */
public class JustTest {
    public static void main(String[] args) throws ParseException {
        testString("ZJHM");

        String uuid = UUID.randomUUID().toString();
        System.out.println(uuid);
        uuid = uuid.replace("-", "");
        System.out.println(uuid);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateStr = "20200105132203";
        Date date = dateFormat.parse(dateStr);
        System.out.println(date);


        JSONObject object1 = JSONObject.parseObject("");
        JSONObject object2 = object1.getJSONObject("111");
        String a = object2.getString("ttt");
        System.out.println(a);
    }

    public static void testString(String paramName) {
        String str = paramName.substring(0, 1).toLowerCase() + paramName.substring(1);
        System.out.println(str);
        str = str.toLowerCase();
        System.out.println(str);
        str = str.toUpperCase();
        System.out.println(str);
    }
}
