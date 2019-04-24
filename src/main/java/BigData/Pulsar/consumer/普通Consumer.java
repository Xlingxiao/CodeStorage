package BigData.Pulsar.consumer;

import org.apache.pulsar.client.api.*;
import org.apache.pulsar.client.internal.DefaultImplementation;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @Author: LX
 * @Date: 2019/4/1 13:49
 * @Version: 1.0
 */
@SuppressWarnings("ALL")
class 普通Consumer {
    @Test
    void main() throws IOException {
        // 准备Pulsar的服务url地址
        String Url = "pulsar://172.16.2.107:6650";
        // 创建Pulsar客户端
        PulsarClient client = PulsarClient.builder().serviceUrl(Url).build();
        // 使用Pulsar客户端创建生产者
        // 每个subscriptionName相当于一个消费分组，一个分组只能对一条数据消费一次
        Consumer<byte[]> consumer = client.newConsumer().topic("persistent://lx/java/log")
                .subscriptionName("subscriptionName").subscribe();
        System.out.println(consumer.getTopic());
        while (true) {
            // 获得消息
             Message msg = consumer.receive();
            //消费消息
            System.out.println(String.format("%s\t%s",msg.getKey(),new String(msg.getData())));
            //发送commit到pulsar服务
            consumer.acknowledge(msg);
        }
    }
}
