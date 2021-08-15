package cn.egenie.architect.common.core.query;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lucien
 * @since 2021/01/07
 */
@Getter
@Setter
public class PageQuery {

    private Integer pageNum;

    private Integer pageSize;

    public static PageQuery of(int pageNum, int pageSize) {
        PageQuery pageQuery = new PageQuery();
        pageQuery.setPageNum(pageNum);
        pageQuery.setPageSize(pageSize);

        return pageQuery;
    }

    public void validate() {
        pageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        pageSize = pageSize == null || pageSize < 1 ? 20 : pageSize;
    }

    /**
     * 请求之前保证先validate
     */
    public Integer getOffset() {
        return (pageNum - 1) * pageSize;
    }

    @Override
    public String toString() {
        return "PageQuery{" +
                "pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                '}';
    }
}
