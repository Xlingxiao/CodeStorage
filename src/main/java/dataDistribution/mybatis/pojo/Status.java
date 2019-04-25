package dataDistribution.mybatis.pojo;

import lombok.Getter;

/**
 * @Author: LX
 * @Date: 2019/4/23 13:20
 * @Version: 1.0
 *
 * 对象用于根据状态查询数据库中的msg
 */

@Getter
public class Status {
    private String status;
    private int size;

    public Status(String status, int size) {
        this.status = status;
        this.size = size;
    }
}
