package Pulsar.consumer;

import org.apache.pulsar.client.api.*;
import org.apache.pulsar.client.internal.DefaultImplementation;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @Author: LX
 * @Date: 2019/4/1 13:56
 * @Version: 1.0
 */
@SuppressWarnings({"Duplicates", "NonAsciiCharacters"})
class 定制化读取消息 {
    /*这种方式不能对消息进行ACK，只能新建一个consumer专门用来进行ACK*/
    @Test
    void consumer() throws IOException {
        // 准备Pulsar的服务url地址
        String Url = "pulsar://172.16.2.107:6650";
        // 创建Pulsar客户端
        PulsarClient client = PulsarClient.builder().serviceUrl(Url).build();
        // 每个subscriptionName相当于一个消费分组，一个分组只能对一条数据消费一次
        Consumer<byte[]> consumer = client.newConsumer()
                .topic("my-topic")
                .subscriptionName("subscriptionName").subscribe();
        //从指定的message开始消费
        MessageId id = DefaultImplementation.newMessageId(1, 1, 1);
        //从第一条message开始读取
        // MessageId id = MessageId.earliest;
        //从最新的message开始读取
        // MessageId id = MessageId.latest;
        Reader reader = client.newReader().topic("my-topic").startMessageId(id).create();
        while (true) {
            // 获得消息
            Message msg = reader.readNext();
            //消费消息
            System.out.println(new String(msg.getData()));
            //发送commit到pulsar服务
            consumer.acknowledge(msg);
        }
    }
}
