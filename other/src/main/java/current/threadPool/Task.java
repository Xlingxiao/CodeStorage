package current.threadPool;

/**
 * 用来做线程池的任务
 */
public class Task implements Runnable{

    int num;

    public Task(int num) {
        this.num = num;
    }

    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        System.out.printf("%s开始工作,任务编号：%d\n", name, num);
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("%s工作完成,任务编号：%d\n", name, num);
    }
}
