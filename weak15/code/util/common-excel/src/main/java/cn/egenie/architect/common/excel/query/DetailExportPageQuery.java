package cn.egenie.architect.common.excel.query;

import java.util.List;

import cn.egenie.architect.common.core.util.JsonUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * 导出二级查询
 *
 * @author lucien
 * @since 2021/07/13
 */
@Getter
@Setter
public class DetailExportPageQuery extends ExportPageQuery {
    /**
     * 一级查询列表ids，比如订单ids
     */
    private List<Long> mainIds;

    public static DetailExportPageQuery of(ExportPageQuery exportPageQuery) {
        return JsonUtils.convertValue(exportPageQuery, DetailExportPageQuery.class);
    }

    @Override
    public String toString() {
        return "DetailExportPageQuery{" +
                "mainIds=" + mainIds +
                ", pageNum=" + getPageNum() +
                ", pageSize=" + getPageSize() +
                "} ";
    }
}
