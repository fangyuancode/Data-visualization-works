package org.example.base_project.entity;

import java.math.BigDecimal;

public class TvDramaPlayQuery {
    private Integer current = 1;
    private Integer size = 10;

    private String dramaName;
    private String dramaType;

    private Long playCountMin;
    private Long playCountMax;

    private Long likeCountMin;
    private Long likeCountMax;

    private Long negativeCountMin;
    private Long negativeCountMax;

    private BigDecimal scoreMin;
    private BigDecimal scoreMax;

    private String collectStartTime;
    private String collectEndTime;

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

    public String getDramaName() {
        return dramaName;
    }

    public void setDramaName(String dramaName) {
        this.dramaName = dramaName;
    }

    public String getDramaType() {
        return dramaType;
    }

    public void setDramaType(String dramaType) {
        this.dramaType = dramaType;
    }

    public Long getPlayCountMin() {
        return playCountMin;
    }

    public void setPlayCountMin(Long playCountMin) {
        this.playCountMin = playCountMin;
    }

    public Long getPlayCountMax() {
        return playCountMax;
    }

    public void setPlayCountMax(Long playCountMax) {
        this.playCountMax = playCountMax;
    }

    public Long getLikeCountMin() {
        return likeCountMin;
    }

    public void setLikeCountMin(Long likeCountMin) {
        this.likeCountMin = likeCountMin;
    }

    public Long getLikeCountMax() {
        return likeCountMax;
    }

    public void setLikeCountMax(Long likeCountMax) {
        this.likeCountMax = likeCountMax;
    }

    public Long getNegativeCountMin() {
        return negativeCountMin;
    }

    public void setNegativeCountMin(Long negativeCountMin) {
        this.negativeCountMin = negativeCountMin;
    }

    public Long getNegativeCountMax() {
        return negativeCountMax;
    }

    public void setNegativeCountMax(Long negativeCountMax) {
        this.negativeCountMax = negativeCountMax;
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

    public String getCollectStartTime() {
        return collectStartTime;
    }

    public void setCollectStartTime(String collectStartTime) {
        this.collectStartTime = collectStartTime;
    }

    public String getCollectEndTime() {
        return collectEndTime;
    }

    public void setCollectEndTime(String collectEndTime) {
        this.collectEndTime = collectEndTime;
    }
}