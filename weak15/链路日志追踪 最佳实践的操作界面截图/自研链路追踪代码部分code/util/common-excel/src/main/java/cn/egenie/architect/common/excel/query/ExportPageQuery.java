package cn.egenie.architect.common.excel.query;

import cn.egenie.architect.common.core.query.PageQuery;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lucien
 * @since 2021/07/13
 */
@Getter
@Setter
public class ExportPageQuery extends PageQuery {
    /**
     * 用户id
     */
    private long userId;

    /**
     * 租户id
     */
    private long tenantId;

    @Override
    public String toString() {
        return "ExportPageQuery{" +
                "userId=" + userId +
                ", tenantId=" + tenantId +
                ", pageNum=" + getPageNum() +
                ", pageSize=" + getPageSize() +
                "} " + super.toString();
    }
}
