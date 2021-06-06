package com.ejlerp.cache.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @Author Lucien
 * @create 2021/6/2 13:20
 */
public class DbHeplerUtil {

    private static Connection connection;

    private static String url = "jdbc:mysql://192.168.200.112:3306/egenie_basic?characterEncoding=utf8&useSSL=true";
    private static String useName = "root";
    private static String passWord = "my-secret-pw";
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        try {
            //通过DriverManager类的getConenction方法指定三个参数,连接数据库
            connection = DriverManager.getConnection(url, useName, passWord);
            System.out.println("连接数据库成功!!!");
            //返回连接对象
            return connection;

        } catch (SQLException e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }

    public static Connection getConnection(String url,String useName,String passWord,String dataBase) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        try {
            String urlValue = "jdbc:mysql://"+url+":3306/"+dataBase;
            connection = DriverManager.getConnection(urlValue, useName, passWord);
            //返回连接对象
            return connection;

        } catch (SQLException e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }

}
