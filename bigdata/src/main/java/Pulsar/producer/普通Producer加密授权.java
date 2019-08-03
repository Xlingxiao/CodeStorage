package Pulsar.producer;

import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.client.api.AuthenticationFactory;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Author: LX
 * @Date: 2019/4/28 11:04
 * @Version: 1.0
 */
@SuppressWarnings({"Duplicates", "NonAsciiCharacters"})
public class 普通Producer加密授权 {
    public static void main(String[] args) throws IOException, InterruptedException, PulsarAdminException {
        // 准备Pulsar的服务url地址
        String Url = "pulsar+ssl://172.16.2.107:6653/";
        // 创建Pulsar客户端
        //tls授权使用
        /*Map<String, String> map = new HashMap<>();
        map.put("tlsCertFile", "F:\\lx\\Code\\CodeStorage\\src\\main\\resources\\pulsar\\admin.cert.pem");
        map.put("tlsKeyFile", "F:\\lx\\Code\\CodeStorage\\src\\main\\resources\\pulsar\\admin.key-pk8.pem");*/
        // jwt授权使用
        PulsarClient client = PulsarClient.builder()
                .serviceUrl(Url)
                .tlsTrustCertsFilePath("F:\\lx\\Code\\CodeStorage\\src\\main\\resources\\pulsar\\ca.cert.pem")
                //.authentication("org.apache.pulsar.client.impl.auth.AuthenticationTls", map)
                .authentication(AuthenticationFactory.token("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0MyJ9.wGFd0RslYqT4IKAFAq-1FXcFlSM0VEvK109GpZBXzOY"))
                .allowTlsInsecureConnection(false).build();
        String topic = "persistent://public/log/topicLog";
        int count = 0;
        /*下面几句测试时非必需*/
        /*PropertiesUtil propertiesUtil = new PropertiesUtil();
        Properties prop = propertiesUtil.getProperties("pulsar/pulsar.properties");
        LXAdmin lxAdmin = new LXAdmin(prop);
        lxAdmin.printSome();
        int partitionsCount = lxAdmin.getPartitionsCount(topic);
        System.out.println(String.format("topic中有 %d 个分区", partitionsCount));
        count = lxAdmin.countTopicMessage(topic, partitionsCount);
        System.out.println(String.format("topic中有 %d 条消息", count));*/
        // 使用Pulsar客户端创建生产者
        Producer<byte[]> producer = client.newProducer().topic(topic).create();
        for (int i = count; i < Integer.MAX_VALUE; i++) {
            // 调用send()方法发送消息 同步
            String msg = String.format("Hello Java Client____%d", i);
            System.out.println(msg);
            producer.send(msg.getBytes());
            producer.flush();
            TimeUnit.MILLISECONDS.sleep(1000);
        }

        // 异步
        /*producer.sendAsync("Hello Java Client Async".getBytes())
                .thenAccept(messageId -> System.out.println("message send success"));*/
        // 调用flush()方法将消息发送到Pulsar服务
        // 同步关闭producer和client
        producer.close();
        client.close();
        // 异步关闭producer和client
        /*producer.closeAsync().thenRun(() -> System.out.println("Producer closed"));
        client.closeAsync().thenRun(() -> System.out.println("Client closed"));*/
    }
}
