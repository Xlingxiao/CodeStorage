package Pulsar.admin;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * @Author: LX
 * @Date: 2019/4/30 10:44
 * @Version: 1.0
 */
@Getter
@Setter
public class TopicNode {

    public TopicNode(String title, String value, String key) {
        this.title = title;
        this.value = value;
        this.key = key;
    }

    String title;
    String value;
    String key;
    Map<String,?> admin;
    List<TopicNode> children;
    boolean isLeaf;

    /*@Override
    public String toString() {
        *//*String str = '{' +
                "\"title\" : \"" + title + "\"," +
                "\"value\" : \"" + value + "\"," +
                "\"key\" : \"" + key + "\",";
        if (admin != null) {
            str = str + "\"admin\" : " + new JSONObject(admin).toString() + ",";
//            System.out.println(new JSONObject(admin).toString());
        }
        if (isLeaf) {
            str = str + "\"isLeaf\" : \"" + true + "\"}";
        } else if (children != null) {
            str = str + "\"children\" : " + children + '}';
        } else {
            str = str + '}';
        }
        return str;*//*
        return new JSONObject(this).toString();
    }*/
}
