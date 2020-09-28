package com.thunisoft.znbq.bbq.util;

import com.thunisoft.znbq.bbq.smd.model.DatabaseInfo;
import lombok.experimental.UtilityClass;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Consumer;

/**
 *
 * @author <a href="mailto:wuzhao-1@thunisoft.com>Zhao.Wu</a>
 * @description com.thunisoft.znbq.bbq.util Barbecue
 * @date 2020/9/28 0028 11:20
 */
@UtilityClass
public class ConnectionUtil {
    /**
     * 创建要给sybase链接并消费他
     * @param address 数据库地址：ip:port; (172.18.17.186:5000, etc)
     * @param database 数据库： YWST
     * @param username 账号
     * @param password 密码
     * @param consumer 消费者
     */
    public void sybase(String address, String database, String username, String password,
                       Consumer<Connection> consumer) throws ClassNotFoundException, SQLException {
        Class.forName("net.sf.log4jdbc.sql.jdbcapi.DriverSpy");
        String url = "jdbc:log4jdbc:jtds:sybase://"+address+"/"+database+";charset=UTF-8;appname=ZNBQ;";
        Connection connection = DriverManager.getConnection(url, username, password);
        consumer.accept(connection);
        connection.close();;
    }

    public void sybase(DatabaseInfo databaseInfo,
                       Consumer<Connection> consumer) throws SQLException, ClassNotFoundException {
        sybase(databaseInfo.getAddress(), databaseInfo.getDatabase(),
                databaseInfo.getUsername(), databaseInfo.getPassword(), consumer);
    }

    /**
     * 创建要给abase链接并消费他
     * @param address 数据库地址：ip:port; (172.18.17.186:5000, etc)
     * @param database 数据库： YWST
     * @param username 账号
     * @param password 密码
     * @param consumer 消费者
     */
    public void abase(String address, String database, String username, String password,
                      Consumer<Connection> consumer) throws ClassNotFoundException, SQLException {
        Class.forName("net.sf.log4jdbc.sql.jdbcapi.DriverSpy");
        String url = "jdbc:log4jdbc:ArteryBase://"+address+"/"+database+"" +
                "?currentSchema=ywst&ApplicationName=znbq&Charset=utf8";
        Connection connection = DriverManager.getConnection(url, username, password);
        consumer.accept(connection);
        connection.close();;
    }

    public void abase(DatabaseInfo databaseInfo,
                      Consumer<Connection> consumer) throws SQLException, ClassNotFoundException {
        abase(databaseInfo.getAddress(), databaseInfo.getDatabase(),
                databaseInfo.getUsername(), databaseInfo.getPassword(), consumer);
    }
}
