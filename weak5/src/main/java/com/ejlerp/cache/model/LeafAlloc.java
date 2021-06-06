package com.ejlerp.cache.model;

import java.util.Date;

/**
 * @Author lucien
 * @create 2021/5/24 9:32
 */
public class LeafAlloc {
    /**
     * 业务key
     */
    private String bizTag;

    /**
     * 当前已经分配了的最大id
     */
    private Long maxId;

    /**
     * 初始步长，也是动态调整的最小步长
     */
    private Integer step;

    /**
     * 业务key的描述
     */
    private String description;

    /**
     * 数据库维护的更新时间
     */
    private Date updateTime;

    public String getBizTag() {
        return bizTag;
    }

    public void setBizTag(String bizTag) {
        this.bizTag = bizTag;
    }

    public Long getMaxId() {
        return maxId;
    }

    public void setMaxId(Long maxId) {
        this.maxId = maxId;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}