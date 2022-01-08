package utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: LX
 * @Date: 2019/4/22 16:52
 * @Version: 1.0
 */
@SuppressWarnings("WeakerAccess")
public class DateUtil {

    private static final SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat sdfMinute = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final SimpleDateFormat sdfSecond = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");



    void main() throws ParseException {
        String dateString = "2010-01-01 00:00";
        Date date2000 = stringToDate(dateString);
        System.out.println(date2000);
        System.out.println(date2000.getTime());
        long SYSTEM_START_TIME = 946656000000L;
        Date date = new Date(SYSTEM_START_TIME);
        System.out.println(date);
        long qqq = System.currentTimeMillis() - SYSTEM_START_TIME;
        System.out.println(qqq);
    }

    /*String 转Date*/
    public Date stringToDate(String dateStr) throws ParseException {
        if (dateStr.contains(" ") && dateStr.contains(":")) {
            String tmp = dateStr.split(" ")[1];
            String[] dates = tmp.split(":");
            if(dates.length == 2) return sdfMinute.parse(dateStr);
            if(dates.length == 3) return sdfSecond.parse(dateStr);
        }
        return sdfDay.parse(dateStr);
    }

    public String dateToString(long time) {
        return sdfSecond.format(new Date(time));
    }
}
