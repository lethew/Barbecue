package com.thunisoft.znbq.bbq.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.NoSuchElementException;

/**
 *
 * @author <a href="mailto:wuzhao-1@thunisoft.com>Zhao.Wu</a>
 * @description com.thunisoft.znbq.bbq.constant Barbecue
 * @date 2020/8/17 0017 14:32
 */
@Getter
@AllArgsConstructor
public enum  DatabaseTypeEnum {
    /**
     * mysql
     */
    MYSQL("mysql", ""),
    /**
     * postgresql
     */
    POSTGRESQL("postgresql", ""),
    /**
     * abase
     */
    ABASE("abase", ""),
    /**
     * sybase
     */
    SYBASE("sybase", "net.sourceforge.jtds.jdbc.Driver"),
    /**
     * sqlserver
     */
    SQLSERVER("sqlserver", ""),
    /**
     * oracle
     */
    ORACLE("oracle", "");
    private final String code;
    private final String driver;

    @SneakyThrows
    public void load() {
        Class.forName(driver);
    }

    public static DatabaseTypeEnum codeOf(String code) {
        for (DatabaseTypeEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        throw new NoSuchElementException("未找到对应的数据库类型："+code);
    }
}
