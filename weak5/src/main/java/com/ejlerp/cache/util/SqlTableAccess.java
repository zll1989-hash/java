package com.ejlerp.cache.util;

import com.ejlerp.cache.controller.LogBiz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * @Author Lucien
 * @create 2021/6/2 13:30
 */
public class SqlTableAccess {

    private static final Logger logger = LoggerFactory.getLogger(SqlTableAccess.class);

    private Connection conn;
    private Statement stt;
    private ResultSet set;

    public int selectSqlInfo(String url, String useName, String passWord, String dataBase, String tableName) {

        try {
            conn = DbHeplerUtil.getConnection(url, useName, passWord, dataBase);
            if (conn == null) {
                return 0;
            }
            String Sql = "select * from `leaf_alloc` " + " where biz_tag = '" + tableName + "';";
            stt = conn.createStatement();
            set = stt.executeQuery(Sql);
            // 获取数据
            while (set.next()) {
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                set.close();
                conn.close();
            } catch (Exception e2) {
                // TODO: handle exception
            }

        }
        return 0;
    }

    public void addSqlInfo(String url, String useName, String passWord, String dataBase, String tableName, String id) {

        try {
            conn = DbHeplerUtil.getConnection(url, useName, passWord, dataBase);
            if (conn == null) {
                return;
            }
            /**
             * insert into leaf_alloc(biz_tag, max_id, step, description) values('filter_set_setting', 296210000, 50, '日志filter_set_setting表名');
             */
            String sql = "insert into leaf_alloc (biz_tag, max_id, step, description) values ('" + tableName + "'," + Long.parseLong(id) + "," + 100 + ",'log information')";
            stt = conn.createStatement();
            stt.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("addSqlInfo error" + e);
        } finally {
            try {
                conn.close();
            } catch (Exception e2) {
                logger.debug("close  is error" + e2.getMessage());
            }
        }
    }

    public void deleteSqlInfo(String url, String useName, String passWord, String dataBase, String tableName) {
        try {
            conn = DbHeplerUtil.getConnection(url, useName, passWord, dataBase);
            if (conn == null) {
                return;
            }
            String deleteSql = "DELETE FROM leaf_alloc WHERE biz_tag='" + tableName + "';";
            stt = conn.createStatement();
            stt.executeUpdate(deleteSql);
        } catch (Exception e) {
            logger.debug("deleteSqlInfo error" + e);
        } finally {
            try {
                conn.close();
            } catch (Exception e2) {
                logger.debug("deleteSqlInfo close error" + e2);
            }
        }
    }

    public void updateSqlInfo(String url, String useName, String passWord, String dataBase, String tableName) {

        PreparedStatement ps = null;

        try {
            //获取连接
            conn = DbHeplerUtil.getConnection(url, useName, passWord, dataBase);
            if (conn == null) {
                return;
            }
            //定义Sql语句
            String UpdateSql = "UPDATE login SET pwd = '" + "NewPwd" + "' WHERE user = " + "user" + ";";
            //创建Statement对象
            ps = conn.prepareStatement(UpdateSql);
            //执行sql语句
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.debug("updateSqlInfo error" + e);
        } finally {
            //释放资源
            try {
                ps.close();
                conn.close();
            } catch (Exception e2) {
                logger.debug("updateSqlInfo close error" + e2);
            }
        }
    }

//    public static void main(String[] args) {
//        SqlTableAccess access = new SqlTableAccess();
//        int a = access.selectSqlInfo("192.168.200.112","root","my-secret-pw","egenie_basic","leaf_alloc");
//        System.out.println("======"+a);
//        //access.addSqlInfo("192.168.200.112","root","my-secret-pw","egenie_basic","leaf_alloc","100");
//        System.out.println("====================");
//        //access.deleteSqlInfo("192.168.200.112","root","my-secret-pw","egenie_basic","leaf_alloc");
//    }

}
