import lombok.Data;
import org.junit.jupiter.api.Test;
import utils.BeanUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author LX    
 * @description 参数相关测试
 * @date 2020/3/25 12:08  
 */
public class ParamsTest {

    @Test
    void testCheckParams() throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Pojo pojo = new Pojo();
        pojo.setName("oopp");
        System.out.println(pojo);
        Properties pro = new Properties();
        InputStream in = this.getClass().getResourceAsStream("params/paramsErrMsgMap.properties");
        pro.load(in);
        in.close();
        Map<String, String> paramsMsgMap = new HashMap<String,String>((Map)pro);
        String[] params = "name_,_value".split("_,_");
        String errMsg = BeanUtils.checkParams(pojo, params, paramsMsgMap);
        if (errMsg != null) {
            System.out.println("对象有属性检查为空，此处应该抛错");
        }
        pojo.setValue("qqq");
        errMsg = BeanUtils.checkParams(pojo, params, paramsMsgMap);
        if (errMsg != null) {
            System.out.println("对象有属性检查为空，此处应该抛错！");
        } else {
            System.out.println("对象检查属性成功！");
        }
    }

    @Data
    public static class Pojo {
        String name;
        String value;
    }
}
