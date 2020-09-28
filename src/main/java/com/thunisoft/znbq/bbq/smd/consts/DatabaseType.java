package com.thunisoft.znbq.bbq.smd.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.NoSuchElementException;

/**
 *
 * @author <a href="mailto:wuzhao-1@thunisoft.com>Zhao.Wu</a>
 * @description com.thunisoft.znbq.bbq.smd.consts Barbecue
 * @date 2020/9/28 0028 22:26
 */
@Getter
@AllArgsConstructor
public enum DatabaseType {
    /**
     * sybase
     */
    SYBASE(1, "Sybase"),
    /**
     * abase
     */
    ABASE(2, "Abase");

    /**
     * 代码值
     */
    private final int code;
    /**
     * 描述
     */
    private final String desc;

    public static DatabaseType codeOf(int code) {
        for (DatabaseType value : values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        throw new NoSuchElementException("不支持的数据库类型："+code);
    }
}
