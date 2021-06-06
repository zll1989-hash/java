package com.ejlerp.cache.model;

import java.io.Serializable;

/**
 * @Author Mike
 * @create 2021/6/2 16:37
 */
public class DataSourceVo {

    private String dataSourceA;
    private String urlA;
    private String userNameA;
    private String passWordA;

    private String dataSourceB;
    private String urlB;
    private String userNameB;
    private String passWordB;


    public String getDataSourceA() {
        return dataSourceA;
    }

    public void setDataSourceA(String dataSourceA) {
        this.dataSourceA = dataSourceA;
    }

    public String getUrlA() {
        return urlA;
    }

    public void setUrlA(String urlA) {
        this.urlA = urlA;
    }

    public String getUserNameA() {
        return userNameA;
    }

    public void setUserNameA(String userNameA) {
        this.userNameA = userNameA;
    }

    public String getPassWordA() {
        return passWordA;
    }

    public void setPassWordA(String passWordA) {
        this.passWordA = passWordA;
    }

    public String getDataSourceB() {
        return dataSourceB;
    }

    public void setDataSourceB(String dataSourceB) {
        this.dataSourceB = dataSourceB;
    }

    public String getUrlB() {
        return urlB;
    }

    public void setUrlB(String urlB) {
        this.urlB = urlB;
    }

    public String getUserNameB() {
        return userNameB;
    }

    public void setUserNameB(String userNameB) {
        this.userNameB = userNameB;
    }

    public String getPassWordB() {
        return passWordB;
    }

    public void setPassWordB(String passWordB) {
        this.passWordB = passWordB;
    }
}
