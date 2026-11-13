package org.example.base_project.entity;

import java.math.BigDecimal;

/**
 * 社交媒体对青少年心理健康影响数据实体
 */
public class TeenMentalHealth {
    public Long id;

    public Integer age;
    public String gender;

    public BigDecimal dailySocialMediaHours;
    public String platformUsage;

    public BigDecimal sleepHours;
    public BigDecimal screenTimeBeforeSleep;

    public BigDecimal academicPerformance;
    public BigDecimal physicalActivity;

    public String socialInteractionLevel;

    public Integer stressLevel;
    public Integer anxietyLevel;
    public Integer addictionLevel;

    public Integer depressionLabel;

    public String createTime;
}