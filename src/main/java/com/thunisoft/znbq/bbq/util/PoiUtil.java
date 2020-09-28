package com.thunisoft.znbq.bbq.util;

import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.util.StringUtils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author <a href="mailto:wuzhao-1@thunisoft.com>Zhao.Wu</a>
 * @description com.thunisoft.znbq.bbq.util Barbecue
 * @date 2020/9/28 0028 19:40
 */
@UtilityClass
public class PoiUtil {
    public Object getValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return Double.valueOf(cell.getNumericCellValue()).intValue();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
            case ERROR:
            case _NONE:
            default:
                return "";
        }
    }

    public List<List<String>> getLines(Sheet sheet) {
        List<List<String>> datas = new LinkedList<>();
        Iterator<Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext()) {
            List<String> rowData = new LinkedList<>();
            Row row= rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Object value = getValue(cellIterator.next());
                if (value == null) {
                    rowData.add(Strings.EMPTY);
                } else {
                    rowData.add(String.valueOf(value));
                }
            }
            datas.add(rowData);
        }
        return datas;
    }
}
