package singleton;

@SuppressWarnings("NonAsciiCharacters")
public enum _6枚举 {
    INSTANCE(1, "lx"),
    TWO(2, "lx2");
    private int age;
    private String name;

    _6枚举(int age, String name) {
        this.age = age;
        this.name = name;
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
