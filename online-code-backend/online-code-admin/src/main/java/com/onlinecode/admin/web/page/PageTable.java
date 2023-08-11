package com.onlinecode.admin.web.page;

import com.onlinecode.admin.web.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 表格分页数据对象
 *
 * @author ruoyi
 */
public class PageTable implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 列表数据
     */
    private List<?> rows;

    /**
     * 表格数据对象
     */
    public PageTable() {
    }

    public PageTable total(long total) {
        this.total = total;
        return this;
    }

    public PageTable rows(List<?> rows) {
        this.rows = rows;
        return this;
    }

    public static PageTable empty() {
        return new PageTable().total(0).rows(new ArrayList<>());
    }

    public static PageTable page(long total, List<?> rows) {
        return new PageTable().total(total).rows(rows);
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<?> rows) {
        this.rows = rows;
    }
}
