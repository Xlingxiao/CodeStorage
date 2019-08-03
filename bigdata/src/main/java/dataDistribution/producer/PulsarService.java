package dataDistribution.producer;



import dataDistribution.mybatis.mapper.MessageStatusMapper;
import dataDistribution.mybatis.pojo.MessageStatus;
import org.apache.pulsar.client.api.Producer;
import utils.PulsarUtil;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @Author: LX
 * @Date: 2019/4/23 10:47
 * @Version: 1.0
 *
 * 生产者主要服务区
 */

public class PulsarService implements Runnable {

    private Producer<byte[]> producer;

    private MessageStatusMapper msgMapper;

    PulsarService(String topic, Properties properties, MessageStatusMapper msgMapper) throws IOException {
        PulsarUtil util = new PulsarUtil(properties);
        producer = util.getProducer(topic);
        this.msgMapper = msgMapper;
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        String topic = producer.getTopic();
        System.out.println(String.format("%s已经启动处理数据：%s", name, topic));
        //每次读取数据库中的多少数据
        int size = 1000;
        while (true) {
            try {
                List<MessageStatus> myMessages = msgMapper.getByTopicAndStatus(topic, "配置下发", size);
                for (MessageStatus myMessage : myMessages) {
                    System.out.println(String.format("%s处理了：%s", name, myMessage.getContent()));
                    myMessage.setStatus("配置传输");
                    myMessage.setModifyTime(new Date(System.currentTimeMillis()));
                    //发送到pulsar
                    sendMessage(String.valueOf(myMessage.getId()), myMessage.getContent());
                    msgMapper.updateByPrimaryKey(myMessage);
                }
                //每两秒读取一次数据库
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private void sendMessage(String key, String msg) {
        producer.newMessage().key(key).value(msg.getBytes()).sendAsync();
        System.out.println("Pulsar发送成功！");
    }
}
