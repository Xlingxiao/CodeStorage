package BigData.Pulsar.producer;

import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClientException;
import org.junit.jupiter.api.Test;
import utils.PropertiesUtil;
import utils.PulsarUtil;

import java.io.IOException;
import java.util.Properties;

/**
 * @Author: LX
 * @Date: 2019/4/1 19:53
 * @Version: 1.0
 */
class MyProducer {

    private PulsarUtil util = new PulsarUtil();

    @Test
    void main() throws IOException {
        PropertiesUtil propertiesUtil = new PropertiesUtil();
        Properties properties = propertiesUtil.getProperties("pulsar/pulsar.properties");
        Producer<byte[]> producer = util.getProducer(properties.getProperty("topic"));
        for (int i = 0; i < 20; i++) {
            producer.newMessage()
                    .key("key")
                    .value(String.format("message_%d", i).getBytes())
                    .send();
        }
    }


}
