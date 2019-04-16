package BigData.Kafka.Producer;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: LX
 * @Date: 2019/4/11 16:44
 * @Version: 1.0
 */
@SuppressWarnings("Duplicates")
public class LxWorker implements Runnable {

    private int num;
    private String rowData;
    private Properties brokerProps;
    CountDownLatch largeLatch;

    public LxWorker(int num, String rowData, Properties brokerProps, CountDownLatch largeLatch) {
        this.num = num;
        this.rowData = rowData;
        this.brokerProps = brokerProps;
        this.largeLatch = largeLatch;
    }

    @Override
    public void run() {
        ProducerUtil producerUtil = new ProducerUtil();
        Producer<String, String> producer = producerUtil.getProducer();
        String topic = brokerProps.getProperty("topic");
        CountDownLatch latch = new CountDownLatch(num);
        Callback callback = (recordMetadata, e) -> {
            latch.countDown();
//            System.out.println("success");
        };

        for (int i = 0; i < num; i++) {
            String data = rowData + i;
            ProducerRecord<String, String> msg = new ProducerRecord<>(topic, data);
            producer.send(msg, callback);
        }

        try {
            latch.await();
            largeLatch.countDown();
            System.out.println("success");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
