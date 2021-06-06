package com.ejlerp.cache.controller;

import com.alibaba.druid.util.StringUtils;
import com.ejlerp.cache.api.KVCacher;
import com.ejlerp.cache.model.DataSourceVo;
import com.ejlerp.cache.model.SqlData;
import com.ejlerp.cache.service.IDGeneratorImpl;
import com.ejlerp.cache.util.SqlTableAccess;
import com.ejlerp.cache.vo.SerialNumberVo;
import com.ejlerp.common.vo.InvokeResult;
import com.ejlerp.common.vo.JsonResult;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author Mike
 * @create 2021/5/25 17:47
 */
@RestController
@RequestMapping("/api/log")
public class LogBiz {

    private static final Logger logger = LoggerFactory.getLogger(LogBiz.class);
    @Autowired
    private IDGeneratorImpl idGe;

    @Autowired
    private KVCacher kvCacher;


    @ApiOperation(value = "cache", tags = "1.0.0")
    @RequestMapping(value = "/cache/{key}/{value}", method = RequestMethod.GET)
    public String cache(@PathVariable String key, @PathVariable String value) {
        kvCacher.set(key, value, 1, TimeUnit.HOURS);
        return kvCacher.get(key);
    }

    @ApiOperation(value = "测试接口功能", tags = "1.0.0")
    @RequestMapping(value = "/testSwitch", method = RequestMethod.GET)
    public InvokeResult<List<String>> testSwitch(@RequestParam String name) {
        logger.info("************test recordLog begin**********");

        List<Long> idlist = new ArrayList<>();
        idlist = idGe.generateList("leaf-segment-test", 6);

        for (Long id : idlist) {
            System.out.println("print id value:" + id);
        }
        System.out.println("------------");
        return null;
    }

    @ApiOperation(value = "新增或修改SQL")
    @PostMapping(value = "/cache/saveOrupdate")
    public JsonResult addOrUpdate(@RequestBody DataSourceVo dv) {

        try {
            List<SqlData> sqlDataList = getDatabaseNameToTableNameAndColumnName(dv.getUrlA(), dv.getDataSourceA(), dv.getUserNameA(), dv.getPassWordA());
            for (SqlData sqlData : sqlDataList) {
                if (!StringUtils.isEmpty(sqlData.getId())) {
                    SqlTableAccess access = new SqlTableAccess();
                    int acc = access.selectSqlInfo(dv.getUrlB(), dv.getUserNameB(), dv.getPassWordB(), dv.getDataSourceB(), sqlData.getName());
                    if (acc == 1) {
                        access.deleteSqlInfo(dv.getUrlB(), dv.getUserNameB(), dv.getPassWordB(), dv.getDataSourceB(), sqlData.getName());
                    }
                    access.addSqlInfo(dv.getUrlB(), dv.getUserNameB(), dv.getPassWordB(), dv.getDataSourceB(), sqlData.getName(), sqlData.getId());
                }
            }
            return new JsonResult(JsonResult.SUCCESSFUL);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return new JsonResult(JsonResult.FAILED);
    }


    public static List<SqlData> getDatabaseNameToTableNameAndColumnName(String urlValue, String databaseName, String userName, String passWord) throws SQLException {
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://" + urlValue + ":3306/" + databaseName;
        List<SqlData> sqlDataList = new ArrayList<>();
        Connection connection = null;
        ResultSet tables = null;
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, userName, passWord);
            DatabaseMetaData metaData = connection.getMetaData();
            tables = metaData.getTables(null, null, null, new String[]{"TABLE"});
            while (tables.next()) {
                Map<String, String> columnNameMap = new HashMap<>();
                SqlData sqlData = new SqlData();
                String table_name = tables.getString("TABLE_NAME");
                ResultSet columns = metaData.getColumns(null, null, table_name, "%");
                Statement statement = connection.createStatement();
                while (columns.next()) {
                    String column_name = columns.getString("COLUMN_NAME");
                    String type_name = columns.getString("TYPE_NAME");
                    columnNameMap.put(column_name, type_name);
                    sqlData.setColumnMap(columnNameMap);
                }
                Set<String> columnValue = columnNameMap.keySet();
                for (String column : columnValue) {
                    if (column.equals(table_name + "_id")) {
                        ResultSet resultSet = statement.executeQuery("SELECT * FROM " + table_name + " where " + table_name + "_id = ( SELECT max(" + table_name + "_id) from " + table_name + ") limit 1");
                        while (resultSet.next()) {
                            String id = resultSet.getString(1);
                            if (!StringUtils.isEmpty(id)) {
                                sqlData.setId(String.valueOf(Long.parseLong(id) + 100L));
                            }
                        }
                    }
                }
                sqlData.setName(table_name);
                sqlDataList.add(sqlData);
            }
        } catch (Exception e) {
            logger.debug("获取驱动SQL失败:" + e.getMessage());
        } finally {
            if (tables != null) {
                tables.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        return sqlDataList;
    }

}
