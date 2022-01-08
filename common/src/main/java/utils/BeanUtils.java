package utils;

import lombok.Data;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 *  
 * @description  Bean处理工具
 * @author LX    
 * @date   2020/3/25 9:58  
 *  
 */
public class BeanUtils {

    /**
     * 检查对象中指定的多个属性中是否有值
     * @param object 需要检查的对象，对象中必须有get方法
     * @param params 需要检查的属性数组
     * @return 检查结果
     */
    public static String checkParams(Object object, String[] params, Map<String, String> paramsMsgMap) {
        for (String param : params) {
            try {
                if (!checkOneParam(object, param)) {
                    String errMsg = paramsMsgMap.get(param);
                    if (errMsg == null) {
                        return param + "校验失败！";
                    } else {
                        return errMsg;
                    }
                }
            } catch (NoSuchMethodException e) {
                return "找不到指定属性：[" + param + "]的get方法:";
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
                return "执行反射方法出错！";
            }
        }
        return null;
    }

    /**
     * 检查object对象中paramName属性是否有数据
     *
     * @param object    待检查的对象
     * @param paramName 需要检查的属性
     * @return 检查结果
     * @throws NoSuchMethodException 反射找不到指定的方法
     * @throws IllegalAccessException 反射执行指定方法报错
     * @throws InvocationTargetException 反射执行方法报错
     */
    public static boolean checkOneParam(Object object, String paramName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object value = getParamValue(object, paramName);
        return !StringUtils.isEmpty(value);
        // System.out.println(value);
    }

    /**
     * 获取对象中指定属性的值
     * @param object 指定对象
     * @param paramName 指定属性
     * @return 对象中指定属性的值
     */
    private static Object getParamValue(Object object, String paramName) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        String getMethodName = "get" + paramName.substring(0, 1).toUpperCase() + paramName.substring(1);
        Method method = object.getClass().getMethod(getMethodName);
        return method.invoke(object);
    }

    @Data
    static class Pojo {
        String name;
        String value;
    }

    public static void main(String[] args) {
        Pojo pojo = new Pojo();
        pojo.setName("oopp");
        System.out.println(pojo);
        Map<String, String> paramsMsgMap = new HashMap<>();
        paramsMsgMap.put("name", "name不能为空！");
        paramsMsgMap.put("value", "value不能为空！");

        String[] params = "name_value".split("_,_");
        String errMsg = checkParams(pojo, params, paramsMsgMap);
        
        if (errMsg != null) {
            System.out.println("对象有属性检查为空，此处应该抛错");
        }
        pojo.setValue("qqq");
        errMsg = checkParams(pojo, params, paramsMsgMap);
        if (errMsg != null) {
            System.out.println("对象有属性检查为空，此处应该抛错！");
        } else {
            System.out.println("对象检查属性成功！");
        }
    }
}
