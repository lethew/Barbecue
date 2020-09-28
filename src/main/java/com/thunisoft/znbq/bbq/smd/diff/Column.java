package com.thunisoft.znbq.bbq.smd.diff;

import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 *
 * @author <a href="mailto:wuzhao-1@thunisoft.com>Zhao.Wu</a>
 * @description com.thunisoft.znbq.bbq.smd.diff Barbecue
 * @date 2020/9/28 0028 18:17
 */
@Data
public class Column implements Ikey {
    private String tableName;
    private String name;
    private String remark;
    private boolean primaryKey;
    private String defaultValue;
    private boolean nullable;
    private String dataType;
    private int dataLength;
    private int dataPrecision;

    public static Column parse(List<String> items) {
        Column column = new Column();
        int i = 0;
        for (String item : items) {
            switch (i){
                case 0: column.setTableName(item);break;
                case 1: column.setName(item);break;
                case 2: column.setRemark(item);break;
                case 3: column.setPrimaryKey("1".equals(item));break;
                case 4: column.setDefaultValue(item);break;
                case 5: column.setNullable("1".equals(item));break;
                case 6: column.setDataType(item);break;
                case 7: column.setDataLength(StringUtils.hasText(item)?Integer.parseInt(item):-1);break;
                case 8: column.setDataPrecision(StringUtils.hasText(item)?Integer.parseInt(item):-1);break;
                default:;
            }
            i++;
        }
        return column;
    }

    @Override
    public String key(){
        return tableName+"#"+name;
    }
}
