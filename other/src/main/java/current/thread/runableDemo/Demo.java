package current.thread.runableDemo;


public class Demo implements Runnable {

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
    public void run() {
        for (int i = min; i < max; i++) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            System.out.println("线程"+Thread.currentThread().getName() + "  打印:" + i);
            System.out.printf("线程%s   打印： %d \n", Thread.currentThread().getName(), i);
        }
    }
}
