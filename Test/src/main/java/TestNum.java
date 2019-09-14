import java.text.DecimalFormat;
import java.util.Scanner;

public class TestNum {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String row = sc.nextLine();
        double d = Double.parseDouble(row);
        DecimalFormat df = new DecimalFormat("#.00");
        String dd = df.format(d);
        System.out.println(row);
        System.out.println(d);
        System.out.println(dd);
    }
}
