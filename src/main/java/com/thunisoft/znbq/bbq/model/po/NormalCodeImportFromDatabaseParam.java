package com.thunisoft.znbq.bbq.model.po;

import lombok.Data;

/**
 * 从数据导入单值代码参数
 *
 * @author <a href="mailto:wuzhao-1@thunisoft.com>Zhao.Wu</a>
 * @description com.thunisoft.znbq.bbq.model.po Barbecue
 * @date 2020/8/13 0013 14:22
 */
@Data
public class NormalCodeImportFromDatabaseParam {
    private String databaseType;
    private String databaseUrl;
    private String databaseUsername;
    private String databasePassword;
    private String typeSql;
    private String codeSql;
}
