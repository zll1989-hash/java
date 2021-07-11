package com.ejlerp.log.domain;

import com.ejlerp.common.util.AbstractObject;

/**
 * @date 2021-05-18
 * @author paul
 * 业务日志查询VO
 */
public class LogBizQueryVO extends AbstractObject {
    /**
     * 业务日志模块类型
     * @see com.ejlerp.log.enums.LogBizModuleTypeEnum
     */
    private String logBizModuleType;
    /**
     * 模块对应的表的ID 如发货单表ID 入库单表ID
     */
    private Long entityId;
    /**
     * 排序规则
     */
    private String sort;
    /**
     * 页
     */
    private Integer page;
    /**
     * 页大小
     */
    private Integer pageSize;

    @Override
    public String toString() {
        return "LogBizQueryVO{" +
                "logBizModuleType='" + logBizModuleType + '\'' +
                ", entityId=" + entityId +
                ", sort='" + sort + '\'' +
                ", page=" + page +
                ", pageSize=" + pageSize +
                '}';
    }


    public String getLogBizModuleType() {
        return logBizModuleType;
    }

    public void setLogBizModuleType(String logBizModuleType) {
        this.logBizModuleType = logBizModuleType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
