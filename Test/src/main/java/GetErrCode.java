import org.springframework.util.StringUtils;
import utils.FileUtil;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author LX
 * @description 获取错误码和错误信息
 * @date 2020/7/15 16:02
 */
public class GetErrCode {

    private static Pattern pattern = Pattern.compile("(?<=com.brons.wt.interceptor.AuthInterceptor - ).+");

    public static void main(String[] args) {
        GetErrCode getErrCode = new GetErrCode();
        int lineNum = 6;
        FileUtil fileUtil = new FileUtil();
        String fileContent = fileUtil.getFileContent("e:/Download/auth_error.out", false);
        Queue<String> queue = new ArrayDeque<>(lineNum);
        String[] lines = fileContent.split("\n");
        List<String> errLines = new ArrayList<>(10000);
        for (String line : lines) {
            int i;
            if ((i = line.indexOf("com.brons.wt.interceptor.AuthInterceptor")) != -1) {
                errLines.add(line.substring(0, i + 3));
            }
            /*queue.add(line);
            // 定位到报错的行
            if (line.contains("wt_wt")) {
                for (int j = 0; j < lineNum; j++) {
                    errLines.add(queue.poll());
                }
            }
            if (queue.size() >= lineNum) {
                queue.poll();
            }*/
        }
        for (String errLine : errLines) {
            if (errLine.contains("-------权限过滤器---启动---")) {

            }
        }
        StringBuilder sb = new StringBuilder();
        for (String errLine : errLines) {
            if (StringUtils.isEmpty(errLine)) {
                continue;
            }
            Matcher matcher = pattern.matcher(errLine);
            if (matcher.find()) {
                sb.append(matcher.group()).append("\n");
            }
//            sb.append(errLine).append("\n");
        }
//        fileUtil.writeString(sb.toString(),"e:/Download/error.json");
        System.out.println(sb.toString());
    }


}
