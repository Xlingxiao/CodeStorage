package dataDistribution.mybatis.mapper;

import org.apache.ibatis.annotations.Param;
import dataDistribution.mybatis.pojo.MyMessage;

import java.util.List;


/**
 * @Author: LX
 * @Date: 2019/4/25 14:08
 * @Version: 1.0
 */
public interface MessageStatusMapper {
    List<MyMessage> selectAll();

    List<MyMessage> selectOnePage(@Param("startIndex") int startIndex, @Param("endIndex") int endIndex);

    List<MyMessage> getByStatus(@Param("status") String status, @Param("size") int size);

    List<MyMessage> getMsgsByIdRange(@Param("startPosition") long startPosition, @Param("endPosition") long endPosition, @Param("size") int size);

    List<MyMessage> getMsgByTopic(@Param("topic") String topic, @Param("size") int size);

    List<MyMessage> getMsgByTopicAndStatus(@Param("topic") String topic, @Param("status") String status, @Param("size") int size);

    MyMessage selectByPrimaryKey(Long id);

    int deleteByPrimaryKey(Long id);

    int insert(MyMessage record);

    int insertSelective(MyMessage record);

    int updateByPrimaryKeySelective(MyMessage record);

    void updateByPrimaryKey(MyMessage record);
}
