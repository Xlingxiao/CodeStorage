package utils;

import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.client.api.*;
import org.apache.pulsar.client.impl.conf.ClientConfigurationData;
import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.util.Properties;

/**
 * @Author: LX
 * @Date: 2019/4/1 19:01
 * @Version: 1.0
 */
@SuppressWarnings({"UnnecessaryLocalVariable", "unused", "Duplicates", "WeakerAccess"})
public class PulsarUtil {

    private PulsarClient client;
    private PulsarClient tlsClient;
    private PropertiesUtil propUtil;
    private static Properties prop;

    /*初始化Pulsar Client 以及 Pulsar LXAdmin*/
    public PulsarUtil() throws IOException {
        propUtil = new PropertiesUtil();
        prop = propUtil.getProperties("pulsar/pulsar.properties");
        init();
    }

    public PulsarUtil(PropertiesUtil propertiesUtil) throws IOException {
        this.propUtil = propertiesUtil;
        prop = propUtil.getProperties("pulsar/pulsar.properties");
        init();
    }

    public PulsarUtil(Properties properties) throws IOException {
        prop = properties;
        init();
    }

    private void init() throws IOException {
        // 创建Pulsar客户端
        client = getTlsAndTokenClient(prop.getProperty("trustTlsCertFile"), prop.getProperty("adminToken"));
    }

    /*获得client*/
    public PulsarClient getClient() {
        return client;
    }

    /*获得tls加密client*/
    public PulsarClient getTlsClient(String tlsCertificatePath) throws PulsarClientException {
        if (tlsClient == null) {
            synchronized (this) {
                if (tlsClient == null) {
                    String Url = prop.getProperty("tlsBrokerUrl");
                    tlsClient = PulsarClient.builder()
                            .serviceUrl(Url)
                            .tlsTrustCertsFilePath(tlsCertificatePath)
                            .allowTlsInsecureConnection(false).build();
                }
            }
        }
        return tlsClient;
    }

    /*获得tls加密client并用授权token*/
    public PulsarClient getTlsAndTokenClient(String tlsCertificatePath, String token) throws PulsarClientException {
        if (tlsClient == null) {
            synchronized (this) {
                if (tlsClient == null) {
                    String Url = prop.getProperty("tlsBrokerUrl");
                    tlsClient = PulsarClient.builder()
                            .serviceUrl(Url)
                            .tlsTrustCertsFilePath(tlsCertificatePath)
                            .authentication(AuthenticationFactory.token(token))
                            .allowTlsInsecureConnection(false).build();
                }
            }
        }
        return tlsClient;
    }

    /*获得consumer，byte数组类型*/
    public Consumer<byte[]> getConsumer(String topic, String subName, String consumerName) throws PulsarClientException {
        Consumer<byte[]> consumer = client.newConsumer()
                .topic(topic)
                .subscriptionName(subName)
                .consumerName(consumerName)
                .subscribe();
        return consumer;
    }

    /*不指定topic使用配置文件中的testTopic，获得consumer，byte数组类型*/
    public Consumer<byte[]> getConsumer(String subName, String consumerName) throws PulsarClientException {
        return getConsumer(prop.getProperty("testTopic"), subName, consumerName);
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


    /*-----------------LXAdmin----------------------*/


}
