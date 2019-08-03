package Kafka.Producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import utils.PropertiesUtil;

import java.io.IOException;
import java.util.Properties;

/**
 * @Author: LX
 * @Date: 2019/4/11 16:45
 * @Version: 1.0
 */
public class ProducerUtil {

    private static PropertiesUtil propertiesUtil = new PropertiesUtil();

    public Producer<String,String> getProducer() {
        return InnerClass.producer;
    }

    private static class InnerClass {
        static Properties props;

        static {
            try {
                props = propertiesUtil.getProperties("kafka/producer.properties");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private static Producer<String, String> producer = new KafkaProducer<>(props);
    }

}
