package com.thunisoft.znbq.bbq.smd.model;

import lombok.Data;

/**
 *
 * @author <a href="mailto:wuzhao-1@thunisoft.com>Zhao.Wu</a>
 * @description com.thunisoft.znbq.bbq.smd Barbecue
 * @date 2020/9/28 0028 21:43
 */
@Data
public class DatabaseSnapshot {
    private String id;
    private String baseId;
    private String systemName;
    private int databaseType;
    private String version;
    private boolean base;
    private String smdPath;
    private String scriptPath;
}
