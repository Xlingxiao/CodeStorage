package utils;

import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.impl.conf.ClientConfigurationData;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Properties;

/**
 * @Author: LX
 * @Date: 2019/4/1 19:01
 * @Version: 1.0
 */
@SuppressWarnings({"UnnecessaryLocalVariable", "WeakerAccess", "unused"})
public class PulsarUtil {

    private PulsarClient client;
    private static final PropertiesUtil propUtil = new PropertiesUtil();
    private Properties prop;

    /*初始化Pulsar Client 以及 Pulsar Admin*/
    public PulsarUtil() {
        try {
            prop = propUtil.getProperties("pulsar/pulsar.properties");
            // 初始化admin
            initAdmin();
            // 准备Pulsar的服务url地址
            String localClusterUrl = prop.getProperty("brokerUrl");
            // 创建Pulsar客户端
            client = PulsarClient.builder().serviceUrl(localClusterUrl).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*获得consumer，byte数组类型*/
    public Consumer<byte[]> getConsumer(String topic,String subName) throws PulsarClientException {
        Consumer<byte[]> consumer = client.newConsumer()
                .topic(topic)
                .subscriptionName(subName)
                .subscribe();
        return consumer;
    }

    /*不指定topic使用配置文件中的testTopic，获得consumer，byte数组类型*/
    public Consumer<byte[]> getConsumer(String subName) throws PulsarClientException {
        return getConsumer(prop.getProperty("testTopic"), subName);
    }

    /*获得producer，byte[]型*/
    public Producer<byte[]> getProducer(String topic) throws PulsarClientException {
        Producer<byte[]> producer = client.newProducer().topic(topic).create();
        return producer;
    }

    /*不指定topic使用配置文件中的testTopic获得producer，byte[]型*/
    public Producer<byte[]> getProducer() throws PulsarClientException {
        return getProducer(prop.getProperty("testTopic"));
    }



    /*-----------------Admin----------------------*/

    private PulsarAdmin admin;

    private void initAdmin() throws PulsarClientException {
        ClientConfigurationData config = new ClientConfigurationData();
        config.setUseTls(false);
        config.setTlsAllowInsecureConnection(false);
        config.setTlsTrustCertsFilePath(null);
        admin = new PulsarAdmin("http://172.16.2.107:8080", config);
    }

    public PulsarAdmin getAdmin() {
        return admin;
    }

    @Test
    void main() throws PulsarClientException {
        System.out.println();
    }

}
