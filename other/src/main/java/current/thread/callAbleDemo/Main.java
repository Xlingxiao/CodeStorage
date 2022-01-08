package current.thread.callAbleDemo;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * 使用callable方法新建线程，实现callable接口的线程创建方式可以有返回值
 *
 * 实现两个线程打印1-200
 *
 * Callable方式和Runable：本质上Callable是在Runable的基础上进行了封装，增加了返回结果
 *
 * 不同点：
 * 1. callable可以获取线程结束后的返回结果，同时可以抛出异常
 * 2. callable可以使用其包装对象：futureTask中的方法做代理访问。
 * 3. callable使用时需要创建callable对象--》创建futureTask对象--》创建Thread--》thread.start()
 * 4. callable使用其上包装的futureTask的get()方法获取返回值，这个方法会阻塞主线程
 *
 */
public class Main {
    public static void main(String[] args) {
        Demo demo = new Demo(1, 100);
        Demo demo2 = new Demo(100, 200);
        FutureTask futureTask = new FutureTask(demo);
        FutureTask futureTask2 = new FutureTask(demo2);

        Thread thread = new Thread(futureTask);
        Thread thread2 = new Thread(futureTask2);

        thread.start();
        thread2.start();

        try {
            System.out.println(futureTask.get());
            System.out.println(futureTask2.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("线程被打断！");
        } catch (ExecutionException e) {
            e.printStackTrace();
            System.out.println("执行异常！");
        }

    }
}
