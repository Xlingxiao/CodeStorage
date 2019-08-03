package singleton;

/**
 * @Author: LX
 * @Date: 2019/4/1 20:14
 * @Version: 1.0
 */
public class _3加锁 {

    private static Pojo pojo;

    public synchronized Pojo getPojo() {
        if (pojo==null) pojo = new Pojo();
        return pojo;
    }

}
