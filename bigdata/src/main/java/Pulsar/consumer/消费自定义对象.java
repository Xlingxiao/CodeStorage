package Pulsar.consumer;

import Pulsar.Pojo;
import org.apache.pulsar.client.api.*;
import org.apache.pulsar.client.impl.schema.JSONSchema;
import org.junit.jupiter.api.Test;

/**
 * @Author: LX
 * @Date: 2019/4/1 13:54
 * @Version: 1.0
 */
@SuppressWarnings({"NonAsciiCharacters", "Duplicates", "InfiniteLoopStatement"})
class 消费自定义对象 {
    @Test
    void main() throws PulsarClientException {
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
