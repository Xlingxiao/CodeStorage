package dataDistribution.mybatis.pojo;

import lombok.Getter;
import lombok.Setter;

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

    String title;
    String value;
    String key;
    Map<String,?> admin;
    List<TopicNode> children;
    boolean isLeaf;

    public TopicNode(String title, String value, String key) {
        this.title = title;
        this.value = value;
        this.key = key;
    }
}
