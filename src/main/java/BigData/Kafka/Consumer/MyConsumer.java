package BigData.Kafka.Consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import utils.KafkaUtil;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;

/**
 * @program: KafkaUtil
 * @description: 单独的consumer通过设置不同的partition获取数据相互不影响
 * @author: Ling
 * @create: 2018/08/26 15:17
 **/
@SuppressWarnings("WeakerAccess")
public class MyConsumer implements Runnable {

    /**
     * id 每个消费者线程的id
     * topic 消费者消费的topic
     * partition 指定消费者消费的partition
     */

    private String topic;
    private int partition;
    private KafkaConsumer<String,String> consumer;
    private KafkaUtil kafkaUtil;

    public MyConsumer(KafkaUtil kafkaUtil, String topic, int partition) throws IOException {
        this.partition = partition;
        this.topic = topic;
        this.kafkaUtil = kafkaUtil;
    }

    @Override
    public void run() {
        createConsumer();
        startReceive();
    }

    /*配置kafka consumer*/
    private void createConsumer(){
        /*使用配置文件创建consumer*/
        consumer = kafkaUtil.getConsumer();
        /*指定consumer消费的分区
        在指定分区时分区的内容已经包括了topic所以指定分区进行消费就ok*/
        consumer.assign(Collections.singletonList(new TopicPartition(topic, partition)));
        consumer.seekToBeginning(Collections.singletonList(new TopicPartition(topic, partition)));
        //consumer.seekToEnd(Collections.singletonList(new TopicPartition(topic, partition)));
    }

    /*开始消费*/
    private void startReceive(){
        //声明records对象,kafka的消息使用这个对象进行读取
        ConsumerRecords<String,String> records;
        String name = Thread.currentThread().getName();
        long count = 0;
        long timeStart = System.currentTimeMillis();
        int times = 0;
        System.out.printf("%s已启动，消费 %s/%d 的数据\n", name, topic, partition);
        while (true){
//            每1000ms从集群中获取一次数据
            records = consumer.poll(Duration.ofMillis(500));
//            一个records中可能包含多条消息遍历这些消息
            for (ConsumerRecord record : records){
                count++;
                if (count % 100000 == 0) {
                    times++;
                    long countTimes = System.currentTimeMillis() - timeStart;
                    long avgTimes = countTimes / times;
                    System.out.printf("%s,处理了：%d条数据，总用时：%d，平均10万数据用时：%d\n",
                            name, count, countTimes, avgTimes);
                }
                //System.out.printf("Thread-%d, offset = %d, value = %s%n",this.id ,record.offset(), record.value());
            }
            consumer.commitAsync();
        }
    }
}
