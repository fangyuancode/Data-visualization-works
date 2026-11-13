package org.example.base_project.entity;

import java.math.BigDecimal;

public class TeenMentalHealthQuery {
    private Integer current = 1;
    private Integer size = 10;

    private String gender;
    private String platformUsage;
    private String socialInteractionLevel;
    private Integer depressionLabel;

    private Integer ageMin;
    private Integer ageMax;

    private BigDecimal dailySocialMediaHoursMin;
    private BigDecimal dailySocialMediaHoursMax;

    private BigDecimal sleepHoursMin;
    private BigDecimal sleepHoursMax;

    private BigDecimal academicPerformanceMin;
    private BigDecimal academicPerformanceMax;

    private Integer stressLevel;
    private Integer anxietyLevel;
    private Integer addictionLevel;

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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPlatformUsage() {
        return platformUsage;
    }

    public void setPlatformUsage(String platformUsage) {
        this.platformUsage = platformUsage;
    }

    public String getSocialInteractionLevel() {
        return socialInteractionLevel;
    }

    public void setSocialInteractionLevel(String socialInteractionLevel) {
        this.socialInteractionLevel = socialInteractionLevel;
    }

    public Integer getDepressionLabel() {
        return depressionLabel;
    }

    public void setDepressionLabel(Integer depressionLabel) {
        this.depressionLabel = depressionLabel;
    }

    public Integer getAgeMin() {
        return ageMin;
    }

    public void setAgeMin(Integer ageMin) {
        this.ageMin = ageMin;
    }

    public Integer getAgeMax() {
        return ageMax;
    }

    public void setAgeMax(Integer ageMax) {
        this.ageMax = ageMax;
    }

    public BigDecimal getDailySocialMediaHoursMin() {
        return dailySocialMediaHoursMin;
    }

    public void setDailySocialMediaHoursMin(BigDecimal dailySocialMediaHoursMin) {
        this.dailySocialMediaHoursMin = dailySocialMediaHoursMin;
    }

    public BigDecimal getDailySocialMediaHoursMax() {
        return dailySocialMediaHoursMax;
    }

    public void setDailySocialMediaHoursMax(BigDecimal dailySocialMediaHoursMax) {
        this.dailySocialMediaHoursMax = dailySocialMediaHoursMax;
    }

    public BigDecimal getSleepHoursMin() {
        return sleepHoursMin;
    }

    public void setSleepHoursMin(BigDecimal sleepHoursMin) {
        this.sleepHoursMin = sleepHoursMin;
    }

    public BigDecimal getSleepHoursMax() {
        return sleepHoursMax;
    }

    public void setSleepHoursMax(BigDecimal sleepHoursMax) {
        this.sleepHoursMax = sleepHoursMax;
    }

    public BigDecimal getAcademicPerformanceMin() {
        return academicPerformanceMin;
    }

    public void setAcademicPerformanceMin(BigDecimal academicPerformanceMin) {
        this.academicPerformanceMin = academicPerformanceMin;
    }

    public BigDecimal getAcademicPerformanceMax() {
        return academicPerformanceMax;
    }

    public void setAcademicPerformanceMax(BigDecimal academicPerformanceMax) {
        this.academicPerformanceMax = academicPerformanceMax;
    }

    public Integer getStressLevel() {
        return stressLevel;
    }

    public void setStressLevel(Integer stressLevel) {
        this.stressLevel = stressLevel;
    }

    public Integer getAnxietyLevel() {
        return anxietyLevel;
    }

    public void setAnxietyLevel(Integer anxietyLevel) {
        this.anxietyLevel = anxietyLevel;
    }

    public Integer getAddictionLevel() {
        return addictionLevel;
    }

    public void setAddictionLevel(Integer addictionLevel) {
        this.addictionLevel = addictionLevel;
    }
}