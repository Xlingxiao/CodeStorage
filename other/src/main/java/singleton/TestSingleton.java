package singleton;

import org.junit.jupiter.api.Test;

/**
 * @Author: LX
 * @Date: 2019/4/1 20:23
 * @Version: 1.0
 */
public class TestSingleton {
    @Test
    void main() {
        for (_6枚举 value : _6枚举.values()) {
            System.out.println(value.getName() + "  " + value.getAge());
        }
        _6枚举.INSTANCE.setName("qqq");
        for (_6枚举 value : _6枚举.values()) {
            System.out.println(value.getName() + "  " + value.getAge());
        }
        System.out.println();

    }
}
