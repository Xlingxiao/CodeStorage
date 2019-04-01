package design_patten.singleton;

/**
 * @Author: LX
 * @Date: 2019/4/1 20:12
 * @Version: 1.0
 */
public class _2多线程饿汉式 {
    private static final Pojo pojo = new Pojo();

    public Pojo getPojo() {
        return pojo;
    }
}
