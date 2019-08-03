package singleton;

/**
 * @Author: LX
 * @Date: 2019/4/1 20:16
 * @Version: 1.0
 */
public class _4优化加锁 {
    private Pojo pojo;

    public Pojo getPojo() {
        if (pojo == null)
            synchronized (this) {
                if (pojo == null)
                    pojo = new Pojo();
            }
        return pojo;
    }
}
