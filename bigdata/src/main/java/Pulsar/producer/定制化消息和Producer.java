package Pulsar.producer;

import org.apache.pulsar.client.api.*;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

/**
 * @Author: LX
 * @Date: 2019/4/1 12:50
 * @Version: 1.0
 */

@SuppressWarnings("NonAsciiCharacters")
class 定制化消息和Producer {

    /**
     * producer定制消息主要是定制Message对象，主要的是key-value对
     * producer主要可以配置路由信息：
     * 路由机制由两个参数组成：
     * 1. messageRouter 自定义路由，新建MessageRouter()方法要求实现choosePartition()
     * 方法，这个方法的入参为Message，TopicMetadata，message中包含消息的各种元数据信息，
     * topicMetadata中有topic的分区数量，使用两个对象可以自定义自己的路由机制，
     * 返回的int数据即为数据将要发送到的partition位置
     * 2. messageRoutingMode 使用系统内自带的三种路由模式
     * @throws PulsarClientException Pulsar的相关错误
     */
    @Test
    void main() throws PulsarClientException {
        // 准备Pulsar的服务url地址
        String localClusterUrl = "pulsar://172.16.2.107:6650";
        // 创建Pulsar客户端
        PulsarClient client = PulsarClient.builder().serviceUrl(localClusterUrl).build();
        // 使用Pulsar客户端创建生产者 并进行配置
        Producer<byte[]> producer = client.newProducer().topic("persistent://lx/java/test")
                .batchingMaxPublishDelay(20, TimeUnit.MILLISECONDS)
                .sendTimeout(10, TimeUnit.SECONDS)
                .blockIfQueueFull(true)
                /*.messageRouter(new MessageRouter() {
                    @Override
                    public int choosePartition(Message<?> msg, TopicMetadata metadata) {
                        return 0;
                    }
                })*/
                .messageRoutingMode(MessageRoutingMode.RoundRobinPartition)
                .create();
        // 对发送的消息进行配置
        producer.newMessage()
                .key("one")
                .value("Value %d".getBytes())
                .property("my-key", "my-value")
                .send();
        // 调用flush()方法将消息发送到Pulsar服务
        producer.flush();
        // 生产结束后关闭生产者和Pulsar client
         producer.close();
         client.close();
    }

}
