package dataDistribution;

import dataDistribution.mybatis.mapper.MessageStatusMapper;
import dataDistribution.mybatis.pojo.MyMessage;
import factory.MapperFactory;

import java.io.IOException;
import java.util.List;

/**
 * @Author: LX
 * @Date: 2019/4/25 13:18
 * @Version: 1.0
 */
public class Test {
    public static void main(String[] args) throws IOException {
        MapperFactory factory = new MapperFactory();
        //获得mapper对象
        MessageStatusMapper messageMapper = factory.getMessageMapper(MessageStatusMapper.class);
        List<MyMessage> myMessages = messageMapper.getByStatus("配置生效", 10);
        for (MyMessage myMessage : myMessages) {
            System.out.println(myMessage.getContent());
        }
        factory.closeSession();
    }
}
