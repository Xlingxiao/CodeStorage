package utils;


import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.zookeeper.KeeperException;
import org.junit.jupiter.api.Test;
import utils.PropertiesUtil;
import utils.ZKUtil;

import java.io.IOException;
import java.util.Properties;

/**
 * @Author: LX
 * @Date: 2019/4/16 9:49
 * @Version: 1.0
 */
@SuppressWarnings({"unused", "WeakerAccess", "FieldCanBeLocal"})
public class KafkaUtil {

    private static final String producerPropPath = "kafka/producer.properties";
    private static final String consumerPropPath = "kafka/consumer.properties";
    private static PropertiesUtil propertiesUtil = new PropertiesUtil();
    private static String zooPath = "/brokers/topics";
    private static Properties consumerProperties;
    private static Properties producerProperties;
    private static ZKUtil zkUtil;

    /*初始化时要加载consumer/producer的配置文件*/
    public KafkaUtil() throws IOException {
        zkUtil = new ZKUtil();
        consumerProperties = propertiesUtil.getProperties(consumerPropPath);
        producerProperties = propertiesUtil.getProperties(producerPropPath);
    }

    @Test
    void main() throws KeeperException, InterruptedException {
        String path = "/brokers/topics";
        String topic = "lxTopic";
        String partition = "partitions";
        int num = getPartitionsNum(String.format("%s/%s/%s", path, topic, partition));
        System.out.println(String.format("Topic: %s Partitions: %d", path, num));
    }

    /*获得topic下的分区数量*/
    public int getPartitionsNum(String topic) throws KeeperException, InterruptedException {
        String path = zooPath + '/' + topic + "/partitions";
        return zkUtil.getChildren(path).size();
    }

    /*-----------------------Producer-----------------------*/

    /*创建单例生产者*/
    public Producer<String,String> getProducer() {
        return InnerClass.producer;
    }

    private static class InnerClass {
        private static Producer<String, String> producer = new KafkaProducer<>(producerProperties);
    }

    /*-----------------------Consumer-----------------------*/

    /*使用预先加载好的配置文件创建消费者*/
    public KafkaConsumer<String, String> getConsumer() {
        return new KafkaConsumer<>(consumerProperties);
    }
}
