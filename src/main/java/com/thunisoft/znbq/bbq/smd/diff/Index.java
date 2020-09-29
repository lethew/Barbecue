package com.thunisoft.znbq.bbq.smd.diff;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author <a href="mailto:wuzhao-1@thunisoft.com>Zhao.Wu</a>
 * @description com.thunisoft.znbq.bbq.smd.diff Barbecue
 * @date 2020/9/28 0028 18:18
 */
@Data
public class Index implements Ikey {
    private String tableCat;
    private String tableName;
    private String name;
    private String indexType;
    private List<String> fieldName;

    public Index() {
        fieldName = new LinkedList<>();
    }

    public static Index parse(List<String> items) {
        Index index = new Index();
        int i = 0;
        for (String item : items) {
            switch (i){
                case 0: index.setTableName(item);break;
                case 1: index.setName(item);break;
                case 2: index.setIndexType(item);break;
                case 3: index.getFieldName().add(item);break;
                case 4: index.setTableCat(item);break;
                default:;
            }
            i++;
        }
        return index;
    }

    @Override
    public String key(){
        return tableName+"#"+name;
    }
}
