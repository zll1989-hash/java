package cn.egenie.architect.common.excel.model;

import java.util.Map;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lucien
 * @since 2021/01/20
 */
@Getter
@Setter
public class BaseRowModel {
    private Long id;

    @ExcelIgnore
    private Map<String, Object> dynamicPropertyMap;
}
