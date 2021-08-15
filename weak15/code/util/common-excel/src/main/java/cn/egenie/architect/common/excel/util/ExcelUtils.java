package cn.egenie.architect.common.excel.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;

import cn.egenie.architect.common.core.constants.Constants;
import cn.egenie.architect.common.core.util.Assert;
import cn.egenie.architect.common.core.util.Funs;
import cn.egenie.architect.common.core.util.JsonUtils;
import cn.egenie.architect.common.core.util.ReflectUtils;
import cn.egenie.architect.common.core.util.TimeUtils;
import cn.egenie.architect.common.core.util.ValidatorUtils;
import cn.egenie.architect.common.excel.event.BaseExcelListener;
import cn.egenie.architect.common.excel.merge.ExportMergeStrategy;
import cn.egenie.architect.common.excel.model.BaseRowModel;
import cn.egenie.architect.common.excel.model.DetailRowModel;
import cn.egenie.architect.common.excel.param.WriteSheetParam;
import cn.egenie.architect.common.excel.query.DetailExportPageQuery;
import cn.egenie.architect.common.excel.query.ExportPageQuery;
import cn.egenie.architect.common.function.util.ThrowUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lucien
 * @since 2021/01/19
 */
public class ExcelUtils {
    private static final String SPECIAL_CHAR = "[^0-9a-zA-Z\u4e00-\u9fa5-_]+";
    private static final String FILE_FOLDER_PATH = System.getProperty("user.dir");

    /**
     * 同步按模型读
     * 默认读取第一个sheet，Head占一行
     */
    public static <T> List<T> syncRead(InputStream inputStream, Class<T> clazz) {
        return EasyExcelFactory.read(inputStream)
                .sheet(0)
                .headRowNumber(1)
                .head(clazz)
                .doReadSync();
    }

    public static <T extends BaseRowModel> void asyncRead(InputStream inputStream, List<BaseExcelListener<T>> listeners) {
        ExcelReader excelReader = EasyExcel.read(inputStream).build();
        try {
            ReadSheet[] readSheets = new ReadSheet[listeners.size()];
            for (int i = 0; i < listeners.size(); i++) {
                BaseExcelListener<? extends BaseRowModel> listener = listeners.get(i);
                readSheets[i] = EasyExcel.readSheet(i)
                        .head(listener.getRowModel())
                        .registerReadListener(listener)
                        .build();
                listener.setSheetName(readSheets[i].getSheetName());
            }

            // 这里注意 一定要把readSheets一起传进去，不然有个问题就是03版的excel 会读取多次，浪费性能
            excelReader.read(readSheets);
        }
        finally {
            excelReader.finish();
        }
    }

    public static <T extends BaseRowModel> void write(HttpServletResponse response,
                                                      WriteSheetParam<T> writeParam,
                                                      String fileName) {
        write(response, Arrays.asList(writeParam), fileName);
    }

    public static <T extends BaseRowModel> void write(HttpServletResponse response,
                                                      List<WriteSheetParam<T>> writeParams,
                                                      String fileName) {
        validateWriteSheetParams(writeParams);
        Assert.throwIfBlank(fileName, "fileName必传");

        List<String> sheetNames = Funs.map(writeParams, WriteSheetParam::getSheetName);
        Set<String> sheetNameSet = new HashSet<>(sheetNames);
        Assert.throwIfTrue(sheetNames.size() != sheetNameSet.size(), "sheetName{}重复", JsonUtils.toJson(sheetNames));

        String name = getFileName(fileName);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Accept-Ranges", "bytes");
        String fileNameParam = ThrowUtils.submit(() -> URLEncoder.encode(name, "UTF-8"));
        response.setHeader("Content-Disposition", "attachment; filename=" + fileNameParam);

        ServletOutputStream outputStream = ThrowUtils.submit(response::getOutputStream);
        writeExcel(outputStream, writeParams, null);
    }

    private static <T extends BaseRowModel> void validateWriteSheetParams(List<WriteSheetParam<T>> writeParams) {
        Assert.throwIfEmpty(writeParams, "写入sheet参数必传");

        writeParams.forEach(writeParam -> {
            ValidatorUtils.validate(writeParam);

            DetailExportPageQuery detailExportPageQuery = writeParam.getDetailExportPageQuery();
            Function<DetailExportPageQuery, List<DetailRowModel>> detailDataListFun = writeParam.getDetailDataListFun();

            boolean isDetailQueryInValid = (detailExportPageQuery == null && detailDataListFun != null)
                    || (detailExportPageQuery != null && detailDataListFun == null);
            Assert.throwIfTrue(isDetailQueryInValid, "二级查询参数无效");
        });
    }

    private static String getFileName(String fileName) {
        fileName = fileName.replaceAll(SPECIAL_CHAR, "");
        String[] fileNames = fileName.split(Constants.POINT_SPLITTER);

        return fileNames.length > 1 ? fileNames[0] : fileNames[0] + Constants.POINT + ExcelTypeEnum.XLSX.getValue();
    }

    /**
     * @return Triple: File, 日期, userId
     */
    public static <T extends BaseRowModel> Triple<File, String, String> writeOnDisk(WriteSheetParam<T> writeParam,
                                                                                    String fileName,
                                                                                    BiConsumer<Integer, Integer> taskConsumer) {
        return writeOnDisk(Arrays.asList(writeParam), fileName, taskConsumer);
    }

    /**
     * @return Triple: File, 日期, userId
     */
    public static <T extends BaseRowModel> Triple<File, String, String> writeOnDisk(List<WriteSheetParam<T>> writeParams,
                                                                                    String fileName,
                                                                                    BiConsumer<Integer, Integer> taskConsumer) {
        validateWriteSheetParams(writeParams);

        long userId = writeParams.get(0).getExportPageQuery().getUserId();
        Date now = new Date();
        String today = TimeUtils.formatAsYYYYMMDD(now);

        String filePath = FILE_FOLDER_PATH
                + Constants.SLASH
                + fileName
                + Constants.MIDDLE_LINE
                + userId
                + Constants.MIDDLE_LINE
                + TimeUtils.format(now, TimeUtils.YYYYMMDDHHMMSSSSS)
                + ExcelTypeEnum.XLSX.getValue();
        File file = new File(filePath);
        writeExcel(ThrowUtils.submit(() -> new FileOutputStream(file)), writeParams, taskConsumer);

        return Triple.of(file, today, String.valueOf(userId));
    }

    private static <T extends BaseRowModel> void writeExcel(OutputStream outputStream,
                                                            List<WriteSheetParam<T>> writeParams,
                                                            BiConsumer<Integer, Integer> taskConsumer) {
        ExcelWriter excelWriter = EasyExcel.write(outputStream).build();

        int total = writeParams.stream()
                .mapToInt(writeParam -> writeParam.getTotalFun().apply(writeParam.getExportPageQuery()))
                .sum();
        CompletedInfo completedInfo = CompletedInfo.of(total, 0);

        try {
            for (int i = 0; i < writeParams.size(); i++) {
                WriteSheetParam<T> writeParam = writeParams.get(i);

                String sheetName = writeParam.getSheetName().replaceAll(SPECIAL_CHAR, "");
                ExcelWriterSheetBuilder writerSheetBuilder = EasyExcel.writerSheet(i, sheetName);

                List<String> headList = writeParam.getHeadList();
                writerSheetBuilder = writerSheetBuilder.head(Funs.map(headList, Arrays::asList));

                List<WriteHandler> writeHandlers = writeParam.getWriteHandlers();
                ExportMergeStrategy exportMergeStrategy = (ExportMergeStrategy) Funs.filterFirst(writeHandlers, writeHandler -> writeHandler instanceof ExportMergeStrategy);
                if (exportMergeStrategy != null) {
                    exportMergeStrategy.setMergeColumns(getMergeColumns(writeParam.getHeadList(), writeParam.getMergeHeadList()));
                }
                if (CollectionUtils.isNotEmpty(writeHandlers)) {
                    writeHandlers.forEach(writerSheetBuilder::registerWriteHandler);
                }

                WriteSheet writeSheet = writerSheetBuilder.build();

                Class<T> modelClass = writeParam.getModelClass();
                Map<String, Field> excelFieldMap = getExcelFieldMap(modelClass);
                Map<String, String> translationMap = writeParam.getTranslationMap();

                ExportPageQuery exportPageQuery = writeParam.getExportPageQuery();
                List<T> rowList = writeParam.getDataListFun().apply(exportPageQuery);

                MergeContainer mergeContainer = new MergeContainer(exportMergeStrategy);

                while (CollectionUtils.isNotEmpty(rowList)) {
                    DetailExportPageQuery detailExportPageQuery = writeParam.getDetailExportPageQuery();
                    if (detailExportPageQuery == null) {
                        excelWriter.write(buildRows(rowList, headList, excelFieldMap, translationMap), writeSheet);
                        if (!writeParam.isTotalByDetail() && taskConsumer != null) {
                            completedInfo.setCompletedNum(completedInfo.getCompletedNum() + rowList.size());
                            taskConsumer.accept(total, completedInfo.getCompletedNum());
                        }
                    }
                    else {
                        writeDetailRows(writeParam, excelFieldMap, translationMap, mergeContainer, writeSheet, excelWriter, rowList, detailExportPageQuery, completedInfo, taskConsumer);
                    }

                    if (rowList.size() < exportPageQuery.getPageSize()) {
                        break;
                    }

                    exportPageQuery.setPageNum(exportPageQuery.getPageNum() + 1);
                    rowList = writeParam.getDataListFun().apply(exportPageQuery);
                }
            }

            if (taskConsumer != null) {
                taskConsumer.accept(total, total);
            }
        }
        finally {
            excelWriter.finish();
        }
    }

    private static List<Integer> getMergeColumns(List<String> headList, List<String> mergeHeadList) {
        List<Integer> mergeColumns = new ArrayList<>();
        Set<String> mergeHeadSet = new HashSet<>(mergeHeadList);

        Funs.forEach(headList, (index, head) -> {
            if (mergeHeadSet.contains(head)) {
                mergeColumns.add(index);
            }
        });

        return mergeColumns;
    }


    private static <T extends BaseRowModel> Map<String, Field> getExcelFieldMap(Class<T> modelClass) {
        List<Field> excelFields = Funs.filter(
                Arrays.asList(modelClass.getDeclaredFields()),
                field -> field.getAnnotation(ExcelProperty.class) != null);

        return Funs.toMapQuietly(
                excelFields,
                excelField -> excelField.getAnnotation(ExcelProperty.class).value()[0],
                Function.identity());
    }

    private static <T extends BaseRowModel> List<T> buildRowModels(T row, Map<Long, List<DetailRowModel>> detailRowMap, Class<T> modelClass) {
        List<DetailRowModel> detailList = detailRowMap == null ? Collections.emptyList() : detailRowMap.get(row.getId());
        return CollectionUtils.isEmpty(detailList) ? Arrays.asList(row) : Funs.map(detailList, detailRow -> buildRowModel(row, detailRow, modelClass));
    }

    private static <T extends BaseRowModel> T buildRowModel(T row, DetailRowModel detailRowModel, Class<T> modelClass) {
        T model = JsonUtils.convertValue(row, modelClass);
        Map<String, Object> dynamicMap = model.getDynamicPropertyMap();
        if (dynamicMap == null) {
            dynamicMap = new HashMap<>(16);
            model.setDynamicPropertyMap(dynamicMap);
        }

        Map<String, Object> detailDynamicMap = detailRowModel.getDynamicPropertyMap();
        if (MapUtils.isNotEmpty(detailDynamicMap)) {
            dynamicMap.putAll(detailDynamicMap);
        }

        return model;
    }


    private static <T extends BaseRowModel> void writeDetailRows(WriteSheetParam<T> writeParam,
                                                                 Map<String, Field> excelFieldMap,
                                                                 Map<String, String> translationMap,
                                                                 MergeContainer mergeContainer,
                                                                 WriteSheet writeSheet,
                                                                 ExcelWriter excelWriter,
                                                                 List<T> rowList,
                                                                 DetailExportPageQuery detailExportPageQuery,
                                                                 CompletedInfo completedInfo,
                                                                 BiConsumer<Integer, Integer> taskConsumer) {
        List<String> headList = writeParam.getHeadList();
        Class<T> modelClass = writeParam.getModelClass();

        Map<Long, T> rowMap = Funs.toMapQuietly(rowList, BaseRowModel::getId, Function.identity());

        // 保留顺序
        List<Long> mainIds = Funs.map(rowList, BaseRowModel::getId);
        Map<Long, Integer> mainIdIndexMap = Funs.toMapQuietly(mainIds, t -> t, mainIds::indexOf);
        detailExportPageQuery.setMainIds(mainIds);

        // 强制从第一页查询，避免接入方错误
        detailExportPageQuery.setPageNum(1);
        List<DetailRowModel> detailDataList = writeParam.getDetailDataListFun().apply(detailExportPageQuery);
        detailDataList = detailDataList == null ? Collections.emptyList() : detailDataList;

        MainIdIndexContainer mainIdIndexContainer = new MainIdIndexContainer();
        Set<Long> processedMainIdSet = new HashSet<>();

        while (true) {
            if (writeParam.isOrderByDetail()) {
                detailDataList.forEach(detailRowModel -> processedMainIdSet.add(detailRowModel.getMainId()));
                List<T> pageData = Funs.map(detailDataList, detailModel -> buildRowModel(rowMap.get(detailModel.getMainId()), detailModel, modelClass));

                if (CollectionUtils.isNotEmpty(pageData)) {
                    excelWriter.write(buildRows(pageData, headList, excelFieldMap, translationMap), writeSheet);
                    if (writeParam.isTotalByDetail() && taskConsumer != null) {
                        completedInfo.setCompletedNum(completedInfo.getCompletedNum() + pageData.size());
                        taskConsumer.accept(completedInfo.getTotal(), completedInfo.getCompletedNum());
                    }
                }
            }
            else {
                List<T> pageData = new ArrayList<>(detailDataList.size());
                Map<Long, List<DetailRowModel>> detailMap = Funs.groupingBy(detailDataList, DetailRowModel::getMainId);

                // 注意这里只需要二级明细里面存在的row
                Funs.filter(rowList, row -> detailMap.containsKey(row.getId()))
                        .forEach(row -> {
                            Long mainId = row.getId();
                            Integer mainIdIndex = mainIdIndexMap.get(mainId);
                            fillRowsWithoutDetail(rowList, pageData, mainIdIndexContainer, mainIdIndex, mergeContainer, modelClass);

                            mainIdIndexContainer.setPreMainIdIndex(mainIdIndex);

                            mergeContainer.refreshMergeInfo(mainId, detailMap.get(mainId).size());
                            pageData.addAll(buildRowModels(row, detailMap, modelClass));
                        });
                if (CollectionUtils.isNotEmpty(pageData)) {
                    excelWriter.write(buildRows(pageData, headList, excelFieldMap, translationMap), writeSheet);
                }

                if (writeParam.isTotalByDetail() && taskConsumer != null) {
                    completedInfo.setCompletedNum(completedInfo.getCompletedNum() + pageData.size());
                    taskConsumer.accept(completedInfo.getTotal(), completedInfo.getCompletedNum());
                }
            }

            if (detailDataList.size() < detailExportPageQuery.getPageSize()) {
                if (writeParam.isOrderByDetail()) {
                    List<Long> notFillMainIds = Funs.filter(mainIds, mainId -> !processedMainIdSet.contains(mainId));
                    List<T> notFillMainRows = Funs.map(notFillMainIds, rowMap::get);
                    if (CollectionUtils.isNotEmpty(notFillMainRows)) {
                        mergeContainer.increaseRowNum(notFillMainRows.size());
                        excelWriter.write(buildRows(notFillMainRows, headList, excelFieldMap, translationMap), writeSheet);
                    }
                }
                else {
                    List<T> pageData = new ArrayList<>();
                    fillRowsWithoutDetail(rowList, pageData, mainIdIndexContainer, rowList.size(), mergeContainer, modelClass);

                    if (CollectionUtils.isNotEmpty(pageData)) {
                        excelWriter.write(buildRows(pageData, headList, excelFieldMap, translationMap), writeSheet);
                    }
                }

                break;
            }

            detailExportPageQuery.setPageNum(detailExportPageQuery.getPageNum() + 1);
            detailDataList = writeParam.getDetailDataListFun().apply(detailExportPageQuery);
            detailDataList = detailDataList == null ? Collections.emptyList() : detailDataList;
        }
    }

    private static <T extends BaseRowModel> List<Object> buildRows(List<T> rows,
                                                                   List<String> headList,
                                                                   Map<String, Field> excelFieldMap,
                                                                   Map<String, String> translationMap) {
        return Funs.map(rows, row -> {
            Map<String, Object> valueMap = new HashMap<>(16);
            excelFieldMap.forEach((k, v) -> {
                valueMap.put(k, ReflectUtils.getValue(row, v));
            });

            Map<String, Object> dynamicPropertyMap = row.getDynamicPropertyMap();
            if (MapUtils.isNotEmpty(dynamicPropertyMap)) {
                if (MapUtils.isEmpty(translationMap)) {
                    // 不需要翻译动态属性的key
                    valueMap.putAll(dynamicPropertyMap);
                }
                else {
                    dynamicPropertyMap.forEach((k, v) -> valueMap.put(translationMap.getOrDefault(k, k), v));
                }
            }

            List<Object> list = new ArrayList<>();
            headList.forEach(head -> list.add(valueMap.getOrDefault(head, Constants.EMPTY_STRING)));

            return list;
        });
    }

    /**
     * 填充没有二级明细的一级明细到pageData
     */
    private static <T extends BaseRowModel> void fillRowsWithoutDetail(List<T> rows,
                                                                       List<T> pageData,
                                                                       MainIdIndexContainer mainIdIndexContainer,
                                                                       Integer currentMainIdIndex,
                                                                       MergeContainer mergeContainer,
                                                                       Class<T> modelClass) {
        for (int i = mainIdIndexContainer.getPreMainIdIndex() + 1; i < currentMainIdIndex; i++) {
            T row = rows.get(i);
            Long mainId = row.getId();
            mergeContainer.refreshMergeInfo(mainId, 0);
            pageData.addAll(buildRowModels(row, null, modelClass));
        }
    }

    @Getter
    @Setter
    private static class MergeContainer {
        private ExportMergeStrategy exportMergeStrategy;
        private int currentRowNum;
        private Map<Long, Pair<Integer, Integer>> mergeMainIdMap = new LinkedHashMap<>(16);

        public MergeContainer(ExportMergeStrategy exportMergeStrategy) {
            this.exportMergeStrategy = exportMergeStrategy;
            if (exportMergeStrategy != null) {
                exportMergeStrategy.setMergeMainIdMap(mergeMainIdMap);
            }
        }

        public void increaseRowNum(int rowsNum) {
            currentRowNum += rowsNum;
        }

        public void refreshMergeInfo(Long mainId, int rowsNum) {
            // 一级明细至少一行
            rowsNum = rowsNum == 0 ? 1 : rowsNum;
            if (mergeMainIdMap.containsKey(mainId)) {
                Pair<Integer, Integer> pair = mergeMainIdMap.get(mainId);
                pair = Pair.of(pair.getLeft(), pair.getRight() + rowsNum);
                mergeMainIdMap.put(mainId, pair);
            }
            else {
                Pair<Integer, Integer> pair = Pair.of(currentRowNum + 1, currentRowNum + rowsNum);
                mergeMainIdMap.put(mainId, pair);
            }

            currentRowNum += rowsNum;
        }
    }

    @Getter
    @Setter
    private static class CompletedInfo {
        private int total;
        private int completedNum;

        public static CompletedInfo of(int total, int completedNum) {
            CompletedInfo completedInfo = new CompletedInfo();
            completedInfo.setTotal(total);
            completedInfo.setCompletedNum(completedNum);

            return completedInfo;
        }
    }

    @Getter
    @Setter
    private static class MainIdIndexContainer {
        private int preMainIdIndex = -1;
    }
}


