package dataDistribution.mybatis.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @Author: LX
 * @Date: 2019/5/5 18:52
 * @Version: 1.0
 */
@Getter
@Setter
public class TopicStatus {
    String name;
    Map<String,Long> subCount;
    long messageCount;
    long totalSize;
    int partitions;
}
