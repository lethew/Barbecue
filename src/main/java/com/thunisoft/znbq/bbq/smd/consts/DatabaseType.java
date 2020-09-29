package com.thunisoft.znbq.bbq.smd.consts;

import com.thunisoft.znbq.bbq.smd.model.DatabaseInfo;
import com.thunisoft.znbq.bbq.util.ConnectionUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

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
    SYBASE(1, "Sybase") {
        @SneakyThrows
        @Override
        public void execute(DatabaseInfo info, Consumer<Connection> consumer) {
            ConnectionUtil.sybase(info, consumer);
        }
    },
    /**
     * abase
     */
    ABASE(2, "Abase") {
        @SneakyThrows
        @Override
        public void execute(DatabaseInfo info, Consumer<Connection> consumer) {
            ConnectionUtil.abase(info, consumer);
        }
    };

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

    public static void executeConsumer(DatabaseInfo info, Consumer<Connection> consumer) {
        codeOf(info.getDatabaseType()).execute(info, consumer);
    }

    public abstract void execute(DatabaseInfo info, Consumer<Connection> consumer);
}
