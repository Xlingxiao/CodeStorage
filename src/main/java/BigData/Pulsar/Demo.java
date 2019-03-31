package BigData.Pulsar;

import org.apache.pulsar.client.api.*;
import org.apache.pulsar.client.impl.conf.ReaderConfigurationData;
import org.apache.pulsar.client.impl.schema.JSONSchema;
import org.apache.pulsar.client.internal.DefaultImplementation;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @Author: LX
 * @Date: 2019/3/25 13:02
 * @Version: 1.0
 */
@SuppressWarnings("WeakerAccess")
public class Demo {
    @Test
    void main() throws PulsarClientException {
        // 准备Pulsar的服务url地址
        String localClusterUrl = "pulsar://172.16.2.107:6650";
        // 创建Pulsar客户端
        PulsarClient client = PulsarClient.builder().serviceUrl(localClusterUrl).build();
        // 使用Pulsar客户端创建生产者
        // Producer<byte[]> producer = client.newProducer().topic("my-topic").create();
        /*Producer<byte[]> producer = client.newProducer().topic("my-topic")
                .batchingMaxPublishDelay(20, TimeUnit.MILLISECONDS)
                .sendTimeout(10, TimeUnit.SECONDS)
                .blockIfQueueFull(true)
                .create();*/
         Producer<String> producer = client.newProducer(Schema.STRING).topic("my-topic").create();
        // 调用send()方法发送消息
        producer.send("Hello Java Pulsar");
        producer.send("Hello Java Client");
        // 异步发送消息
        producer.sendAsync("HelloAsync").thenAccept(
                messageId -> System.out.println("Message send success"));
        // 对发送的消息进行配置
        producer.newMessage()
                .key("one")
                .value("Key-Value")
                .property("my-key", "my-value")
                .send();
        // 调用flush()方法将消息发送到Pulsar服务
        producer.flush();
        // 生产结束后关闭生产者
        // producer.close();
        producer.closeAsync().thenRun(() -> System.out.println("Producer closed"));
        // 发送pojo对象
        Producer<Pojo> producer2 = client.newProducer(JSONSchema.of(Pojo.class))
                .topic("pojo-topic")
                .create();
        producer2.send(new Pojo("1", 15, "lx"));
        producer2.close();
        // 关闭Pulsar客户端
        producer.close();
    }


    @Test
    void consumer() throws IOException {
        // 准备Pulsar的服务url地址
        String Url = "pulsar://172.16.2.107:6650";
        // 创建Pulsar客户端
        PulsarClient client = PulsarClient.builder().serviceUrl(Url).build();
        // 使用Pulsar客户端创建生产者
        Consumer<byte[]> consumer = client.newConsumer().topic("my-topic")
                .subscriptionName("my-subscription").subscribe();
        //从指定的message开始消费
        MessageId id = DefaultImplementation.newMessageId(2, 2, 2);
        //从第一条message开始消费
        // MessageId id = MessageId.earliest;
        //从最新的message开始消费
        // MessageId id = MessageId.latest;
        Reader reader = client.newReader()
                .topic("my-topic")
                .startMessageId(id)
                .create();
        while (true) {
            // 获得消息
            // Message msg = consumer.receive();
            Message msg = reader.readNext();
            //消费消息
            System.out.println(new String(msg.getData()));
            //发送commit到pulsar服务
            consumer.acknowledge(msg);
        }
    }

    @Test
    void pojoConsumer() throws PulsarClientException {
        String url = "pulsar://172.16.2.107:6650";
        PulsarClient client = PulsarClient.builder().serviceUrl(url).build();
        Schema<Pojo> pojoSchema = JSONSchema.of(Pojo.class);
        Consumer<Pojo> consumer = client.newConsumer(pojoSchema)
                .topic("pojo-topic")
                .subscriptionName("consumer")
                .subscribe();
        while (true) {
            Message message = consumer.receive();
            byte[] data = message.getData();
            Pojo pojo = pojoSchema.decode(data);
            System.out.println(pojo);
            consumer.acknowledge(message);
        }
    }
}
