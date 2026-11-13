package org.example.base_project.entity;

import java.util.List;

public class PageResult<T> {
    private List<T> records;
    private Integer current;
    private Integer size;
    private Long total;

    public PageResult() {
    }

    public PageResult(List<T> records, Integer current, Integer size, Long total) {
        this.records = records;
        this.current = current;
        this.size = size;
        this.total = total;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
