package BigData.Pulsar.consumer.dataDistribution;

import org.apache.pulsar.client.api.*;
import utils.PropertiesUtil;
import utils.PulsarUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @Author: LX
 * @Date: 2019/4/23 20:30
 * @Version: 1.0
 */
@SuppressWarnings({"Duplicates", "FieldCanBeLocal"})
class LowerApp {

    private PulsarUtil util;
    private Properties properties;
    private Producer<byte[]> producer;
    private PropertiesUtil propertiesUtil = new PropertiesUtil();


    private LowerApp() throws IOException {
        /*读取pulsar的配置文件*/
        util = new PulsarUtil(propertiesUtil);
        properties = propertiesUtil.getProperties("pulsar/pulsar.properties");
        producer = util.getProducer(properties.getProperty("logTopic"));
    }

    public static void main(String[] args) throws IOException {
        LowerApp lowerApp = new LowerApp();
        lowerApp.start();
    }

    private void start() throws PulsarClientException {
        /*获得topic 消费者组名*/
        String subName = properties.getProperty("subName");
        /*多个topic放在multiTopic中使用,分隔。*/
        String topics = properties.getProperty("multiTopic");
        List<String> topicList = Arrays.asList(topics.split(","));
        PulsarClient pulsarClient = util.getClient();
        ConsumerBuilder consumerBuilder = pulsarClient.newConsumer().subscriptionName(subName);

        /*通过列表订阅*/
        //noinspection unchecked
        Consumer<Byte[]> allTopicsConsumer = consumerBuilder.topics(topicList).subscribe();

        //noinspection InfiniteLoopStatement
        for (; ; ) {
            Message msg = allTopicsConsumer.receive();
            //消费
            consume(msg);
            //构建BackLog
            String backLog = String.format("Topic:%s\tSubscriptionName:%s\tConsumerName:%s\t%s",
                    msg.getTopicName(), subName, allTopicsConsumer.getConsumerName(), "配置生效");

            // 使用生产者将messageId发送到log topic中
            backLog(msg.getKey(), backLog);
            //返回ack
            allTopicsConsumer.acknowledge(msg);
        }
    }

    /*模拟消费*/
    private void consume(Message msg) {
        System.out.println(new String(msg.getData()));
    }

    private void backLog(String msgId, String logContent) {
        producer.newMessage().key(String.valueOf(msgId))
                .value(logContent.getBytes())
                .sendAsync();
    }
}
