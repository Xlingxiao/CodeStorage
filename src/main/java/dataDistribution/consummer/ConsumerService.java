package dataDistribution.consummer;

import org.apache.pulsar.client.api.*;
import utils.DateUtil;

/**
 * @Author: LX
 * @Date: 2019/5/8 14:52
 * @Version: 1.0
 */
@SuppressWarnings("Duplicates")
public class ConsumerService implements Runnable {

    private String topic;
    private final DateUtil dateUtil;
    private Consumer<byte[]> consumer;
    private Producer<byte[]> producer;

    ConsumerService(DateUtil dateUtil, Consumer<byte[]> consumer, Producer<byte[]> producer) {
        this.dateUtil = dateUtil;
        this.consumer = consumer;
        this.producer = producer;
    }

    @Override

    public void run() {
        System.out.println(consumer.getConsumerName() + "已启动");
        String consumerName = consumer.getConsumerName();
        String subName = consumer.getSubscription();
        //noinspection InfiniteLoopStatement
        for (; ; ) {
            try {
                Message msg = consumer.receive();
                //消费
                consume(msg);
                //构建BackLog
                String backlog = String.format("Time:%s " +
                                "Topic:%s " + "Message ID:%s " +
                                "SubscriptionName:%s " + "ConsumerName:%s %s",
                        dateUtil.dateToString(System.currentTimeMillis()),
                        msg.getTopicName(),msg.getKey(), subName,
                        consumerName, "配置生效");
                // 使用生产者将messageId发送到log topic中
                backLog(msg.getKey(), backlog);
                //返回ack
                consumer.acknowledge(msg);
            } catch (PulsarClientException e) {
                e.printStackTrace();
            }
        }
    }

    /*模拟消费*/
    private void consume(Message msg) {
        System.out.println(String.format("%s消费了%s", msg.getTopicName(), new String(msg.getData())));
    }

    private void backLog(String msgId, String logContent) {
        producer.newMessage().key(String.valueOf(msgId))
                .value(logContent.getBytes())
                .sendAsync();
    }
}
