package BigData.Pulsar.producer;

import org.apache.pulsar.client.api.MessageRoutingMode;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

/**
 * @Author: LX
 * @Date: 2019/4/1 12:50
 * @Version: 1.0
 */

@SuppressWarnings("NonAsciiCharacters")
class 定制化消息和Producer {

    @Test
    void main() throws PulsarClientException {
        // 准备Pulsar的服务url地址
        String localClusterUrl = "pulsar://172.16.2.107:6650";
        // 创建Pulsar客户端
        PulsarClient client = PulsarClient.builder().serviceUrl(localClusterUrl).build();
        // 使用Pulsar客户端创建生产者 并进行配置
        Producer<byte[]> producer = client.newProducer().topic("persistent://lx/java/test")
                .batchingMaxPublishDelay(20, TimeUnit.MILLISECONDS)
                .sendTimeout(10, TimeUnit.SECONDS)
                .blockIfQueueFull(true)
                .messageRoutingMode(MessageRoutingMode.RoundRobinPartition)
                .create();
        // 对发送的消息进行配置
        producer.newMessage()
                .key("one")
                .value("Value %d".getBytes())
                .property("my-key", "my-value")
                .send();
        // 调用flush()方法将消息发送到Pulsar服务
        producer.flush();
        // 生产结束后关闭生产者和Pulsar client
         producer.close();
         client.close();
    }

}
