package Pulsar;

/**
 * @Author: LX
 * @Date: 2019/3/25 16:48
 * @Version: 1.0
 */

public class Pojo {
    String id;
    int age;
    String name;

    public Pojo() {
    }

    public Pojo(String id, int age, String name) {
        this.id = id;
        this.age = age;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Pojo{" +
                "id='" + id + '\'' +
                ", age=" + age +
                ", name='" + name + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
