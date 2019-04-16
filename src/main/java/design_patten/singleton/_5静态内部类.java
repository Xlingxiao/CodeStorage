package design_patten.singleton;

/**
 * @Author: LX
 * @Date: 2019/4/1 20:19
 * @Version: 1.0
 */
public class _5静态内部类 {

    public Pojo getPojo() {
        return Inner.pojo;
    }

    private static class Inner{
        private static Pojo pojo = new Pojo();
    }
}
