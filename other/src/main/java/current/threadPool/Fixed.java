package current.threadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 这里创建的是一个固定大小的线程池，这个线程池里核心池大小和最大线程大小相同，
 * 使用的队列是LinkedBlockingQueue，
 * 导致的结果就是任务数量超过核心池大小后任务会全部存到队列中,直到任务执行完毕
 */
public class Fixed {
    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        int taskCount = 100;
        System.out.println();
        for (int i = 0; i < taskCount; i++) {
            Task task = new Task(i);
            threadPool.submit(task);
        }
        System.out.println("任务提交完毕");
    }
}
