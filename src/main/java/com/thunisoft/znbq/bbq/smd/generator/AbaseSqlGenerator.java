package com.thunisoft.znbq.bbq.smd.generator;

import com.thunisoft.znbq.bbq.smd.diff.Column;
import com.thunisoft.znbq.bbq.smd.diff.Index;
import com.thunisoft.znbq.bbq.smd.diff.Table;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;

/**
 *
 * @author <a href="mailto:wuzhao-1@thunisoft.com>Zhao.Wu</a>
 * @description com.thunisoft.znbq.bbq.smd.sql Barbecue
 * @date 2020/9/28 0028 20:48
 */
public class AbaseSqlGenerator implements SqlGenerator {
    @Override
    public String create(Table table) {
        StringBuilder builder = new StringBuilder();
        builder.append("-- Generated by barbecue\n\n");
        builder.append("set client_encoding = UTF8;\n");
        builder.append("set search_path to ").append(table.getCatalog()).append(";\n");
        builder.append("commit;\n\n");

        builder.append("drop table if exists ").append(table.getTableName()).append(";\n");
        builder.append("create table  ").append(table.getTableName()).append("(\n");
        String primaryKey = null;
        for (Column column : table.getColumns()) {
            if(column.isPrimaryKey()) {
                primaryKey = column.getName();
            }
            builder.append('\t').append(column.getName()).append('\t').append(column.getDataType());
            if (column.getDataLength()>=0) {
                builder.append('(').append(column.getDataLength());
                if (column.getDataPrecision()>=0) {
                    builder.append(',').append(column.getDataPrecision());
                }
                builder.append(')');
            }
            builder.append('\t');
            if (column.isNullable()) {
                builder.append("NULL");
            } else {
                builder.append("NOT NULL");
            }
            builder.append(",\t--").append(column.getRemark()).append('\n');
        }
        if (primaryKey != null) {
            builder.append("constraint PK_").append(table.getTableName()).append(" primary key( ").append(primaryKey).append(" )\n);");
        }

        if (StringUtils.hasText(table.getRemark())) {
            builder.append("comment on table  ").append(table.getTableName()).append(" is '").append(table.getRemark()).append("';\n");
        }
        for (Column column : table.getColumns()) {
            if (StringUtils.hasText(column.getRemark())) {
               builder.append("comment on column ").append(column.getTableName()).append(".").append(column.getName()).append(" is '").append(column.getRemark()).append("';\n");
            }
        }

        builder.append("commit;\n\n");

        for (Index index : table.getIndices()) {
            builder.append(create(index));
        }

        return builder.toString();
    }

    @Override
    public String create(Column column) {
        StringBuilder builder = new StringBuilder();
        builder.append("-- Generated by barbecue\n\n");
        builder.append("set client_encoding = UTF8;\n");
        builder.append("set search_path to ").append(column.getTableCat()).append(";\n");
        builder.append("alter table ").append(column.getTableName())
                .append(" add column if not exists \"").append(column.getName())
                .append(' ').append(column.getDataType());
        if (column.getDataLength()>=0) {
            builder.append('(').append(column.getDataLength());
            if (column.getDataPrecision()>=0) {
                builder.append(',').append(column.getDataPrecision());
            }
            builder.append(')');
        }
        builder.append('\t');
        if (column.isNullable()) {
            builder.append("NULL");
        } else {
            builder.append("NOT NULL");
        }
        builder.append(";\n");
        builder.append("commit;\n\n");

        return builder.toString();
    }

    @Override
    public String create(Index index) {
        StringBuilder builder = new StringBuilder();
        builder.append("-- Generated by barbecue\n\n");
        builder.append("set client_encoding = UTF8;\n");
        builder.append("set search_path to ").append(index.getTableCat()).append(";\n");
        builder.append("commit;\n\n");

        builder.append("drop index if exists ").append(index.getName()).append(";\n");
        builder.append("create  index ").append(index.getName()).append(" on ")
                .append(index.getTableName()).append(" (").append(index.fields()).append(");\n");
        builder.append("commit\n\n");

        return builder.toString();
    }
}
