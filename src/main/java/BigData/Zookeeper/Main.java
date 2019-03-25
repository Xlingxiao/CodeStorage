package BigData.Zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Administrator
 * @Date: 2019/3/21 22:42
 * @Version: 1.0
 */
public class Main {
    //测试分布式锁，开十个线程，分别去抢锁，然后打印消息
    @Test
    void main() throws InterruptedException {
        int len = 10;
        final CountDownLatch latch = new CountDownLatch(len);
        for (int i = 0; i < len; i++) {
            new Thread(new Runnable() {
                public void run() {
                    Lock myLock = new Lock();

                    myLock.getLock();
                    System.out.println(String.format("%s 获得锁啦", Thread.currentThread().getName()));
                    System.out.println("做自己的事");
                    try {
                        TimeUnit.MILLISECONDS.sleep(1000);
                        System.out.println("事情做完啦");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    latch.countDown();
                    myLock.release();
                }
            }).start();
        }
        latch.await();
        System.out.println("所有事都做完了");
    }


    @Test
    void test(){
        String path = "/tmp/node";
        CountDownLatch latch = new CountDownLatch(1);
        try {
            ZooKeeper zookeeper = new ZooKeeper("172.16.2.107:2182", 3000, new MyWatcher(latch));
            zookeeper.create(path, "0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            zookeeper.create(path, "0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            latch = new CountDownLatch(1);
            List<String> list = zookeeper.getChildren("/tmp", false);
            System.out.println(list);
            Stat stat = zookeeper.exists(path, new MyWatcher(latch));

            if (stat != null)
                latch.await();
            System.out.println("获取锁");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
