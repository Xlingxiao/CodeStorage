package BigData.Pulsar.producer;

import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

/**
 * @Author: LX
 * @Date: 2019/4/1 11:34
 * @Version: 1.0
 */
@SuppressWarnings("NonAsciiCharacters")
public class 普通Producer {
    public static void main(String[] args) throws PulsarClientException {
        // 准备Pulsar的服务url地址
        String localClusterUrl = "pulsar://172.16.2.107:6650";
        // 创建Pulsar客户端
        PulsarClient client = PulsarClient.builder().serviceUrl(localClusterUrl).build();
        // 使用Pulsar客户端创建生产者
         Producer<byte[]> producer = client.newProducer().topic("persistent://lx/java/test").create();
        // 调用send()方法发送消息 同步
        producer.send("Hello Java Client".getBytes());
        // 异步
        producer.sendAsync("Hello Java Client Async".getBytes())
                .thenAccept(messageId -> System.out.println("message send success"));
        // 调用flush()方法将消息发送到Pulsar服务
        producer.flush();
        // 同步关闭producer和client
        producer.close();
        client.close();
        // 异步关闭producer和client
        /*producer.closeAsync().thenRun(() -> System.out.println("Producer closed"));
        client.closeAsync().thenRun(() -> System.out.println("Client closed"));*/
    }
}
