import java.util.HashMap;
import java.util.Map;

/**
 * @author LX
 * @description
 * @date 2020/7/17 15:35
 */
public class GetParams {
    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        map.put("String", "ssssw");
        System.out.println(map);
        getParams(map);
        System.out.println(map);

    }

    static HashMap<String, String> getParams(Map map) {
        map.put("qweqwe", "ddd");
        return new HashMap<String, String>();
    }

}
