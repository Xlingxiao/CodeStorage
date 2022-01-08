package current.threadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 这里演示的是创建基于无界队列的线程池，理论上内存有多大就可以创建多少线程。
 * 本质是创建了一个核心池子为0，任务队列大小为int最大值的一个队列，线程超时销毁时间为 60 秒
 */
public class Cached {
    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        int taskCount = 100;
        System.out.println();
        for (int i = 0; i < taskCount; i++) {
            Task task = new Task(i);
            threadPool.submit(task);
        }
        System.out.println("任务提交完毕");
    }
}
