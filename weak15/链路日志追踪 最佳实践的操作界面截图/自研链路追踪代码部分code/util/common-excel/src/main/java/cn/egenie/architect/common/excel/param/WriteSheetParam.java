package cn.egenie.architect.common.excel.param;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.alibaba.excel.write.handler.WriteHandler;

import cn.egenie.architect.common.excel.model.BaseRowModel;
import cn.egenie.architect.common.excel.model.DetailRowModel;
import cn.egenie.architect.common.excel.query.DetailExportPageQuery;
import cn.egenie.architect.common.excel.query.ExportPageQuery;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lucien
 * @since 2021/01/21
 */
@Getter
@Setter
public class WriteSheetParam<T extends BaseRowModel> {
    @NotBlank(message = "sheet name不能为空")
    private String sheetName;

    /**
     * excel的head，注意是excel最终的中文head
     */
    @NotEmpty(message = "head不能为空")
    private List<String> headList;

    /**
     * 需要合并的excel的head，注意是excel最终的中文head
     */
    private List<String> mergeHeadList;

    /**
     * 表头翻译，key是英文，value是中文
     * 如果model的动态属性名需要翻译，就必须传
     */
    private Map<String, String> translationMap;

    @NotNull(message = "分页查询参数必传")
    private ExportPageQuery exportPageQuery;

    @NotNull(message = "总数量查询函数必传")
    private Function<ExportPageQuery, Integer> totalFun;

    /**
     * 是否以明细总数为准
     */
    private boolean totalByDetail;

    @NotNull(message = "分页查询函数必传")
    private Function<ExportPageQuery, List<T>> dataListFun;

    @NotNull(message = "model class必传")
    private Class<T> modelClass;

    /**
     * 二级查询
     */
    private DetailExportPageQuery detailExportPageQuery;
    /**
     * 二级查询结果，key是一级明细的Id
     */
    private Function<DetailExportPageQuery, List<DetailRowModel>> detailDataListFun;

    /**
     * 使用二级明细的顺序导出，默认使用一级明细的顺序
     */
    boolean orderByDetail;

    /**
     * 预留一些合并，样式之类的钩子，
     * 具体参考easy excel的WriteHandler
     */
    private List<WriteHandler> writeHandlers;

    public WriteSheetParam() {

    }

    public WriteSheetParam(String sheetName,
                           Class<T> modelClass,
                           List<String> headList,
                           List<String> mergeHeadList,
                           ExportPageQuery exportPageQuery,
                           Function<ExportPageQuery, Integer> totalFun,
                           Function<ExportPageQuery, List<T>> dataListFun,
                           DetailExportPageQuery detailExportPageQuery,
                           Function<DetailExportPageQuery, List<DetailRowModel>> detailDataListFun,
                           boolean orderByDetail,
                           Map<String, String> translationMap,
                           List<WriteHandler> writeHandlers) {
        this.sheetName = sheetName;
        this.modelClass = modelClass;

        this.headList = headList;
        this.mergeHeadList = mergeHeadList;

        this.exportPageQuery = exportPageQuery;
        this.totalFun = totalFun;
        this.dataListFun = dataListFun;

        this.detailExportPageQuery = detailExportPageQuery;
        this.detailDataListFun = detailDataListFun;

        this.orderByDetail = orderByDetail;
        this.translationMap = translationMap;
        this.writeHandlers = writeHandlers;
    }
}
