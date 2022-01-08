package current.thread.threadDemo;

/**
 * 多个线程同时打印，总共打印两百个数字
 *
 * Thread对象内部是实现了Runable对象的run方法
 * 使用时调用Thread对象的start()方法即能创建线程执行
 */
public class Main {

    public static void main(String[] args) {
        Demo demo = new Demo(0, 100);
        Demo demo2 = new Demo(100, 200);
        demo.start();
        demo2.start();
    }
}
