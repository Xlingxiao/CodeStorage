package dataDistribution.mybatis.mapper;


import dataDistribution.mybatis.pojo.MessageStatus;

import java.util.List;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface MessageStatusMapper {

    /*根据用户名获得用户角色*/
    List<String> selectRoleByUser(String userName);

    List<MessageStatus> selectAll();

    List<MessageStatus> selectOnePage(int startIndex, int size);

    List<MessageStatus> selectOnePageByUser(int startIndex, int size, String userName);

    List<MessageStatus> getByStatus(String status, int size);

    List<MessageStatus> getByTopicAndStatus(String topic, String status, int size);

    List<MessageStatus> getMsgsByIdRange(long startPosition, long endPosition, int size);

    List<MessageStatus> getMsgByTopic(String topic, int size);

    MessageStatus selectByPrimaryKey(Long id);

    int deleteByPrimaryKey(Long id);

    int insert(MessageStatus record);

    int insertSelective(MessageStatus record);

    int updateByPrimaryKeySelective(MessageStatus record);

    int updateByPrimaryKey(MessageStatus record);

    long getMessageTotal();

    long getMessageTotalByUser(String userName);
}