package com.thunisoft.znbq.bbq.smd.diff;

import com.thunisoft.znbq.bbq.smd.sql.SqlGenerator;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 *
 * @author <a href="mailto:wuzhao-1@thunisoft.com>Zhao.Wu</a>
 * @description com.thunisoft.znbq.bbq.smd.diff Barbecue
 * @date 2020/9/28 0028 19:04
 */
@Slf4j
@Data
public class CompareResult {
    /**
     * 增量表
     */
    private List<Table> incrTables;
    /**
     * 增量字段
     */
    private List<Column> incrColumns;
    /**
     * 增量索引
     */
    private List<Index> incrIndices;

    public CompareResult() {
        incrColumns = new LinkedList<>();
        incrIndices = new LinkedList<>();
        incrTables = new LinkedList<>();
    }

    public void addIncrTable(Table table) {
        incrTables.add(table);
    }

    public void addIncrColumn(Column column) {
        incrColumns.add(column);
    }

    public void addIncrIndex(Index index) {
        incrIndices.add(index);
    }


    /**
     * 根据比较结果生成脚本
     * @param generator 脚本生成器
     * @return 脚本存储根目录
     */
    public String generateScript(SqlGenerator generator){
        List<Table> incrTables = getIncrTables();
        String src = "script/"+Thread.currentThread().getId()+"_"+System.currentTimeMillis();
        File dir = new File(src);
        if (dir.mkdirs()) {
            for (Table incrTable : incrTables) {
                String sql = generator.create(incrTable);
                String tableName = incrTable.getTableName();
                File f = new File(src+"/01_CT_"+tableName+".sql");
                writeString(sql, f);
            }

            ci(this::getIncrIndices, Index::getTableName, generator::create, src+"/03_CI");
            ci(this::getIncrColumns, Column::getTableName, generator::create, src+"/02_CC");
        }
        return src;
    }

    private <T> void ci(Supplier<List<T>> listSupplier, Function<T, String> grouping, Function<T, String> generator, String perfix) {
        listSupplier.get().stream()
                .collect(Collectors.groupingBy(grouping))
                .forEach((k, v)->{
                    String sql = v.stream().map(generator).collect(Collectors.joining("%n"));
                    File f = new File(perfix+"_"+k+".sql");
                    writeString(sql, f);
                });
    }

    private void writeString(String sql, File f) {
        try (FileWriter writer = new FileWriter(f)) {
            writer.write(sql);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
