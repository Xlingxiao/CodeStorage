public class 培训 {
    public static void main(String[] args) {
        String aZfinlq7p = "_ZfinLq7p";
        char[] chars = aZfinlq7p.toCharArray();
        int v5 = 0;
        int v1 = aZfinlq7p.length() + 1;
        for (int i = 0; i < chars.length; i++) {
            chars[i] += 7;
            chars[i] ^= 0xB;
            chars[i] -= 5;
        }
        System.out.println(String.valueOf(chars));

    }

}
