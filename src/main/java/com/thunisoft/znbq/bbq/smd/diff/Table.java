package com.thunisoft.znbq.bbq.smd.diff;

import lombok.Data;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:wuzhao-1@thunisoft.com>Zhao.Wu</a>
 * @description com.thunisoft.znbq.bbq.smd.diff Barbecue
 * @date 2020/9/28 0028 18:17
 */
@Data
public class Table implements Ikey {
    private String catalog;
    private String tableName;
    private String remark;
    private LinkedHashSet<Column> columns;
    private Set<Index> indices;

    public Table() {
        columns = new LinkedHashSet<>();
        indices = new HashSet<>();
    }

    public static Table parse(List<String> items) {
        Table table = new Table();
        int i = 0;
        for (String item : items) {
            switch (i){
                case 0: table.setRemark(item);break;
                case 1: table.setTableName(item);break;
                case 2: table.setCatalog(item);break;
                default:;
            }
            i++;
        }
        return table;
    }

    @Override
    public String key(){
        return tableName;
    }
}
