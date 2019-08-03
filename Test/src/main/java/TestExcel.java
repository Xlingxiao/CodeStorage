import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import utils.ExcelUtil;

import java.io.IOException;
import java.util.ArrayList;

public class TestExcel {
    public static void main(String[] args) throws IOException, InvalidFormatException {
        String excelPath = "E:\\工作\\two.xlsx";
        ArrayList<ArrayList<String>> result = ExcelUtil.excelAnalysis(excelPath, 0, 4);
        for (ArrayList<String> strings : result) {
            System.out.println(strings);
        }
    }
}
