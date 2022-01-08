package current.thread.callAbleDemo;

import java.util.concurrent.Callable;

public class Demo implements Callable {

    int min;
    int max;

    public Demo(int min, int max) {
        if (min > max) {
            int s = max;
            max = min;
            min = s;
        }
        this.min = min;
        this.max = max;
    }

    @Override
    public String call() throws Exception {
        for (int i = 0; i < max; i++) {
            Thread.sleep(10);
            System.out.printf("线程{%s},打印：{%d}\n", Thread.currentThread().getName(), i);
        }

        return String.format("%s 任务执行完毕！", Thread.currentThread().getName());
    }
}
