package BigData.Pulsar.consumer;

import org.apache.pulsar.client.api.*;
import org.junit.jupiter.api.Test;


/**
 * @Author: LX
 * @Date: 2019/4/1 14:54
 * @Version: 1.0
 */
public class 指定分区消费 {

    @Test
    void main() throws PulsarClientException {
        String url = "pulsar://172.16.2.107:6650";
        PulsarClient client = PulsarClient.builder().serviceUrl(url).build();
        Consumer<String> consumer = client.newConsumer(Schema.STRING)
                .topic("persistent://lx/java/test-partition-3")
                .subscriptionName("test").subscribe();
        while (true){
            Message msg = consumer.receive();
            System.out.println(new String(msg.getData()));
            consumer.acknowledge(msg);
        }

    }

    @Test
    void test() throws PulsarClientException {
        String url = "pulsar://172.16.2.107:6650";
        PulsarClient client = PulsarClient.builder().serviceUrl(url).build();
        Consumer<String> consumer = client.newConsumer(Schema.STRING)
                .topic("persistent://lx/java/test-partition-2")
                .subscriptionName("test").subscribe();
        while (true){
            Message msg = consumer.receive();
            System.out.println(new String(msg.getData()));
            consumer.acknowledge(msg);
        }
    }
}
