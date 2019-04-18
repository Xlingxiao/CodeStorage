//package BigData.Pulsar.producer;
//
//import BigData.Pulsar.admin.LXAdmin;
//import org.apache.pulsar.client.admin.PulsarAdminException;
//import org.apache.pulsar.client.api.Producer;
//import org.apache.pulsar.client.api.TypedMessageBuilder;
//import org.junit.jupiter.api.Test;
//import utils.PropertiesUtil;
//import utils.PulsarUtil;
//
//import java.io.IOException;
//import java.util.Properties;
//import java.util.concurrent.TimeUnit;
//
///**
// * @Author: LX
// * @Date: 2019/4/1 19:53
// * @Version: 1.0
// */
//class MyProducer {
//
//    private PulsarUtil util = new PulsarUtil();
//    private PropertiesUtil propertiesUtil = new PropertiesUtil();
//    private String topic;
//    private Properties properties;
//
//    @Test
//    void main() throws IOException, InterruptedException, PulsarAdminException {
//        properties = propertiesUtil.getProperties("pulsar/pulsar.properties");
//        LXAdmin admin = new LXAdmin(properties);
//        topic = properties.getProperty("topic");
//        int partitionCount = admin.getPartitionsCount(topic);
//        Producer<byte[]> producer = util.getProducer(topic);
//        int count = admin.countTopicMessage(topic, partitionCount);
//        for (int i = count; i < Integer.MAX_VALUE; i++) {
//            String msg = String.format("message_%d", i);
//            producer.newMessage()
//                    .key(i + "")
//                    .value(msg.getBytes())
//                    .send();
//            if (i % 100 == 0) System.out.println(msg);
//            TimeUnit.SECONDS.sleep(1);
//        }
//    }
//
//}
