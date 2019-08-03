package Zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: Administrator
 * @Date: 2019/3/21 21:52
 * @Version: 1.0
 */
@SuppressWarnings("WeakerAccess")
public class Lock {

    private ZooKeeper zoo;
    private static String PARENT_PATH;
    private static String NODE_PATH;
    private CountDownLatch latch;
    private String nodeName;
    private String nodePath;

    /**
     * 初始化锁，
     * 1.连接zookeeper，参数放在配置文件中了，使用Properties对象去读取
     * 2.判断锁的目录是否创建了，没创建的话就创建
     */
    public Lock() {
        Properties pro = new Properties();
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("zookeeper/zoo.properties");
        try {
            pro.load(is);
            PARENT_PATH = pro.getProperty("lockZNode");
            NODE_PATH = pro.getProperty("lockZNode") + "/node";
            latch = new CountDownLatch(1);
            zoo = new ZooKeeper(pro.getProperty("hostPort"),
                    Integer.parseInt(pro.getProperty("sessionTimeOut")), new MyWatcher(latch));
            latch.await();
            Stat stat = zoo.exists(pro.getProperty("lockZNode"), false);
            if (stat == null)
                zoo.create(pro.getProperty("lockZNode"), "0".getBytes(),
                        ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            System.out.println("操作zookeeper出错");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得锁步骤：
     * 1.在PARENT_PATH目录下创建同样名字的零时顺序节点 （创建节点需要节点全路径）
     * 2.获得自己的节点名称（没有路径）
     * 3.调用lock方法
     */
    public void getLock() {
        try {
            //创建自己的节点
            nodePath =  zoo.create(NODE_PATH, "0".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            nodeName = nodePath.substring(PARENT_PATH.length() + 1);
//            System.out.println(nodeName);
//            System.out.println(nodePath);
            lock();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 真正的获得锁步骤：
     * 1.获取PARENT_PATH下所有节点，这里只能获取到节点的名称 例：node000000054
     * 2.使用自己的nodeName判断自己是不是第一个节点
     * 3.是第一个节点，获得锁
     * 4.不是第一个节点，使用exist()方法查看前一个节点是否存在，顺便新建一个Watcher监视前一个节点
     *      当前一个节点被删除时，Watcher中的process方法会被调用
     *      process方法中调用latch.countDown()唤醒线程。
     *      被唤醒的线程递归调用lock查看自己是不是第一个节点。
     *      递归的作用是防止前一个节点等待中途挂掉
     * @throws KeeperException 操作zNode可能出现的情况
     * @throws InterruptedException 被打断异常
     */
    private void lock() throws KeeperException, InterruptedException {
        List<String> nodes = zoo.getChildren(PARENT_PATH, false);
        int index = nodes.indexOf(nodeName);
        if (index == 0)
            System.out.println("获得锁");
        else{
            String preNode = nodes.get(index - 1);
            latch = new CountDownLatch(1);
            Stat stat = zoo.exists(String.format("%s/%s", PARENT_PATH, preNode), new MyWatcher(latch));
            if (stat != null)
                latch.await();
            lock();
        }
    }

    /**
     * 释放锁的步骤：
     * 1.删除自己的节点
     * 2.退出
     */
    public void release() {
        try {
            zoo.delete(nodePath, -1);
            zoo.close();
            System.out.println("释放锁");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

}
