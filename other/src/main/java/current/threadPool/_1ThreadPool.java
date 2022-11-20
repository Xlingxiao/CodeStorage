package current.threadPool;

import sun.nio.ch.ThreadPool;

import java.util.concurrent.*;

/**
 * 这里演示一个基本线程池创建的方法，和创建线程池相关的参数
 *
 * 执行示例可以看到：
 * 1. 线程池处理任务时先创建核心池大小的线程处理任务
 * 2. 核心池满了之后任务全部入队
 * 3. 队列满了之后判断是否可以创建线程池：判断当前线程数是否大于最大线程数量
 * 4. 不大于最大线程数就创建新线程处理新来的任务（注意：这里不是处理在队列中的旧任务）
 * 5. 大于等于最大线程数，执行拒绝策略
 */
public class _1ThreadPool {
    public static void main(String[] args) throws InterruptedException {
        _1ThreadPool Demo = new _1ThreadPool();
        ExecutorService threadPool = Demo.getExecutorService(1, 2, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10), new ThreadPoolExecutor.AbortPolicy());
        int taskCount = 100;
        for (int i = 0; i < taskCount; i++) {
            Task task = new Task(i);
            threadPool.submit(task);
        }
        System.out.println("任务提交完毕");
    }

    /**
     * 这里展示下创建线程池用到的参数
     * @param corePoolSize 核心池大小，
     * @param maxPoolSize 最大线程数量
     * @param keepAliveTime 线程存活时间，超过核心池的线程超过这个时间后会销毁
     * @param timeUnit 时间单位
     * @param queue 存放线程的队列
     * @param handler 线程队列满了之后怎么处理,拒绝策略有这几种
     *                AbortPolicy: 默认策略，抛出错误，拒绝入队
     *                CallerRunsPolicy： 调用上层任务对象的run方法
     *                DiscardPolicy： 忽略任务，不响应
     *                DiscardOldestPolicy： 将队列头出队（抛弃）新任务入队，
     * @return 线程池
     */
    private ExecutorService getExecutorService(int corePoolSize, int maxPoolSize, int keepAliveTime, TimeUnit timeUnit, BlockingQueue<Runnable> queue, RejectedExecutionHandler handler) {
        return new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, timeUnit, queue, handler);
    }
}
