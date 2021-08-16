package cn.egenie.architect.common.excel.merge;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import com.alibaba.excel.write.handler.AbstractRowWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lucien
 * @since 2021/07/19
 */
@Getter
@Setter
public class ExportMergeStrategy extends AbstractRowWriteHandler {
    /**
     * 需要merge的header所在列
     */
    private List<Integer> mergeColumns;

    /**
     * 需要merge的行，key是主表id，value是主标id对应的行
     */
    private Map<Long, Pair<Integer, Integer>> mergeMainIdMap;

    @Override
    public void afterRowDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Integer relativeRowIndex, Boolean isHead) {
        if (isHead != null && isHead) {
            return;
        }

        Sheet sheet = writeSheetHolder.getSheet();
        mergeMainIdMap.forEach((k, v) ->
                mergeColumns.forEach(column -> {
                            boolean isMergeArea = v.getRight() > v.getLeft()
                                    && row.getRowNum() > v.getLeft()
                                    && row.getRowNum() <= v.getRight();
                            if (isMergeArea) {
                                // 清除要合并的列的值
                                row.getCell(column).setCellValue("");

                                if (row.getRowNum() == v.getRight()) {
                                    CellRangeAddress cellRangeAddress = new CellRangeAddress(v.getLeft(), v.getRight(), column, column);
                                    sheet.addMergedRegion(cellRangeAddress);
                                }
                            }
                        }
                ));
    }
}
