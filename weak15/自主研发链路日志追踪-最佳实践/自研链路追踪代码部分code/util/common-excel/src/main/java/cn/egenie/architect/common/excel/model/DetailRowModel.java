package cn.egenie.architect.common.excel.model;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lucien
 * @since 2021/07/13
 */
@Getter
@Setter
public class DetailRowModel {
    /**
     * 一级表id
     */
    private Long mainId;

    /**
     * 二级表导出属性
     */
    private Map<String, Object> dynamicPropertyMap;
}
