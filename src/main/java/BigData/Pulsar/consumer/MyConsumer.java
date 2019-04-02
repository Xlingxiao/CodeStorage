package BigData.Pulsar.consumer;

import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.common.partition.PartitionedTopicMetadata;
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
class MyConsumer {

    private PulsarUtil util = new PulsarUtil();
    private PropertiesUtil propertiesUtil = new PropertiesUtil();

    @Test
    void main() throws IOException, PulsarAdminException {
        Properties properties = propertiesUtil.getProperties("pulsar/pulsar.properties");
        PulsarAdmin admin = util.getAdmin();
        String url = properties.getProperty("topic");
        PartitionedTopicMetadata p = admin.topics().getPartitionedTopicMetadata(url);
        int num = p.partitions;

        for (int i = 0; i < num; i++) {
            Consumer<byte[]> consumer = util.getConsumer(
                    String.format("%s-partition-%d", properties.getProperty("topic"), i),"JavaTest");
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
