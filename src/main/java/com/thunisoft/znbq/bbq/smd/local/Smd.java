package com.thunisoft.znbq.bbq.smd.local;

import com.thunisoft.znbq.bbq.smd.consts.DatabaseType;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * TODO:
 *
 * @author <a href="mailto:wuzhao-1@thunisoft.com>Zhao.Wu</a>
 * @description com.thunisoft.znbq.bbq.smd Barbecue
 * @date 2020/9/28 0028 11:07
 */
@Getter
@AllArgsConstructor
public enum Smd {
    /**
     * 智能保全np版
     */
    ZNBQ_NP(DatabaseType.SYBASE.getCode(), "智能保全/Sybase/2.5.10", "/config/table_np.json", LocalLoader.SYBASE_ROOT),
    /**
     * 智能保全t3版
     */
    ZNBQ_T3(DatabaseType.ABASE.getCode(), "智能保全/Abase/3.1.7", "/config/table_t3.json", LocalLoader.ABASE_ROOT);
    /**
     * databasetype 0:sybase; 1:abase
     */
    private final int type;
    private final String desc;
    private final String path;
    private final String src;
}
