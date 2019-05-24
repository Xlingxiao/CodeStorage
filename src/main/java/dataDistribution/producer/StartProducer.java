package dataDistribution.producer;

import BigData.Pulsar.admin.LXAdmin;
import dataDistribution.mybatis.mapper.MessageStatusMapper;
import dataDistribution.mybatis.pojo.MessageStatus;
import dataDistribution.mybatis.pojo.TopicStatus;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.client.api.Producer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import utils.PropertiesUtil;
import utils.PulsarUtil;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author: LX
 * @Date: 2019/4/25 17:20
 * @Version: 1.0
 * 启动生产者
 */

public class StartProducer {

    private static Properties pulsarProperties;

    private StartProducer() throws IOException {
    }

    public static void main(String[] args) throws IOException, PulsarAdminException, InterruptedException {
        String path = "applicationContext.xml";
        ApplicationContext ctx = new ClassPathXmlApplicationContext(path);
        MessageStatusMapper messageStatusMapper = (MessageStatusMapper) ctx.getBean("messageStatusMapper");
        StartProducer start = new StartProducer();
//        start.processProperties(messageStatusMapper);
        start.processAllTopic(messageStatusMapper);

    }

    /*=======================一个线程一个producer========================*/

    /*处理配置文件中的topic*/
    private void processProperties(MessageStatusMapper messageStatusMapper) throws IOException {
        String[] topics = getTopics();
        for (String topic : topics) {
            PulsarService pulsarService = new PulsarService(topic, pulsarProperties, messageStatusMapper);
            new Thread(pulsarService).start();
        }
    }

    /*获取配置文件中所有topic*/
    private  String[] getTopics() throws IOException {
        pulsarProperties = new PropertiesUtil().getProperties("pulsar/pulsar.properties");
        String row = pulsarProperties.getProperty("producerMultiTopic");
        return row.split(",");
    }


    /*==========================将producer放到map中，根据topic选择producer=========================*/
    private LXAdmin pulsarAdmin = getAdmin();
    private PulsarUtil util = new PulsarUtil();
    private Map<String, Producer<byte[]>> producerMap;
    //保存topic没有找到producer的次数
    private Map<String, Long> notFountTimes;
    //topic未找到多少次刷新producerMap
    private long timesLine;
    //读取什么状态的数据
    private String loadStatus;
    //将数据处理后数据状态改为什么
    private String uploadStatus;
    //每次从数据库中取多少数据
    private int getDataBaseSize;
    //每隔多少秒从数据库获取数据
    private int getDataBaseInterval;

    /*初始化所有参数*/
    private void init() throws IOException, PulsarAdminException {
        pulsarAdmin = getAdmin();
        util = new PulsarUtil();
        producerMap = new HashMap<>();
        getAllProduce();
        notFountTimes = new HashMap<>();
        pulsarProperties = new PropertiesUtil().getProperties("pulsar/pulsar.properties");
        timesLine = Long.parseLong(pulsarProperties.getProperty("producerNotFoundReload"));
        loadStatus = pulsarProperties.getProperty("producerLoadStatus");
        uploadStatus = pulsarProperties.getProperty("producerUploadStatus");
        getDataBaseSize = Integer.parseInt(pulsarProperties.getProperty("getDataBaseSize"));
        getDataBaseInterval = Integer.parseInt(pulsarProperties.getProperty("getDataBaseInterval"));
    }


    /*处理所有topic中发送消息*/
    @SuppressWarnings({"InfiniteLoopStatement", "unchecked"})
    private void processAllTopic(MessageStatusMapper messageMapper) throws IOException, PulsarAdminException, InterruptedException {
        init();
        while (true) {
            //从数据获取消息
            List<MessageStatus> myMessages = messageMapper.getByStatus(loadStatus, getDataBaseSize);
            for (MessageStatus myMessage : myMessages) {
                //获得topic并根据topic获取对应的producer
                String topic = myMessage.getTopic();
                Producer producer = producerMap.get(topic);
                //producer为空获取producer为空的次数
                if (producer == null) {
                    producer = notFoundProducerProcess(topic, timesLine);
                    if(producer == null) continue;
                }
                //将数据发送到pulsar
                producer.newMessage().key(String.valueOf(myMessage.getId())).value(myMessage.getContent().getBytes()).sendAsync();
                //将状态向数据库进行更新
                myMessage.setStatus(uploadStatus);
                myMessage.setModifyTime(new Date(System.currentTimeMillis()));
                messageMapper.updateByPrimaryKey(myMessage);
                System.out.println(String.format("处理了：%s", myMessage.getContent()));
            }
            TimeUnit.SECONDS.sleep(getDataBaseInterval);
        }

    }

    /*没有找到producer之后的处理*/
    private Producer notFoundProducerProcess(String topic,long timesLine) throws IOException, PulsarAdminException {
        Producer producer = null;
        //putIfAbsent 存在key则相当于get key不存在相当于put
        Long times = notFountTimes.putIfAbsent(topic, (long) 1);
        if (times != null) {
            if (times < timesLine) {
                notFountTimes.put(topic, times + 1);
            }else {
                producerMap = getAllProduce();
                producer = producerMap.get(topic);
                notFountTimes.put(topic, (long) 0);
            }
        }
        return producer;
    }

    /*获取producer的map*/
    private Map<String, Producer<byte[]>> getAllProduce() throws IOException, PulsarAdminException {
        List<TopicStatus> topicList = pulsarAdmin.getAllTopicStatus();
        for (TopicStatus topicStatus : topicList) {
            int partitions = topicStatus.getPartitions();
            for (int i = 0; i < partitions; i++) {
                String topic = topicStatus.getName() + "-partition-" + i;
                Producer<byte[]> producer = producerMap.get(topic);
                if (producer == null) {
                    producer = util.getProducer(topic);
                    producerMap.put(topic, producer);
                    System.out.println(String.format("处理：[%s]的数据", topic));
                }
            }
        }
        return producerMap;
    }

    /*获取所有获取Pulsar管理资源*/
    private LXAdmin getAdmin() throws IOException {
        PropertiesUtil propertiesUtil = new PropertiesUtil();
        return new LXAdmin(propertiesUtil);
    }

}
