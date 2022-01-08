package current.threadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 实现线程池最简单的方法
 *
 * 使用Executors创建线程池，
 * 这里我们创建了一个最简单的线程池，它内部只有一个线程，
 * 每次需要交给线程的工作通过提交实现了Runable接口的Task对象后调用submit()方法即可
 */
public class Single {
    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newSingleThreadExecutor();
        int taskCount = 100;
        System.out.println();
        for (int i = 0; i < taskCount; i++) {
            Task task = new Task(i);
            threadPool.submit(task);
        }
        System.out.println("任务提交完毕");
    }
}
