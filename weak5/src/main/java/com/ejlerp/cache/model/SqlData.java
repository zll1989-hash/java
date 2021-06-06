package com.ejlerp.cache.model;

import java.util.Map;

/**
 * @Author Mike
 * @create 2021/6/2 13:49
 */
public class SqlData {

    private Map<String, String> columnMap;
    private String id;
    private String name;

    public Map<String, String> getColumnMap() {
        return columnMap;
    }

    public void setColumnMap(Map<String, String> columnMap) {
        this.columnMap = columnMap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
