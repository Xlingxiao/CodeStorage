package BigData.Kafka.Consumer;

import org.junit.jupiter.api.Test;
import utils.FileUtil;


/**
 * @Author: LX
 * @Date: 2019/4/11 21:14
 * @Version: 1.0
 * 将Demo1中的数据保存到一个文件，修改程序中的地址为那个文件地址
 * 即可算出消费情况
 */

@SuppressWarnings("NonAsciiCharacters")
class 消费日志分析 {

    private String[] msgCount = new String[50];
    private String[] timeCount = new String[50];

    @Test
    void main() {
        FileUtil util = new FileUtil();
        String data = util.getFileContent("D:\\LX\\log\\消费速率3", false);
        getData(data);
        long[] result = count(msgCount, timeCount);
        System.out.printf("总数据量：%d万\n", result[0] / 10000);
        System.out.printf("总时间：%d ms\n", result[1]);
        System.out.printf("平均每秒处理数据：%d \n", result[2]);
    }

    /* 总数据量：每个线程的总数据相加
     * 总用时：每个线程的总用时相加/ 线程数量
     * 平均每秒用时：总数据量 / 总用时 * 1000*/

    /*将string[] 计算为结果*/
    long[] count(String[] rowMsg, String[] rowTime) {

        long[] msgNums = new long[rowMsg.length];
        //每个线程用时
        long[] threadsTime = new long[rowTime.length];
        //结果
        long[] result = new long[3];

        //String 转 int
        for (int i = 0; i < rowMsg.length; i++) {
            if(rowMsg[i] != null)
                msgNums[i] = Long.parseLong(rowMsg[i]);
        }

        int count = 0;
        //String 转 int
        for (int i = 0; i < threadsTime.length; i++) {
            if (rowTime[i] != null) {
                count++;
                threadsTime[i] = Long.parseLong(rowTime[i]);
            }
        }


        //开始计算总数据量
        for (long msgNum : msgNums) result[0] += msgNum;

        //开始计算总时间
        for (long l : threadsTime) result[1] += l;

        result[1] = result[1] / count;
        //平均每秒处理数据量
        result[2] = result[0] / result[1] * 1000;

        return result;
    }

    /*将原始数据转为String[] 数组*/
    void getData (String data) {
        String[] strings = data.split("\n");
        int len = strings.length;

        int i = len - 1;
        int k = 0;
        for (; i >= 0; i--) {
            for (int j = 0; j < msgCount.length; j++) {
                String target = "Thread-" + j + ",";
                if (strings[i].contains(target) && msgCount[j] == null) {
                    int msgStartIndex = strings[i].indexOf("：");
                    int msgEndIndex = strings[i].indexOf("条");
                    int timeStartIndex = strings[i].indexOf("时：");
                    int timeEndIndex = strings[i].lastIndexOf("，");

                    msgCount[j] = strings[i].substring(msgStartIndex + 1, msgEndIndex);
                    timeCount[j] = strings[i].substring(timeStartIndex + 2, timeEndIndex);
                    k++;
                    break;
                }
            }
            if(k == 50) return;
        }
    }

}
