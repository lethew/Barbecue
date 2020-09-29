package com.thunisoft.znbq.bbq.smd.generator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thunisoft.znbq.bbq.smd.consts.DatabaseType;
import com.thunisoft.znbq.bbq.smd.local.Group;
import com.thunisoft.znbq.bbq.smd.local.LocalLoader;
import com.thunisoft.znbq.bbq.smd.local.Smd;
import com.thunisoft.znbq.bbq.smd.local.Table;
import com.thunisoft.znbq.bbq.smd.model.DatabaseInfo;
import com.thunisoft.znbq.bbq.smd.model.DatabaseSnapshot;
import com.thunisoft.znbq.bbq.smd.model.TableFilter;
import com.thunisoft.znbq.bbq.util.ConnectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:wuzhao-1@thunisoft.com>Zhao.Wu</a>
 * @description com.thunisoft.znbq.bbq.smd Barbecue
 * @date 2020/9/27 0027 17:11
 */
@Slf4j
public class SmdFileGenerator {

    private static class GroupListTypeReference extends TypeReference<List<Group>> {}


    public static void main(String[] args) {
        Arrays.stream(Smd.values()).parallel().forEach(smd -> {
            try {
                buildSmd(smd);
            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public static String buildSmd(DatabaseSnapshot snapshot, DatabaseInfo databaseInfo, List<TableFilter> filters) {
        String dir = snapshot.getSystemName()+"/"+snapshot.getDatabaseType()+"/"+snapshot.getVersion();
        Consumer<Connection> consumer = connection -> {
            try {
                DatabaseMetaData metaData = connection.getMetaData();
                List<Group> groups = getGroupByFilter(databaseInfo, filters);
                File dest = new File(dir);
                if(dest.mkdirs()) {
                    for (Group group : groups) {
                        buildByGroup(metaData, dir, group, null);
                    }
                }

            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        };
        DatabaseType.executeConsumer(databaseInfo, consumer);
        return dir;
    }

    private static List<Group> getGroupByFilter(DatabaseInfo databaseInfo, List<TableFilter> filters) {
        if (CollectionUtils.isEmpty(filters)) {
            List<Group> groups = new LinkedList<>();
            Consumer<Connection> consumer = connection -> {
                try {
                    DatabaseMetaData metaData = connection.getMetaData();
                    ResultSet catalogs = metaData.getCatalogs();
                    while (catalogs.next()) {
                        String tableCat = catalogs.getString("TABLE_CAT");
                        ResultSet tables = metaData.getTables(tableCat, null, null, new String[]{"TABLE"});
                        List<Table> tableList = new LinkedList<>();
                        while (tables.next()) {
                            String name = tables.getString("TABLE_NAME");
                            String remarks = tables.getString("REMARKS");
                            tableList.add(new Table(tableCat, name, remarks));
                        }
                        groups.add(new Group("AUTO_"+tableCat, tableList));
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            };

            DatabaseType.executeConsumer(databaseInfo, consumer);
            return groups;
        }

        return filters.stream()
                .collect(Collectors.groupingBy(TableFilter::getGroupName))
                .entrySet().stream()
                .map(e -> {
                    List<Table> tables = e.getValue().stream()
                            .map(filter -> new Table(filter.getTableCat(), filter.getTableName(), filter.getDesc()))
                            .collect(Collectors.toList());
                    return new Group(e.getKey(), tables);
                }).collect(Collectors.toList());
    }


    @Deprecated
    private static void buildSmd(Smd smd) throws SQLException, ClassNotFoundException {
        Consumer<Connection> consumer = connection -> {
            try {
                DatabaseMetaData metaData = connection.getMetaData();
                LocalLoader localLoader = new LocalLoader(smd.getSrc());
                List<Group> groups = getGroups(smd);
                String dir = smd.getDesc();
                File dest = new File(dir);
                if(dest.mkdirs()) {
                    for (Group group : groups) {
                        buildByGroup(metaData, dir, group, localLoader);
                    }
                }

            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

        };

        switch (DatabaseType.codeOf(smd.getType())) {
            case SYBASE: ConnectionUtil.sybase("172.18.17.186:5000","YWST",
                    "npcx", "qwer1234", consumer); break;
            case ABASE: ConnectionUtil.abase("172.25.17.63:6543","znbq",
                    "znbq", "123456", consumer); break;
            default: throw new UnsupportedOperationException("不支持的数据库类型");
        }

    }



    private static void buildByGroup(DatabaseMetaData metaData, String dir, Group group, LocalLoader localLoader) throws SQLException, IOException {
                HSSFWorkbook workbook = new HSSFWorkbook();
                buildLogListSheet(workbook);
                buildTableSheet(workbook, metaData, group.getTables(), localLoader);
                buildColSheet(workbook, metaData, group.getTables(), localLoader);
                buildIndexSheet(workbook, metaData, group.getTables(), localLoader);
                buildConstraintsSheet(workbook);
                workbook.write(new File(dir+"/"+group.getPath()+".xls"));
    }

    private static List<Group> getGroups(Smd smd) throws IOException {
        List<Group> groups;
        try(InputStream inputStream = SmdFileGenerator.class.getResourceAsStream(smd.getPath())) {
            ObjectMapper objectMapper = new ObjectMapper();
            groups = objectMapper.readValue(inputStream, new GroupListTypeReference());
        }
        return groups;
    }


    private static final String[] TAB_TITLES = {"表中文名","表名","存储空间","单独大对象表空间","数据缓存","记录事务日志","分区","JAVA类名","主键类名","版本","业务注释","额外XML","说明"};
    private static final String[] COL_TITLES = {"表名","字段","字段中文名","主键","默认值","不为空","数据类型","数据长度","数据精度","字段类型","代码类型","版本","说明"};
    private static final String[] INDEX_TITLES = {"表名","索引名","索引类型","字段名","存储空间","单独大对象空间","记录事务日志","说明"};
    private static final String[] CONSTRAINTS_TILES = {"表名", "约束名", "约束类型", "字段名", "参照表", "参照字段", "说明"};

    private static void buildConstraintsSheet(HSSFWorkbook workBook) {
        HSSFCellStyle htCellStyle = getHtHssfCellStyle(workBook);
        HSSFCellStyle stCellStyle = getStHssfCellStyle(workBook);
        HSSFSheet sheet = workBook.createSheet("CONSTRAINTS");
        sheet.setDefaultRowHeightInPoints(20);
        sheet.setDefaultColumnWidth(20);
        titleRow(sheet, htCellStyle, CONSTRAINTS_TILES);

        endRow(sheet, stCellStyle, CONSTRAINTS_TILES, CONSTRAINTS_TILES);
    }



    private static void buildIndexSheet(HSSFWorkbook workBook, DatabaseMetaData metaData, List<com.thunisoft.znbq.bbq.smd.local.Table> tables, LocalLoader localLoader) throws SQLException {
        HSSFCellStyle htCellStyle = getHtHssfCellStyle(workBook);
        HSSFCellStyle stCellStyle = getStHssfCellStyle(workBook);
        HSSFSheet sheet = workBook.createSheet("INDEX");
        sheet.setDefaultRowHeightInPoints(20);
        sheet.setDefaultColumnWidth(20);
        titleRow(sheet, htCellStyle, INDEX_TITLES);

        Row nameRow = sheet.createRow(2);
        nameRow.setHeightInPoints(20);
        for (int i = 0; i < INDEX_TITLES.length; i++) {
            Cell cell = nameRow.createCell(i);
            String value = "";
            if (i == 0) {
                value = "----";
            } else if (i==1) {
                value = "外键索引";
            }
            cell.setCellStyle(stCellStyle);
            cell.setCellValue(value);
        }

        for (com.thunisoft.znbq.bbq.smd.local.Table table : tables) {
            ResultSet indexInfo = metaData.getIndexInfo(table.getCat(), null, table.getName(), false, false);
            while (indexInfo.next()) {
                String[] lines = {table.getName(),
                        indexInfo.getString("INDEX_NAME"),
                        indexInfo.getString("TYPE"),
                       indexInfo.getString("COLUMN_NAME"),
                        indexInfo.getString("TABLE_CAT"),
                        "1..是","0..否",""};
                if (!StringUtils.hasText(lines[1])) {
                    continue;
                }
                toUppercase(lines);
                if (localLoader != null) {
                    String key = lines[0] + lines[1];
                    String[] index = localLoader.getIndex(key);
                    if (index != null) {
                        lines = index;
                    }
                }
                HSSFRow row = sheet.createRow(sheet.getLastRowNum() + 1);
                row.setHeightInPoints(20);
                for (int i = 0; i < INDEX_TITLES.length; i++) {
                    HSSFCell cell = row.createCell(i);
                    cell.setCellStyle(stCellStyle);
                    cell.setCellValue(lines[i]);
                }
            }

        }
        endRow(sheet, stCellStyle, INDEX_TITLES, INDEX_TITLES);
    }

    private static void buildColSheet(HSSFWorkbook workBook, DatabaseMetaData metaData, List<com.thunisoft.znbq.bbq.smd.local.Table> tables, LocalLoader localLoader) throws SQLException {
        HSSFSheet sheet = workBook.createSheet("COL");
        sheet.setDefaultRowHeightInPoints(20);
        sheet.setDefaultColumnWidth(20);
        HSSFCellStyle htCellStyle = getHtHssfCellStyle(workBook);

        HSSFCellStyle stCellStyle = getStHssfCellStyle(workBook);

        titleRow(sheet, htCellStyle, COL_TITLES);

        for (com.thunisoft.znbq.bbq.smd.local.Table table : tables) {
            String tableName = table.getName();
            ResultSet columns = metaData.getColumns(table.getCat(), "%" ,tableName, "%");
            HSSFRow row = sheet.createRow(sheet.getLastRowNum() + 1);
            row.setHeightInPoints(20);
            for (int i = 0; i < COL_TITLES.length; i++) {
                HSSFCell cell = row.createCell(i);
                cell.setCellStyle(stCellStyle);
                String value = "";
                if (i== 0) {
                    value = "----";
                } else if (i==1) {
                    value = table.getDesc();
                }
                cell.setCellValue(value);
            }

            while (columns.next()) {
                String[] lines = {tableName,columns.getString("COLUMN_NAME"),
                        columns.getString("REMARKS"),
                        "","",
                        String.valueOf(columns.getObject("NULLABLE")),
                        columns.getString("TYPE_NAME"),
                        String.valueOf(columns.getObject("COLUMN_SIZE")),
                        "","","","",""};
                toUppercase(lines);
                if (localLoader != null) {
                    String key = lines[0] + lines[1];
                    String[] col = localLoader.getCol(key);
                    if (col != null) {
                        lines = col;
                    }
                }
                row = sheet.createRow(sheet.getLastRowNum() + 1);
                row.setHeightInPoints(20);
                for (int i = 0; i < COL_TITLES.length; i++) {
                    HSSFCell cell = row.createCell(i);
                    cell.setCellStyle(stCellStyle);
                    cell.setCellValue(lines[i]);
                }

            }

            row = sheet.createRow(sheet.getLastRowNum()+1);
            row.setHeightInPoints(20);
            for (int i = 0; i < COL_TITLES.length; i++) {
                Cell cell = row.createCell(i);
                String value = "";
                if (i == 0) {
                    value = ">>>>";
                }
                cell.setCellStyle(stCellStyle);
                cell.setCellValue(value);
            }
        }
        endRow(sheet, stCellStyle, COL_TITLES, COL_TITLES);
    }


    private static void buildLogListSheet(HSSFWorkbook workbook) {
        HSSFSheet sheet = workbook.createSheet("LOGLIST");
        sheet.setDefaultRowHeightInPoints(20);
        sheet.setDefaultColumnWidth(20);
    }

    private static void buildTableSheet(HSSFWorkbook workBook, DatabaseMetaData metaData, List<com.thunisoft.znbq.bbq.smd.local.Table> tables, LocalLoader localLoader) throws SQLException {
        HSSFSheet sheet = workBook.createSheet("TAB");
        sheet.setDefaultRowHeightInPoints(20);
        sheet.setDefaultColumnWidth(20);
        HSSFCellStyle htCellStyle = getHtHssfCellStyle(workBook);
        HSSFCellStyle stCellStyle = getStHssfCellStyle(workBook);
        titleRow(sheet, htCellStyle, TAB_TITLES);
        for (Table table : tables) {
            String tableName = table.getName();
            ResultSet tableInfos = metaData.getTables(table.getCat(), "%",tableName,new String[]{"TABLE"});
            while (tableInfos.next()) {
                Row row = sheet.createRow(sheet.getLastRowNum()+1);
                row.setHeightInPoints(20);
                String name = tableInfos.getString("TABLE_NAME");
                String[] lines = {table.getDesc(),name, table.getCat(), "1..是", "1..是", "1..是","","","","","","",""};
                toUppercase(lines);
                if (localLoader != null) {
                    String key = lines[1]+lines[2];
                    String[] table1 = localLoader.getTable(key);
                    if (table1 != null) {
                        lines = table1;
                    }
                }
                for (int i = 0; i < TAB_TITLES.length; i++) {
                    Cell cell = row.createCell(i);
                    cell.setCellStyle(stCellStyle);
                    cell.setCellValue(lines[i]);
                }
            }
        }
        endRow(sheet, stCellStyle, TAB_TITLES, TAB_TITLES);
        HSSFPalette palette = workBook.getCustomPalette();
        palette.setColorAtIndex(HSSFColor.HSSFColorPredefined.LIME.getIndex(), (byte)153, (byte)204, (byte)255);
    }


    private static HSSFCellStyle getHtHssfCellStyle(HSSFWorkbook workBook) {
        HSSFFont font = workBook.createFont();
        font.setFontName("黑体");
        HSSFCellStyle htCellStyle = getStHssfCellStyle(workBook);
        setBorderAndFont(font, htCellStyle);
        htCellStyle.setAlignment(HorizontalAlignment.CENTER);
        htCellStyle.setFillBackgroundColor(HSSFColor.HSSFColorPredefined.LIME.getIndex());
        htCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        return htCellStyle;
    }

    private static HSSFCellStyle getStHssfCellStyle(HSSFWorkbook workBook) {
        HSSFFont st = workBook.createFont();
        st.setFontName("宋体");
        st.setFontHeightInPoints((short) 10);
        HSSFCellStyle stCellStyle = workBook.createCellStyle();
        setBorderAndFont(st, stCellStyle);
        return stCellStyle;
    }

    private static void setBorderAndFont(HSSFFont st, HSSFCellStyle stCellStyle) {
        stCellStyle.setFont(st);
        stCellStyle.setBorderBottom(BorderStyle.THIN);
        stCellStyle.setBorderTop(BorderStyle.THIN);
        stCellStyle.setBorderLeft(BorderStyle.THIN);
        stCellStyle.setBorderRight(BorderStyle.THIN);
    }

    private static void endRow(HSSFSheet sheet, HSSFCellStyle stCellStyle, String[] tabTitles, String[] tabTitles2) {
        Row endRow = sheet.createRow(sheet.getLastRowNum() + 1);
        endRow.setHeightInPoints(20);
        for (int i = 0; i < tabTitles.length; i++) {
            Cell cell = endRow.createCell(i);
            String value = "";
            if (i == 0) {
                value = "<<<<";
            }
            cell.setCellStyle(stCellStyle);
            cell.setCellValue(value);
            sheet.autoSizeColumn(i);
        }
        CellRangeAddress cellAddresses = new CellRangeAddress(1, sheet.getLastRowNum(), 0, tabTitles2.length - 1);
        sheet.setAutoFilter(cellAddresses);
    }

    private static void titleRow(HSSFSheet sheet, HSSFCellStyle htCellStyle, String[] tabTitles) {
        Row titleRow = sheet.createRow(1);
        titleRow.setHeightInPoints(20);
        for (int i = 0; i < tabTitles.length; i++) {
            Cell cell = titleRow.createCell(i);
            cell.setCellStyle(htCellStyle);
            cell.setCellValue(tabTitles[i]);
        }
    }

    private static void toUppercase(String[] strs) {
        for (int i = 0; i < strs.length; i++) {
            if (null != strs[i]) {
                strs[i] = strs[i].toUpperCase();
            }
        }
    }
}
