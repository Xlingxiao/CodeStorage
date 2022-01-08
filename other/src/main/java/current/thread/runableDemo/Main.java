package current.thread.runableDemo;

/**
 * 多个线程同时打印，打印两百个数
 *
 * Runable方法实现多线程时：
 * 1. 新建Runable对象-->新建Thread对象-->thread.start()
 * 2. Thread对象本身也是实现了Runable对象的run接口
 *
 * 题外话：主线程可以使用 thread.join()等待指定线程执行完毕；
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        Demo demo = new Demo(1, 100);
        Demo demo2 = new Demo(100, 200);
        Thread thread = new Thread(demo);
        Thread thread2 = new Thread(demo2);
        thread.start();
        thread2.start();

        // 主线程使用 join()方法等待线程执行完毕后join主线程，这里会阻塞
        thread.join();
        thread2.join();
        System.out.println("所有线程都执行完毕啦！");
    }
}
