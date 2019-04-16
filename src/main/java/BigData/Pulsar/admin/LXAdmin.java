package BigData.Pulsar.admin;

import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.impl.conf.ClientConfigurationData;
import org.junit.jupiter.api.Test;
import utils.PropertiesUtil;
import utils.PulsarUtil;

import java.io.IOException;
import java.util.Properties;

/**
 * @Author: LX
 * @Date: 2019/4/9 13:40
 * @Version: 1.0
 */
@SuppressWarnings({"unused", "Duplicates"})
public class LXAdmin {
    private PulsarAdmin admin;
    private Properties properties;

    @Test
    void main() throws IOException, PulsarAdminException {
        PropertiesUtil util = new PropertiesUtil();
        properties = util.getProperties("pulsar/pulsar.properties");
        initAdmin();
        String rowTopic = properties.getProperty("topic");
        int partition = admin.topics().getPartitionedTopicMetadata(rowTopic).partitions;
        int count = countTopicMessage(rowTopic, partition);
        System.out.println(count);
    }


    public LXAdmin(Properties prop) throws IOException {
        this.properties = prop;
        initAdmin();
    }

    /*初始化Admin*/
    private void initAdmin() throws IOException {
        ClientConfigurationData config = new ClientConfigurationData();
        config.setUseTls(false);
        config.setTlsAllowInsecureConnection(false);
        config.setTlsTrustCertsFilePath(null);
        admin = new PulsarAdmin(properties.getProperty("adminUrl"), config);
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
