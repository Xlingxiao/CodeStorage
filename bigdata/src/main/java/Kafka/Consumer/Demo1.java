package Kafka.Consumer;

import org.apache.zookeeper.KeeperException;
import org.junit.jupiter.api.Test;
import utils.KafkaUtil;
import utils.PropertiesUtil;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: LX
 * @Date: 2019/4/11 14:55
 * @Version: 1.0
 */
public class Demo1 {
    @Test
    void main() throws InterruptedException, IOException, KeeperException {
        PropertiesUtil prop = new PropertiesUtil();
        String topic = prop.getProperties("kafka/broker.properties").getProperty("topic");
        KafkaUtil util = new KafkaUtil();
        int partitionNum = util.getPartitionsNum(topic);
        /*String topic ="lxTopic";
        int threadNum = 50;*/
        for (int i = 0; i < partitionNum; i++) {
            Thread consumer = new Thread(new MyConsumer(util, topic, i));
            consumer.start();
        }
        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
    }

}
