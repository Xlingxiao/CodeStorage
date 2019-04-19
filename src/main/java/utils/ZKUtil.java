package utils;

import org.apache.zookeeper.*;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

/**
 * 测试Zookeeper提供的Java Api
 */
@SuppressWarnings({"WeakerAccess", "Duplicates"})
public class ZKUtil {

    private ZooKeeper zoo;
    private static final Logger logger = Logger.getLogger("Zookeeper");
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static PropertiesUtil propertiesUtil;
    private String zkServerAddr;

    public ZKUtil() throws IOException {
        propertiesUtil = new PropertiesUtil();
        initZoo();
    }

    public ZKUtil(PropertiesUtil util) throws IOException {
        propertiesUtil = util;
        initZoo();
    }

    /*获得zookeeper连接对象*/
    void initZoo() throws IOException {
        Properties pro = propertiesUtil.getProperties("zookeeper/zoo.properties");
        zkServerAddr = pro.getProperty("zkServerAddr");
        zoo = new ZooKeeper(pro.getProperty("zkServerAddr"),
                Integer.parseInt(pro.getProperty("sessionTimeOut")), new myWatcher());
    }

    @Test
    void main() throws IOException, InterruptedException {
        propertiesUtil = new PropertiesUtil();
        initZoo();
        System.out.println(zoo.getState());
        connectedSemaphore.await();
        String path = "/JavaTest";
        String data = "Hello World";
        createNode(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println(getZNodeContent(path));
        setNode(path, "HELLO");
        System.out.println(getZNodeContent(path));
        deleteNode(path);
        System.out.println(getZNodeContent(path));
    }

    public String getZkServerAddr(){
        return zkServerAddr;
    }

    /*节点是否存在*/
    public Stat exists(String path) throws KeeperException, InterruptedException {
        return zoo.exists(path, false);
    }

    /*创建节点*/
    public void createNode(String path, String data, List<ACL> acl, CreateMode createMode) {
        String returnPath = null;
        try {
            returnPath = zoo.create(path, data.getBytes(), acl, createMode);
        } catch (KeeperException e) {
            System.out.println(String.format("创建节点失败：%s", path));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info(String.format("创建节点成功：%s，data:%s", returnPath, data));
    }

    /*获得节点内容*/
    public String getZNodeContent(String path) {
        String msg = null;
        try {
            msg = new String(zoo.getData(path, false, exists(path)));
        } catch (KeeperException e) {
            System.out.println("没有找到节点");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return msg;
    }

    /*获得节点的所有子节点*/
    public List<String> getChildren(String path) throws KeeperException, InterruptedException {
        return zoo.getChildren(path, false);
    }

    /*更新节点值*/
    public String setNode(String path, String data) {
        Stat stat  = null;
        try {
            stat = exists(path);
        } catch (KeeperException e) {
            System.out.println("没有找到节点，无法设置值");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(stat == null)return null;
        try {
            int v = stat.getVersion();
            zoo.setData(path, data.getBytes(), v);
            System.out.println("更新成功");
            return getZNodeContent(path);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return getZNodeContent(path);
    }

    /*删除节点*/
    public String deleteNode(String path) {
        Stat stat = null;
        try {
            stat = exists(path);
        } catch (KeeperException e) {
            System.out.println("没有找到节点，无法删除");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (stat == null) return null;
        String msg = getZNodeContent(path);
        try {
            zoo.delete(path, stat.getVersion());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
        System.out.println(String.format("删除成功：%s", path));
        return path;
    }

    /*创建一个Watcher对某个节点进行监控*/
    private class myWatcher implements Watcher{
        public void process(WatchedEvent watchedEvent) {
//            System.out.println("Receive watched event : " + watchedEvent);
            if (KeeperState.SyncConnected == watchedEvent.getState()) {
                connectedSemaphore.countDown();
            }
        }
    }

}
