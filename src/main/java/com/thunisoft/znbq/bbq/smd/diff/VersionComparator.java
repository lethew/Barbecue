package com.thunisoft.znbq.bbq.smd.diff;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 *
 * @author <a href="mailto:wuzhao-1@thunisoft.com>Zhao.Wu</a>
 * @description com.thunisoft.znbq.bbq.smd.diff Barbecue
 * @date 2020/9/28 0028 19:02
 */
public class VersionComparator {

    /**
     * 比较两个版本的SMD差异
     * @param previous 被比较版本
     * @param current 比较版本
     * @return 比较版本相较被比较版本的增量表、字段、索引信息
     */
    public static CompareResult compare(SmdVersionBase previous, SmdVersionBase current){
        if (previous == null) {
            return previousNull(current);
        }

        CompareResult result = new CompareResult();

        // 表增量
        compare(current::getTableMap,
                previous::getTable,
                table -> true,
                result::addIncrTable);

        // 字段增量
        compare(current::getColumnMap,
                previous::getColumn,
                column -> !previous.getTableMap().containsKey(column.getTableName()),
                result::addIncrColumn);

        // 索引增量
        compare(current::getIndexMap,
                previous::getIndex,
                index -> !previous.getTableMap().containsKey(index.getTableName()),
                result::addIncrIndex);

        return result;
    }

    /**
     * 当被比较版本为null时，直接认为所有表结构均为增量表，用于生成全量脚本
     * @param current 比较版本
     * @return 全量表信息
     */
    private static CompareResult previousNull(SmdVersionBase current) {
        CompareResult result = new CompareResult();
        Collection<Table> values = current.getTableMap().values();
        result.getIncrTables().addAll(values);
        return result;
    }

    /**
     * 比较某类数据（表、字段、索引）
     * @param current 比较版本的（表、字段、索引）
     * @param previous 查看被比较版本中是否已经有该对象
     * @param filter 过滤器，用于过滤已经在表增量中存在的字段、索引
     * @param insert 存储方法
     * @param <T> 表，字段，索引
     */
    private static <T> void compare(Supplier<Map<String, T>> current, Function<String, T> previous,
                                    Predicate<T> filter, Consumer<T> insert) {
        for (Map.Entry<String, T> entry : current.get().entrySet()) {
            T old = previous.apply(entry.getKey());
            if (null == old && filter.test(entry.getValue())) {
                insert.accept(entry.getValue());
            }
        }
    }
}
