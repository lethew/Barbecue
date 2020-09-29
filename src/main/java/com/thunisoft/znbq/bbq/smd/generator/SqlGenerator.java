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
     * 生成新增索引语句
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
            // 考虑单例模式
            case SYBASE: return new SybaseSqlGenerator();
            case ABASE: return new AbaseSqlGenerator();
            default: return null;
        }
    }
}
