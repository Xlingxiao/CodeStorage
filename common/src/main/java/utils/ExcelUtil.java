package utils;

import lombok.Getter;
import lombok.Setter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtil {

    private static ExcelUtil excelUtil = new ExcelUtil();
    /**
     * 将excel文件转为二维列表
     * @param excelPath excel 文件路径
     * @param sheetNum excel文件中的表格编号，从0开始
     * @param skipLineNum 表格中需要跳过的行数
     * @return 二维列表
     * @throws IOException 找不到文件
     * @throws InvalidFormatException 。。
     */
    public static ArrayList<ArrayList<String>> excelAnalysis(String excelPath,int sheetNum, int skipLineNum) throws IOException, InvalidFormatException {
        File excel = new File(excelPath);
        if (!excel.isFile() || !excel.exists()) return null;
        //根据文件后缀（xls/xlsx）进行判断
        String[] split = excel.getName().split("\\.");  //.是特殊字符，需要转义！！！！！
        Workbook wb;
        if ("xls".equals(split[1])) {
            FileInputStream fis = new FileInputStream(excel);   //文件流对象
            wb = new HSSFWorkbook(fis);
        } else if ("xlsx".equals(split[1])) {
            wb = new XSSFWorkbook(excel);
        } else {
            System.out.println("文件类型错误!");
            return null;
        }

        //开始解析
        Sheet sheet = wb.getSheetAt(sheetNum);
        //跳过前n行
        int firstRowIndex = sheet.getFirstRowNum() + skipLineNum;
        int lastRowIndex = sheet.getLastRowNum();
        System.out.println("首行: " + firstRowIndex);
        System.out.println("末行: " + lastRowIndex);
        excelUtil.getAllMergedCell(sheet);
        return excelUtil.worker(sheet, firstRowIndex, lastRowIndex);
    }

    /*根据cell获取所在的合并单元格的值*/
    private String getCellContain(Cell cell) {
        int row = cell.getRowIndex();
        int column = cell.getColumnIndex();
        for (MyCell bean : mergedList) {
            if (bean.firstRow <= row && bean.lastRow >= row) {
                if (bean.firstColumn <= column && bean.lastColumn >= column) {
                    return bean.content;
                }
            }
        }
        return null;
    }

    /*获得所有合并单元格所占的区域*/
    private List<MyCell> mergedList = new ArrayList<>();
    private void getAllMergedCell(Sheet sheet) {
        int count = sheet.getNumMergedRegions();
        for (int i = 0; i < count; i++) {
            CellRangeAddress rangeAddress = sheet.getMergedRegion(i);
            MyCell bean = new MyCell();
            bean.firstColumn = rangeAddress.getFirstColumn();
            bean.lastColumn = rangeAddress.getLastColumn();
            bean.firstRow = rangeAddress.getFirstRow();
            bean.lastRow = rangeAddress.getLastRow();
            bean.content = sheet.getRow(bean.firstRow).getCell(bean.firstColumn).getStringCellValue();
            mergedList.add(bean);
        }
    }

    /*开始遍历,获得people对象*/
    private ArrayList<ArrayList<String>> worker(Sheet sheet, int firstRowIndex, int lastRowIndex) {
        ArrayList<ArrayList<String>> rows = new ArrayList<>();
        for (int rIndex = firstRowIndex; rIndex <= lastRowIndex; rIndex++) {   //遍历行
            Row row = sheet.getRow(rIndex);
            if (row != null) {
                int firstCellIndex = row.getFirstCellNum();
                int lastCellIndex = row.getLastCellNum();
                ArrayList<String> infos = new ArrayList<>();
                for (int cIndex = firstCellIndex; cIndex < lastCellIndex; cIndex++) {   //遍历列
                    Cell cell = row.getCell(cIndex);
                    if (cell != null) {
                        String content = cell.toString();
                        if (content.equals("")) {
                            content = getCellContain(cell);
                        }
                        infos.add(content);
                    }
                }
                rows.add(infos);
            }
        }
        return rows;
    }

    @Getter
    @Setter
    private class MyCell{
        String content;
        int firstColumn ;
        int lastColumn ;
        int firstRow ;
        int lastRow;
    }

}
