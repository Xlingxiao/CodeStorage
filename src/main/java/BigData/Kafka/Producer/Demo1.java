package BigData.Kafka.Producer;

import org.apache.kafka.clients.producer.*;
import utils.FileUtil;
import utils.PropertiesUtil;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: LX
 * @Date: 2019/4/11 14:55
 * @Version: 1.0
 */
@SuppressWarnings({"Duplicates", "unused", "SameParameterValue"})
public class Demo1 {
    private PropertiesUtil propertiesUtil = new PropertiesUtil();
    private Properties brokerProps;

    public static void main(String[] args) throws IOException, InterruptedException {
        Demo1 demo1 = new Demo1();
        demo1.initBrokerProps();
        Producer<String, String> producer = demo1.getProducer();
        String topic = demo1.brokerProps.getProperty("topic");
        int producerNum = 5;
        int[] times = {10000,100000,1000000,10000000};
        long[] countTime = new long[times.length];
        for (int i = 0; i < times.length; i++) {
            long t = 0;
            for (int j = 0; j < 3; j++) {
                System.out.printf("测试数据：%d\n", times[i]);
                t += demo1.testOneProducer(producer, topic, times[i]);
                //t += demo1.testMultiProducer(topic, times[i],producerNum);
            }
            countTime[i] = t / 3;
        }

        System.out.println("发送结束");

        for (int i = 0; i < times.length; i++) {
            System.out.printf("发送%d条数据平均时间：%dms\n", times[i], countTime[i]);
        }

        System.out.println("数据量 | 发送用时\n--- | ---");
        for (int i = 0; i < times.length; i++) {
            System.out.printf("%d | %dms\n", times[i], countTime[i]);
        }
    }

    /*测试发送一轮数据 num指定发送次数*/
    private long testOneProducer(Producer<String, String> producer, String topic, int num) throws IOException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(num);
        String rowData = getData("data.txt");
        Callback callback = (recordMetadata, e) -> {
            latch.countDown();
            //System.out.println("success");
        };
        long start = System.currentTimeMillis();
        for (int i = 0; i < num; i++) {
            String data = String.format("%s  %d", rowData, i);
            ProducerRecord<String, String> msg = new ProducerRecord<>(topic, data);
            producer.send(msg, callback);
        }
        latch.await();
        return System.currentTimeMillis() - start;
    }

    private long testMultiProducer(String topic, int msgNum, int producerNum) throws IOException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(producerNum);
        String rowData = getData("data.txt");
        int oneProducerMission = msgNum / producerNum;
        long start = System.currentTimeMillis();
        for (int i = 0; i < producerNum; i++) {
            Start myStart = new Start(oneProducerMission, topic, rowData, latch);
            new Thread(myStart).start();
        }
        latch.await();
        return System.currentTimeMillis() - start;
    }

    private class Start implements Runnable{

        private int num;
        private String topic;
        private String ROW_DATA;
        private CountDownLatch bigLatch;
        private CountDownLatch latch;

        Start(int num, String topic, String ROW_DATA, CountDownLatch bigLatch) {
            this.num = num;
            this.topic = topic;
            this.ROW_DATA = ROW_DATA;
            this.bigLatch = bigLatch;
        }

        @Override
        public void run() {
            Producer<String, String> producer = null;
            try {
                producer = getProducer();
            } catch (IOException e) {
                e.printStackTrace();
            }
            latch = new CountDownLatch(num);
            Callback callback = (recordMetadata, e) -> {
                latch.countDown();
                //System.out.println("success");
            };
            String ThreadName = Thread.currentThread().getName();
            System.out.printf("线程%s启动",ThreadName);
            for (int i = 0; i < num; i++) {
                String data = String.format("%s--%s--%d", ThreadName, ROW_DATA, i);
                ProducerRecord<String, String> msg = new ProducerRecord<>(topic, data);
                assert producer != null;
                producer.send(msg, callback);
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            bigLatch.countDown();
        }
    }

   /* @Test
    void main() throws IOException, InterruptedException {
        initBrokerProps();
        Producer<String, String> producer = getProducer();
        String topic = brokerProps.getProperty("topic");
        String rowData = getData("data.txt");
        int num = 1000000;
        CountDownLatch latch = new CountDownLatch(num);
        Callback callback = (recordMetadata, e) -> {
            latch.countDown();
            //System.out.println("success");
        };
        long start = System.currentTimeMillis();
        for (int i = 0; i < num; i++) {
            String data = String.format("%s  %d", rowData, i);
            ProducerRecord<String, String> msg = new ProducerRecord<>(topic,data);
            producer.send(msg, callback);
        }
        latch.await();
        System.out.printf("总时间：%dms\n", System.currentTimeMillis() - start);
        System.out.println("send message over.");
    }*/

    private Producer<String, String> getProducer() throws IOException {
        Properties props = propertiesUtil.getProperties("kafka/producer.properties");
        return new KafkaProducer<>(props);
    }

    private void initBrokerProps() throws IOException {
        brokerProps = propertiesUtil.getProperties("kafka/broker.properties");
    }

    private String getData(String path) throws IOException {
        FileUtil fileUtil = new FileUtil();
        return fileUtil.getRelativeContent(path);
    }


    /*@Test
    void qqq() throws InterruptedException, IOException {
        initBrokerProps();
        String data = getData("data.txt");
        int num = 100000;
        int threadCount = 10;
        long start = System.currentTimeMillis();
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            LxWorker worker = new LxWorker(num / threadCount, data, brokerProps, latch);
            Thread thread = new Thread(worker);
            thread.start();
        }
        latch.await();
        System.out.printf("总时间：%dms\n", System.currentTimeMillis() - start);
        System.out.println("send message over.");
    }*/

}
