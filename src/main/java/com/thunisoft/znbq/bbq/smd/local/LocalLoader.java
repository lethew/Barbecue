package com.thunisoft.znbq.bbq.smd.local;

import com.thunisoft.znbq.bbq.util.FileUtil;
import com.thunisoft.znbq.bbq.util.PoiUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 *
 * @author <a href="mailto:wuzhao-1@thunisoft.com>Zhao.Wu</a>
 * @description com.thunisoft.znbq.bbq.smd Barbecue
 * @date 2020/9/28 0028 13:54
 */
@Slf4j
public class LocalLoader {

    public static final String SYBASE_ROOT = "D:\\Document\\智能保全系统\\CD_FY_PRD_ZNBQ_DOC\\20_工程过程\\30_设计相关\\30_概要设计\\数据库设计\\sybase";
    public static final String ABASE_ROOT = "D:\\Document\\智能保全系统\\CD_FY_PRD_ZNBQ_DOC\\20_工程过程\\30_设计相关\\30_概要设计\\数据库设计\\abase";


    private final Map<String, String[]> tablesTotal = new HashMap<>(128);
    private final Map<String, String[]> colsTotal = new HashMap<>(5120);
    private final Map<String, String[]> idxTotal = new HashMap<>(2048);


    public LocalLoader(String root) {
        load(root);
    }

    private void load(String root) {
        List<File> files = FileUtil.loadFiles(new File(root), file -> file.getName().endsWith(".xls"));
        for (File file : files) {
            try(InputStream inputStream = new FileInputStream(file)) {
                Workbook workbook1 = new HSSFWorkbook(inputStream);
                tablesTotal.putAll(getStringMap(workbook1, "TAB",2, 1, 1, 2));
                colsTotal.putAll(getStringMap(workbook1, "COL",4, 2, 0, 1));
                idxTotal.putAll(getStringMap(workbook1, "INDEX",3, 2, 0, 1));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }


    public String[] getTable(String key) {
        return tablesTotal.get(key);
    }

    public String[] getCol(String key) {
        return colsTotal.get(key);
    }

    public String[] getIndex(String key) {
        return idxTotal.get(key);
    }

    private static String line(String[] strs) {
        StringBuilder builder = new StringBuilder();
        for (String str : strs) {
            builder.append(str).append("\t");
        }
        return builder.toString();
    }

    private static Map<String, String[]> getStringMap(Workbook workbook, String sheetName, int s1, int j1, int i1, int i2) {
        Map<String, String[]> items = new TreeMap<>();
        Sheet tab = workbook.getSheet(sheetName);
        if (tab == null) {
            return items;
        }
        Iterator<Row> rowIterator = tab.rowIterator();
        int i = 0;
        while (rowIterator.hasNext()) {
            if (i++ < s1) {
                continue;
            }
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            List<String> values = new LinkedList<>();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                Object value = PoiUtil.getValue(cell);
                values.add(String.valueOf(value));
            }
            String[] strs = values.toArray(new String[0]);
            if (strs.length > j1 && StringUtils.hasText(strs[j1]) && !strs[i1].equals("表名")) {
                String key = strs[i1] + strs[i2];
                items.put(key, strs);
            }
        }
        return items;
    }


}
