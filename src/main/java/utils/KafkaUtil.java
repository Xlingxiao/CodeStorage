package utils;


import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.zookeeper.KeeperException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Properties;

/**
 * @Author: LX
 * @Date: 2019/4/16 9:49
 * @Version: 1.0
 */
@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class KafkaUtil {

    private static final String producerPropPath = "kafka/producer.properties";
    private static final String consumerPropPath = "kafka/consumer.properties";
    private static String zooPath = "/brokers/topics";
    private static PropertiesUtil propertiesUtil;
    private static Properties consumerProperties;
    private static Properties producerProperties;
    private static ZKUtil zkUtil;

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        String topic = "sniTopic";
        KafkaUtil kafkaUtil = new KafkaUtil();
        int num = kafkaUtil.getPartitionsNum(topic);
        System.out.println(String.format("zkServer: %s\nTopic: %s\nPartitions: %d\n", zkUtil.getZkServerAddr(), topic, num));
    }

    /*初始化时要加载consumer/producer的配置文件*/
    public KafkaUtil() throws IOException {
        propertiesUtil = new PropertiesUtil();
        zkUtil = new ZKUtil(propertiesUtil);
        consumerProperties = propertiesUtil.getProperties(consumerPropPath);
        producerProperties = propertiesUtil.getProperties(producerPropPath);
    }

    public KafkaUtil(ZKUtil zk_util, PropertiesUtil prop_util) throws IOException {
        zkUtil = zk_util;
        propertiesUtil = prop_util;
        consumerProperties = propertiesUtil.getProperties(consumerPropPath);
        producerProperties = propertiesUtil.getProperties(producerPropPath);
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
