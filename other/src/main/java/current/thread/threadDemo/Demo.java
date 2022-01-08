package current.thread.threadDemo;

public class Demo extends Thread {

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
            System.out.println(i);
        }
    }
}
