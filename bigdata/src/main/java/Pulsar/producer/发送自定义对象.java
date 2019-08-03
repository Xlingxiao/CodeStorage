package Pulsar.producer;

import Pulsar.Pojo;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.impl.schema.JSONSchema;

/**
 * @Author: LX
 * @Date: 2019/4/1 11:42
 * @Version: 1.0
 */
@SuppressWarnings("NonAsciiCharacters")
public class 发送自定义对象 {
    public static void main(String[] args) throws PulsarClientException {
        // 准备Pulsar的服务url地址
        String localClusterUrl = "pulsar://172.16.2.107:6650";
        // 创建Pulsar客户端
        PulsarClient client = PulsarClient.builder().serviceUrl(localClusterUrl).build();

        // 使用Pulsar客户端创建发送String对象的生产者
         Producer<String> producer = client.newProducer(Schema.STRING).topic("my-topic").create();
        // 调用send()方法发送String对象
        producer.send("Hello Java Pulsar");
        // 调用flush()方法将消息发送到Pulsar服务
        producer.flush();
        // 生产结束后关闭生产者
         producer.close();

        // 创建发送pojo对象的producer
        Producer<Pojo> producer2 = client.newProducer(JSONSchema.of(Pojo.class))
                .topic("pojo-topic")
                .create();
        //发送pojo对象
        producer2.send(new Pojo("1", 15, "lx"));
        producer2.close();

        // 关闭Pulsar客户端
        client.close();
    }
}
