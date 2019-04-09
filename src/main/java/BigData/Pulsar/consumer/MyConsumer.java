package BigData.Pulsar.consumer;

import BigData.Pulsar.admin.LXAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClientException;
import org.junit.jupiter.api.Test;
import utils.PropertiesUtil;
import utils.PulsarUtil;

import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

/**
 * @Author: LX
 * @Date: 2019/4/1 19:59
 * @Version: 1.0
 */
@SuppressWarnings("FieldCanBeLocal")
class MyConsumer {

    private PulsarUtil util = new PulsarUtil();
    private PropertiesUtil propertiesUtil = new PropertiesUtil();
    private Properties properties;
    private LXAdmin admin;

    @Test
    void main() throws IOException, PulsarAdminException {
        properties = propertiesUtil.getProperties("pulsar/pulsar.properties");
        admin = new LXAdmin(properties);

        String rowTopic = properties.getProperty("topic");
        String subName = properties.getProperty("subName");
        String rowConsumerName = properties.getProperty("consumerName");
        int num = admin.getPartitionsCount(rowTopic);

        for (int i = 0; i < num; i++) {
            Consumer<byte[]> consumer = util.getConsumer(
                    String.format("%s-partition-%d", rowTopic, i),
                    subName,
                    String.format("%s_%d", rowConsumerName, i));
            new Thread(new MyWorker(consumer)).start();
        }
        Scanner sc = new Scanner(System.in);
        sc.nextLine();
    }

    class MyWorker implements Runnable {
        Consumer<byte[]> consumer;
        MyWorker(Consumer<byte[]> consumer) {
            this.consumer = consumer;
        }

        @SuppressWarnings("InfiniteLoopStatement")
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
