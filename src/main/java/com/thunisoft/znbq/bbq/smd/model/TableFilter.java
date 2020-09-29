package com.thunisoft.znbq.bbq.smd.model;

import lombok.Data;

/**
 *
 * @author <a href="mailto:wuzhao-1@thunisoft.com>Zhao.Wu</a>
 * @description com.thunisoft.znbq.bbq.smd.model Barbecue
 * @date 2020/9/28 0028 21:53
 */
@Data
public class TableFilter {
    /**
     * 根快照编号
     */
    private String baseSnapshotId;

    private String tableCat;

    private String tableName;

    private String groupName;

    private String desc;

    /**
     * 添加版本
     */
    private String version;
}
