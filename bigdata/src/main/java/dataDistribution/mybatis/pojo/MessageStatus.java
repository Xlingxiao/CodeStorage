package dataDistribution.mybatis.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @Author: LX
 * @Date: 2019/4/23 13:05
 * @Version: 1.0
 */

@Getter
@Setter
public class MessageStatus {
    private Long id;

    private String content;

    private String status;

    private String topic;

    private String userName;

    private Date createTime;

    private Date modifyTime;


}

