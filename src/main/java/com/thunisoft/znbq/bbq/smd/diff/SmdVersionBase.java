package com.thunisoft.znbq.bbq.smd.diff;

import com.thunisoft.znbq.bbq.util.FileUtil;
import com.thunisoft.znbq.bbq.util.PoiUtil;
import com.thunisoft.znbq.bbq.util.RegxUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 *
 * @author <a href="mailto:wuzhao-1@thunisoft.com>Zhao.Wu</a>
 * @description com.thunisoft.znbq.bbq.smd.diff Barbecue
 * @date 2020/9/28 0028 19:01
 */
@Slf4j
@Data
public class SmdVersionBase {
    private Map<String, Table> tableMap;
    private Map<String, Column> columnMap;
    private Map<String, Index> indexMap;
    private String baseSrc;

    public SmdVersionBase(String baseSrc) {
        this.baseSrc = baseSrc;
        this.tableMap = new LinkedHashMap<>();
        this.columnMap = new LinkedHashMap<>();
        this.indexMap = new LinkedHashMap<>();
        load(baseSrc);
    }

    /**
     * 从文件中加载SMD
     * @param baseSrc SMD根目录
     */
    private void load(String baseSrc) {
        List<File> files = FileUtil.loadFiles(new File(baseSrc), file -> file.getName().endsWith(".xls"));
        for (File file : files) {
            try(InputStream inputStream = new FileInputStream(file)) {
                Workbook workbook = new HSSFWorkbook(inputStream);
                fill(workbook.getSheet("TAB"), tableMap, Table::parse, t->true);

                Function<Index, Boolean> indexBefore = index -> Optional.ofNullable(tableMap.get(index.getTableName()))
                        .map(table -> {
                            boolean r = table.getIndices().add(index);
                            if (r) {
                                // 如果已存在相同KEY的的，则进行字段合并
                                Index exist = indexMap.get(index.key());
                                if (null != exist) {
                                    exist.getFieldName().addAll(index.getFieldName());
                                    return false;
                                }
                            }
                            return r;
                        })
                        .orElse(Boolean.FALSE);
                fill(workbook.getSheet("INDEX"), indexMap, Index::parse, indexBefore);

                Function<Column, Boolean> columnBefore = column -> Optional.ofNullable(tableMap.get(column.getTableName()))
                        .map(table -> table.getColumns().add(column))
                        .orElse(Boolean.FALSE);
                fill(workbook.getSheet("COL"), columnMap, Column::parse, columnBefore);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 填充某一Sheet到容器中
     * @param sheet 源Sheet
     * @param map 目标容器
     * @param parser 转换方法
     * @param <T> 该Sheet的数据类型
     */
    private <T extends Ikey> void fill(Sheet sheet, Map<String, T> map,
                                       Function<List<String>, T> parser,
                                       Function<T, Boolean> beforeFilter) {
        List<List<String>> lines = PoiUtil.getLines(sheet);
        for (List<String> line : lines) {
            // 这个地方强制使用SMD第二列有效数据一定为英文字符串的前提，不太优雅
            if (RegxUtil.isEnStr(line.get(1))) {
                T t = parser.apply(line);
                if (beforeFilter.apply(t)) {
                    map.put(t.key(), t);
                }
            }
        }
    }

    public Table getTable(String key){
        return tableMap.get(key);
    }
    public Column getColumn(String key){
        return columnMap.get(key);
    }
    public Index getIndex(String key){
        return indexMap.get(key);
    }

}

