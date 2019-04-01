package BigData.Pulsar.consumer;

import org.apache.pulsar.client.api.*;
import org.apache.pulsar.client.internal.DefaultImplementation;
import org.apache.pulsar.shade.org.asynchttpclient.Param;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Author: LX
 * @Date: 2019/4/1 14:25
 * @Version: 1.0
 */
@SuppressWarnings({"unchecked", "NonAsciiCharacters"})
class 定制化消费 {

    @Test
    void main() throws IOException, InterruptedException {
        // 准备Pulsar的服务url地址
        String Url = "pulsar://172.16.2.107:6650";
        // 创建Pulsar客户端
        PulsarClient client = PulsarClient.builder().serviceUrl(Url).build();
        // 使用Pulsar客户端创建生产者
        // 配置messageListener之后每当有新的消息就会调用到messageListener中的received()方法
        client.newConsumer()
                .topic("my-topic")
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(new MyListener())
                .subscriptionName("subscriptionName").subscribe();
        // 配置让主线程睡一会，否则主线程运行结束后就看不到接收效果了
        TimeUnit.SECONDS.sleep(20);
    }

    class MyListener implements MessageListener{

        @Override
        public void received(Consumer consumer, Message message) {
            System.out.println(new String(message.getData()));
        }
    }
}
