package BigData.Pulsar.consumer;

import org.apache.pulsar.client.api.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: LX
 * @Date: 2019/4/28 10:56
 * @Version: 1.0
 */
@SuppressWarnings({"Duplicates", "NonAsciiCharacters", "WeakerAccess"})
public class 普通消费者加密授权 {
    @Test
    void main() throws IOException {
        // 准备Pulsar的服务url地址
        String Url = "pulsar+ssl://172.16.2.107:6651/";
        // 创建Pulsar客户端
        //使用tls鉴权
        /*Map<String, String> map = new HashMap<>();
        map.put("tlsCertFile", "F:/lx/Code/CodeStorage/src/main/resources/pulsar/admin.cert.pem");
        map.put("tlsKeyFile", "F:/lx/Code/CodeStorage/src/main/resources/pulsar/admin.key-pk8.pem");*/
        PulsarClient client = PulsarClient.builder()
                .serviceUrl(Url)
                .tlsTrustCertsFilePath("F:\\lx\\Code\\CodeStorage\\src\\main\\resources\\pulsar\\ca.cert.pem")
                //.authentication("org.apache.pulsar.client.impl.auth.AuthenticationTls", map)
                .authentication(AuthenticationFactory.token("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiJ9.fy-zG1gGL_pIWzEMCBupgQWOmUlN4guNrCibPO-h9JE"))
                .allowTlsInsecureConnection(false).build();
        // 使用Pulsar客户端创建生产者
        // 每个subscriptionName相当于一个消费分组，一个分组只能对一条数据消费一次
        Consumer<byte[]> consumer = client.newConsumer().topic("persistent://lx/java/test")
                .subscriptionName("subscriptionName").subscribe();
//        consumer.seek(MessageId.earliest);

        System.out.println(consumer.getTopic());
        while (true) {
            // 获得消息
            Message msg = consumer.receive();
            //消费消息
            System.out.println(String.format("%s\t%s",msg.getKey(),new String(msg.getData())));
            //发送commit到pulsar服务
            consumer.acknowledge(msg);
        }
    }
}
