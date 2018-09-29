package com.imooc.utils;

import java.util.List;

/**
 * 封装分页后的数据格式
 * @author erpljq
 * @date 2018/9/20
 */
public class PagedResult {

    private int page;       //当前页数
    private int total;      //总页数
    private long records;   //总记录数
    private List<?> row;    //每行显示的内容


    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public long getRecords() {
        return records;
    }

    public void setRecords(long records) {
        this.records = records;
    }

    public List<?> getRow() {
        return row;
    }

    public void setRow(List<?> row) {
        this.row = row;
    }
}
