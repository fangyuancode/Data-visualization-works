package org.example.base_project.entity;

import java.math.BigDecimal;

/**
 * 孕妇NIPT检测数据实体
 */
public class MaternalNiptData {
    public Long id;

    public String fetusGender;
    public Integer serialNo;
    public String pregnantWomanCode;

    public Integer age;
    public BigDecimal height;
    public BigDecimal weight;

    public String lastMenstrualPeriod;
    public String ivfPregnancy;
    public String testDate;
    public Integer bloodDrawCount;

    public String gestationalWeek;
    public Integer gestationalDays;
    public BigDecimal bmi;

    public Long rawReadCount;
    public BigDecimal mappingRatio;
    public BigDecimal duplicateReadRatio;
    public Long uniqueMappedReads;
    public BigDecimal gcContent;

    public BigDecimal chr13ZValue;
    public BigDecimal chr18ZValue;
    public BigDecimal chr21ZValue;
    public BigDecimal chrXZValue;
    public BigDecimal chrYZValue;

    public BigDecimal chrYConcentration;
    public BigDecimal chrXConcentration;

    public BigDecimal chr13GcContent;
    public BigDecimal chr18GcContent;
    public BigDecimal chr21GcContent;

    public BigDecimal filteredReadRatio;
    public String chromosomeAneuploidy;

    public String pregnancyCount;
    public Integer productionCount;
    public String fetusHealthy;

    public String createTime;
}