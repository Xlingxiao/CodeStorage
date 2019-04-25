package dataDistribution.producer;

import dataDistribution.mybatis.mapper.MessageStatusMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import utils.PropertiesUtil;

import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

/**
 * @Author: LX
 * @Date: 2019/4/25 17:20
 * @Version: 1.0
 * 启动生产者
 */

public class StartProducer {

    private static Properties pulsarProperties;

    public static void main(String[] args) throws IOException {
        String path = "applicationContext.xml";
        ApplicationContext ctx = new ClassPathXmlApplicationContext(path);
        MessageStatusMapper messageStatusMapper = (MessageStatusMapper) ctx.getBean("messageStatusMapper");
        String[] topics = getTopics();
        for (String topic : topics) {
            PulsarService pulsarService = new PulsarService(topic, pulsarProperties, messageStatusMapper);
            new Thread(pulsarService).start();
        }
        Scanner sc = new Scanner(System.in);
        sc.nextInt();
    }

    /*获取所有topic*/
    private static String[] getTopics() throws IOException {
        pulsarProperties = new PropertiesUtil().getProperties("pulsar/pulsar.properties");
        String row = pulsarProperties.getProperty("producerMultiTopic");
        return row.split(",");
    }

}
