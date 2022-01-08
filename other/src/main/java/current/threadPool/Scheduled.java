package current.threadPool;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * 计划线程池，在收到任务后会等待一定时间之后执行任务。
 * 本质是使用了延时队列，将发过去的任务延时执行。
 */
public class Scheduled {
    public static void main(String[] args) {
        ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(2);
        System.out.println("定时任务启动");
        while (true) {
            Task task = new Task(1);
            threadPool.schedule(task, 3, TimeUnit.SECONDS);
        }
    }
}
