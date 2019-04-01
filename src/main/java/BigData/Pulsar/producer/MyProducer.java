package BigData.Pulsar.producer;

import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClientException;
import org.junit.jupiter.api.Test;
import utils.PulsarUtil;

/**
 * @Author: LX
 * @Date: 2019/4/1 19:53
 * @Version: 1.0
 */
class MyProducer {

    private PulsarUtil util = new PulsarUtil();

    @Test
    void main() throws PulsarClientException {
        Producer<byte[]> producer = util.getProducer();
        for (int i = 0; i < 20; i++) {
            producer.newMessage()
                    .key("key")
                    .value(String.format("message_%d", i).getBytes())
                    .send();
        }
    }


}
