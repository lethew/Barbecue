package com.thunisoft.znbq.bbq.smd.generator;

import com.thunisoft.znbq.bbq.smd.consts.DatabaseType;
import com.thunisoft.znbq.bbq.smd.diff.Column;
import com.thunisoft.znbq.bbq.smd.diff.Index;
import com.thunisoft.znbq.bbq.smd.diff.Table;

/**
 * SQL生成器
 * @author <a href="mailto:wuzhao-1@thunisoft.com>Zhao.Wu</a>
 * @description com.thunisoft.znbq.bbq.smd.sql Barbecue
 * @date 2020/9/28 0028 20:48
 */
public interface SqlGenerator {
    /**
     * 生成文件字符集
     * @return Sybase:GBK, Abase:utf-8
     */
    String charset();
    /**
     * 生成建表语句
     * @param table 表信息
     * @return 建表语句
     */
    String create(Table table);

    /**
     * 生成新增字段语句
     * @param column 字段信息
     * @return 新增字段语句
     */
    String create(Column column);

    /**
     * 生成新增索引语句, 暂时不支持索引类型判断 fixme
     * @param index 索引信息
     * @return 新增索引语句
     */
    String create(Index index);

    /**
     * 根据数据库类型返回对应的脚本生成类
     * @param type 数据库类型
     * @return 脚本生成类
     */
    static SqlGenerator getInstance(int type) {
        switch (DatabaseType.codeOf(type)) {
            case SYBASE: return SybaseSqlGenerator.INSTANCE;
            case ABASE: return AbaseSqlGenerator.INSTANCE;
            default: return null;
        }
    }

    default void datatType(Column column, StringBuilder builder) {
        builder.append(column.getDataType());
        if (column.getDataLength()>=0) {
            builder.append('(').append(column.getDataLength());
            if (column.getDataPrecision()>=0) {
                builder.append(',').append(column.getDataPrecision());
            }
            builder.append(')');
        }
    }

    default void columnLine(StringBuilder builder, Column column) {
        builder.append('\t').append(column.getName()).append('\t');
        datatType(column, builder);
        builder.append('\t');
        if (!column.isNullable()) {
            builder.append("NULL");
        } else {
            builder.append("NOT NULL");
        }
        builder.append(",\t--").append(column.getRemark()).append('\n');
    }

}
