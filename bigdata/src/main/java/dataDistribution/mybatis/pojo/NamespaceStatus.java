package dataDistribution.mybatis.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author: LX
 * @Date: 2019/5/5 18:52
 * @Version: 1.0
 */
@Getter
@Setter
public class NamespaceStatus {
    String name;
    String tenant;
    long retentionTimeInMinutes; //minutes
    long retentionSizeInMB; // MB
    long usedSpaceMB;
    int topics;
}
