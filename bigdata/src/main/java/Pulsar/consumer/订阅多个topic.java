package Pulsar.consumer;

import Pulsar.admin.LXAdmin;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.ConsumerBuilder;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.junit.jupiter.api.Test;
import utils.PropertiesUtil;
import utils.PulsarUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @Author: LX
 * @Date: 2019/4/23 21:41
 * @Version: 1.0
 */
@SuppressWarnings("Duplicates")
class 订阅多个topic {

    private PropertiesUtil propertiesUtil = new PropertiesUtil();

    @Test
    void main() throws IOException {
        /*读取pulsar的配置文件*/
        PulsarUtil util = new PulsarUtil(propertiesUtil);
        LXAdmin admin = new LXAdmin(propertiesUtil);
        Properties properties = propertiesUtil.getProperties("pulsar/pulsar.properties");
        /*获得topic 消费者组id 消费者组名*/
        String subName = properties.getProperty("subName");
        PulsarClient pulsarClient = util.getClient();
        ConsumerBuilder consumerBuilder = pulsarClient.newConsumer().subscriptionName(subName);

        /*通过正则表达式订阅topic*/
        /*Pattern allTopicsInNamespace = Pattern.compile("persistent://lx/java/.*");
        Consumer allTopicsConsumer = consumerBuilder.topicsPattern(allTopicsInNamespace).subscribe();*/

        /*通过列表订阅*/
        List<String> topics = Arrays.asList("topic-1", "topic-2", "topic-3");

        Consumer allTopicsConsumer = consumerBuilder.topics(topics).subscribe();

        String name = Thread.currentThread().getName() + " ";
        int i = 0;
        for (; ; ) {
            Message msg = allTopicsConsumer.receive();
            System.out.println(name + new String(msg.getData()));
            //noinspection unchecked
            allTopicsConsumer.acknowledge(msg);
            System.out.println(i++);
        }
    }
}
