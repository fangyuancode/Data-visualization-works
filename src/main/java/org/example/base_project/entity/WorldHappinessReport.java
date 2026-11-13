package org.example.base_project.entity;

import java.math.BigDecimal;

/**
 * 世界幸福报告数据实体
 */
public class WorldHappinessReport {
    public Long id;

    public Integer year;
    public Integer rankInYear;
    public String country;

    public BigDecimal happinessScore;
    public BigDecimal lowerWhisker;
    public BigDecimal upperWhisker;

    public BigDecimal explainedLogGdpPerCapita;
    public BigDecimal explainedSocialSupport;
    public BigDecimal explainedHealthyLifeExpectancy;
    public BigDecimal explainedFreedom;
    public BigDecimal explainedGenerosity;
    public BigDecimal explainedCorruption;
    public BigDecimal dystopiaPlusResidual;

    /**
     * 是否为可视化分析完整数据，由 SQL 查询动态计算，不是数据库真实字段
     */
    public Boolean completeData;

    public String createTime;
}