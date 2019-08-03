package dataDistribution.consummer;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClientException;
import utils.DateUtil;
import utils.PropertiesUtil;
import utils.PulsarUtil;

import java.io.IOException;
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
        String[] topicList = topics.split(",");

        /*获取是否需要从头消费*/
        boolean fromEarliest = Boolean.parseBoolean(properties.getProperty("fromEarliest"));

        /*根据订阅的topic进行开线程*/
        for (String s : topicList) {
            String consumerName = String.format("%s_%s", subName, s);
            Consumer<byte[]> consumer = util.getConsumer(s, subName, consumerName);
            if(fromEarliest)
                consumer.seek(MessageId.earliest);
            ConsumerService consumerService = new ConsumerService(dateUtil, consumer, producer);
            new Thread(consumerService).start();
        }
    }
}
