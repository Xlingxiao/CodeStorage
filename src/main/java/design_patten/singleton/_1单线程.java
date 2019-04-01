package design_patten.singleton;

/**
 * @Author: LX
 * @Date: 2019/4/1 20:09
 * @Version: 1.0
 */
public class _1单线程 {
    private Pojo pojo;
    public Pojo getPojo() {
        if (pojo != null)
            pojo = new Pojo();
        return pojo;
    }
}
