package BigData.Pulsar.admin;

import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminBuilder;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.client.api.Authentication;
import org.apache.pulsar.client.api.AuthenticationFactory;
import org.apache.pulsar.client.api.MessageId;
import utils.PropertiesUtil;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

/**
 * @Author: LX
 * @Date: 2019/4/9 13:40
 * @Version: 1.0
 */
@SuppressWarnings({"unused", "Duplicates"})
public class LXAdmin {
    private PulsarAdmin admin;
    private Properties properties;

    public static void main(String[] args) throws IOException, PulsarAdminException {
        String topic = "persistent://lx/java/test";
        int count = 0;
        PropertiesUtil propertiesUtil = new PropertiesUtil();
        Properties prop = propertiesUtil.getProperties("pulsar/pulsar.properties");
        LXAdmin lxAdmin = new LXAdmin(prop);
//        lxAdmin.admin.topics().createPartitionedTopic("persistent://zx/java1/test", 3);
        lxAdmin.printSome();
        int partitionsCount = lxAdmin.getPartitionsCount(topic);
        System.out.println(String.format("topic中有 %d 个分区", partitionsCount));
        count = lxAdmin.countTopicMessage(topic, partitionsCount);
        System.out.println(String.format("topic中有 %d 条消息", count));
        Scanner sc = new Scanner(System.in);
        sc.nextInt();
    }

    public LXAdmin(Properties prop) throws IOException {
        this.properties = prop;
        initAdmin();
    }

    /*初始化Admin*/
    private void initAdmin() throws IOException {
        //tls全权限使用
        /*Map<String, String> map = new HashMap<>();
        map.put("tlsCertFile", properties.getProperty("tlsCertFile"));
        map.put("tlsKeyFile", properties.getProperty("tlsKeyFile"));*/
        admin = PulsarAdmin.builder()
                .serviceHttpUrl(properties.getProperty("tlsRestUrl"))
                .tlsTrustCertsFilePath(properties.getProperty("trustTlsCertFile"))
//                .authentication("org.apache.pulsar.client.impl.auth.AuthenticationTls",map)
                .authentication(AuthenticationFactory.token(properties.getProperty("adminToken")))
                .allowTlsInsecureConnection(false).build();
        /*ClientConfigurationData config = new ClientConfigurationData();
        config.setTlsAllowInsecureConnection(false);
        config.setTlsHostnameVerificationEnable(Boolean.parseBoolean(properties.getProperty("useTls")));
        config.setTlsTrustCertsFilePath(properties.getProperty("tlsCertFile"));
        admin = new PulsarAdmin(properties.getProperty("tlsRestUrl"), config);*/
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
            displayMessageId(id);
            int c = Integer.parseInt(id.toString().split(":")[1]);
            count += c;
        }
        return count + partitionCount;
    }

    /*获得topic中的Partition数量*/
    public int getPartitionsCount(String topic) throws PulsarAdminException {
        return admin.topics().getPartitionedTopicMetadata(topic).partitions;
    }

    public void printSome() throws PulsarAdminException {

        System.out.println(admin.clusters().getClusters());
        List<String> t = admin.tenants().getTenants();
        for (String s : t) {
            System.out.println(s);
            List<String> namespaces = admin.namespaces().getNamespaces(s);
            for (String namespace : namespaces) {
                System.out.println(namespace);
                List<String> topics = admin.topics().getPartitionedTopicList(namespace);
                System.out.println(topics);
            }
        }
    }
}
