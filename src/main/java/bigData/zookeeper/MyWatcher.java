package bigData.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;

/**
 * @Author: Administrator
 * @Date: 2019/3/21 21:58
 * @Version: 1.0
 */
public class MyWatcher implements Watcher {
    private CountDownLatch latch;

    public MyWatcher(CountDownLatch latch) {
        this.latch = latch;
    }

    public void process(WatchedEvent watchedEvent) {
        latch.countDown();
    }
}
