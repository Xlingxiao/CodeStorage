package utils;

import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.client.api.*;
import org.apache.pulsar.client.impl.conf.ClientConfigurationData;

import java.io.IOException;
import java.util.Properties;

/**
 * @Author: LX
 * @Date: 2019/4/1 19:01
 * @Version: 1.0
 */
@SuppressWarnings({"UnnecessaryLocalVariable", "unused", "Duplicates"})
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

    private PulsarAdmin admin;

    /*初始化Admin*/
    private void initAdmin() throws IOException {
        ClientConfigurationData config = new ClientConfigurationData();
        config.setUseTls(false);
        config.setTlsAllowInsecureConnection(false);
        config.setTlsTrustCertsFilePath(null);
        admin = new PulsarAdmin(prop.getProperty("restUrl"), config);
    }

    /*展示Message ID*/
    public void displayMessageId(MessageId id) {
        String[] idInfo = id.toString().split(":");
        System.out.println(String.format("ledger id = %s", idInfo[0]));
        System.out.println(String.format("Entry Id = %s", idInfo[1]));
        System.out.println(String.format("Partition Index = %s", idInfo[2]));
    }

    /*获得topic中总共有多少条数据*/
    public int countTopicMessage(String rowTopic, int partitionCount) throws PulsarAdminException {
        int count = 0;
        for (int i = 0; i < partitionCount; i++) {
            String topic = String.format("%s-partition-%d", rowTopic, i);
            MessageId id = admin.topics().getLastMessageId(topic);
            //displayMessageId(id);
            int c = Integer.parseInt(id.toString().split(":")[1]);
            count += c;
        }
        return count + partitionCount;
    }

    /*获得topic中的Partition数量*/
    public int getPartitionsCount(String topic) throws PulsarAdminException {
        return admin.topics().getPartitionedTopicMetadata(topic).partitions;
    }


}
