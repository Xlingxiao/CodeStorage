package BigData.Pulsar.producer;

import BigData.Pulsar.admin.LXAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.client.api.Producer;
import org.junit.jupiter.api.Test;
import utils.FileUtil;
import utils.PropertiesUtil;
import utils.PulsarUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Properties;

/**
 * @Author: LX
 * @Date: 2019/4/1 19:53
 * @Version: 1.0
 */
class MyProducer {

    private String topic;
    private PulsarUtil util;
    private Properties properties;
    private PropertiesUtil propertiesUtil = new PropertiesUtil();

    @Test
    void main() throws IOException, InterruptedException, PulsarAdminException {
        util = new PulsarUtil();
        properties = propertiesUtil.getProperties("pulsar/pulsar.properties");
        LXAdmin admin = new LXAdmin(properties);
        topic = properties.getProperty("logTopic");
        Producer<byte[]> producer = util.getProducer(topic);
        FileUtil fileUtil = new FileUtil();
        String dataLogPath = "pulsarBroker.log";
        BufferedReader br = fileUtil.getRelativeBuffer(dataLogPath);
        String tmp;
        while ((tmp = br.readLine()) != null) {
            producer.newMessage()
                    .value(tmp.getBytes())
                    .send();
        }
        /*int partitionCount = admin.getPartitionsCount(topic);
        int count = admin.countTopicMessage(topic, partitionCount);
        for (int i = count; i < Integer.MAX_VALUE; i++) {
            String msg = String.format("message_%d", i);
            producer.newMessage()
                    .key(i + "")
                    .value(msg.getBytes())
                    .send();
            if (i % 100 == 0) System.out.println(msg);
            TimeUnit.SECONDS.sleep(1);
        }*/
    }

}
