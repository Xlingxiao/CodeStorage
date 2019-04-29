package dataDistribution.consummer;

import org.apache.pulsar.client.api.*;
import utils.DateUtil;
import utils.PropertiesUtil;
import utils.PulsarUtil;

import java.io.IOException;
import java.util.*;

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
    private final DateUtil dateUtil = new DateUtil();
    private PropertiesUtil propertiesUtil = new PropertiesUtil();


    public static void main(String[] args) throws IOException {
        LowerApp lowerApp = new LowerApp();
        lowerApp.start();
    }

    private LowerApp() throws IOException {
        /*读取pulsar的配置文件*/
        util = new PulsarUtil(propertiesUtil);
        properties = propertiesUtil.getProperties("pulsar/pulsar.properties");
        producer = util.getProducer(properties.getProperty("logTopic"));
    }

    private void start() throws PulsarClientException {
        /*获得topic 消费者组名*/
        String subName = properties.getProperty("subName");

        /*多个topic放在multiTopic中使用,分隔。*/
        String topics = properties.getProperty("consumerMultiTopic");
        List<String> topicList = Arrays.asList(topics.split(","));
        PulsarClient pulsarClient = util.getClient();
        ConsumerBuilder consumerBuilder = pulsarClient.newConsumer().subscriptionName(subName);

        /*通过列表订阅*/
        //noinspection unchecked
        Consumer<Byte[]> allTopicsConsumer = consumerBuilder.topics(topicList).subscribe();
        String consumerName = allTopicsConsumer.getConsumerName();


        //noinspection InfiniteLoopStatement
        for (; ; ) {
            Message msg = allTopicsConsumer.receive();
            //消费
            consume(msg);
            //构建BackLog
            String backlog = String.format("Time:%s " +
                            "Topic:%s " + "Message ID:%s " +
                            "SubscriptionName:%s " + "ConsumerName:%s %s",
                    dateUtil.dateToString(System.currentTimeMillis()),
                    msg.getTopicName(),msg.getKey(), subName,
                    consumerName, "配置生效");

            // 使用生产者将messageId发送到log topic中
            System.out.println(backlog);
            backLog(msg.getKey(), backlog);

            //返回ack
            allTopicsConsumer.acknowledge(msg);
        }
    }

    /*模拟消费*/
    private void consume(Message msg) {
        System.out.println(String.format("%s消费了%s", msg.getTopicName(), new String(msg.getData())));
    }

    private void backLog(String msgId, String logContent) {
        producer.newMessage().key(String.valueOf(msgId))
                .value(logContent.getBytes())
                .sendAsync();
    }
}
