package BigData.Pulsar.consumer;

import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.client.api.Authentication;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.impl.conf.ClientConfigurationData;
import org.apache.pulsar.common.partition.PartitionedTopicMetadata;
import org.junit.jupiter.api.Test;
import utils.PropertiesUtil;
import utils.PulsarUtil;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: LX
 * @Date: 2019/4/1 19:59
 * @Version: 1.0
 */
public class MyConsumer {

    private PulsarUtil util = new PulsarUtil();
    PulsarAdmin admin;

    @Test
    void main() throws IOException, PulsarAdminException, InterruptedException {
        PulsarUtil util = new PulsarUtil();
        PropertiesUtil propertiesUtil = new PropertiesUtil();
        Properties properties = propertiesUtil.getProperties("pulsar/pulsar.properties");
        admin = util.getAdmin();
        String url = properties.getProperty("topic");
        PartitionedTopicMetadata p = admin.topics().getPartitionedTopicMetadata(url);
        int num = p.partitions;

        CountDownLatch latch = new CountDownLatch(num);
        for (int i = 0; i < num; i++) {
            Consumer<byte[]> consumer = util.getConsumer(
                    String.format("%s-partition-%d", properties.getProperty("topic"), i),"JavaTest");
            new Thread(new MyWorker(consumer)).start();
        }
        latch.await();
    }

    class MyWorker implements Runnable {
        Consumer<byte[]> consumer;
        MyWorker(Consumer<byte[]> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void run() {
            String name = Thread.currentThread().getName() + " ";
            System.out.println(String.format("%s已经启动,消费%s的数据",
                    name,consumer.getTopic()));
            while (true) {
                try {
                    Message msg = consumer.receive();
                    System.out.println(name + new String(msg.getData()));
                    consumer.acknowledge(msg);
                } catch (PulsarClientException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
