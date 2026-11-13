package org.example.base_project.entity;

import java.math.BigDecimal;

public class WorldHappinessReportQuery {
    private Integer current = 1;
    private Integer size = 10;

    private Integer year;
    private String country;

    private Integer rankMin;
    private Integer rankMax;

    private BigDecimal scoreMin;
    private BigDecimal scoreMax;

    /**
     * 是否只查询核心字段完整的数据
     */
    private Boolean completeOnly;

    public Integer getOffset() {
        int page = current == null || current < 1 ? 1 : current;
        int pageSize = size == null || size < 1 ? 10 : size;
        return (page - 1) * pageSize;
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

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getRankMin() {
        return rankMin;
    }

    public void setRankMin(Integer rankMin) {
        this.rankMin = rankMin;
    }

    public Integer getRankMax() {
        return rankMax;
    }

    public void setRankMax(Integer rankMax) {
        this.rankMax = rankMax;
    }

    public BigDecimal getScoreMin() {
        return scoreMin;
    }

    public void setScoreMin(BigDecimal scoreMin) {
        this.scoreMin = scoreMin;
    }

    public BigDecimal getScoreMax() {
        return scoreMax;
    }

    public void setScoreMax(BigDecimal scoreMax) {
        this.scoreMax = scoreMax;
    }

    public Boolean getCompleteOnly() {
        return completeOnly;
    }

    public void setCompleteOnly(Boolean completeOnly) {
        this.completeOnly = completeOnly;
    }
}