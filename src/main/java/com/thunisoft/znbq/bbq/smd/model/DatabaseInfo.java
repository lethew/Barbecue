package com.thunisoft.znbq.bbq.smd.model;

import lombok.Data;

/**
 * TODO:
 *
 * @author <a href="mailto:wuzhao-1@thunisoft.com>Zhao.Wu</a>
 * @description com.thunisoft.znbq.bbq.smd Barbecue
 * @date 2020/9/28 0028 21:47
 */
@Data
public class DatabaseInfo {
    private String id;
    private String baseSnapshotId;
    private int databaseType;
    private String address;
    private String url;
    private String database;
    private String username;
    private String password;
}
