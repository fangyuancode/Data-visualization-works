package org.example.base_project.entity;

import java.math.BigDecimal;

public class MaternalNiptQuery {
    private Integer current = 1;
    private Integer size = 10;

    private String fetusGender;
    private String pregnantWomanCode;
    private String fetusHealthy;
    private String chromosomeAneuploidy;

    private Integer ageMin;
    private Integer ageMax;

    private BigDecimal bmiMin;
    private BigDecimal bmiMax;

    private Integer gestationalDaysMin;
    private Integer gestationalDaysMax;

    private String testStartDate;
    private String testEndDate;

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

    public String getFetusGender() {
        return fetusGender;
    }

    public void setFetusGender(String fetusGender) {
        this.fetusGender = fetusGender;
    }

    public String getPregnantWomanCode() {
        return pregnantWomanCode;
    }

    public void setPregnantWomanCode(String pregnantWomanCode) {
        this.pregnantWomanCode = pregnantWomanCode;
    }

    public String getFetusHealthy() {
        return fetusHealthy;
    }

    public void setFetusHealthy(String fetusHealthy) {
        this.fetusHealthy = fetusHealthy;
    }

    public String getChromosomeAneuploidy() {
        return chromosomeAneuploidy;
    }

    public void setChromosomeAneuploidy(String chromosomeAneuploidy) {
        this.chromosomeAneuploidy = chromosomeAneuploidy;
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

    public BigDecimal getBmiMin() {
        return bmiMin;
    }

    public void setBmiMin(BigDecimal bmiMin) {
        this.bmiMin = bmiMin;
    }

    public BigDecimal getBmiMax() {
        return bmiMax;
    }

    public void setBmiMax(BigDecimal bmiMax) {
        this.bmiMax = bmiMax;
    }

    public Integer getGestationalDaysMin() {
        return gestationalDaysMin;
    }

    public void setGestationalDaysMin(Integer gestationalDaysMin) {
        this.gestationalDaysMin = gestationalDaysMin;
    }

    public Integer getGestationalDaysMax() {
        return gestationalDaysMax;
    }

    public void setGestationalDaysMax(Integer gestationalDaysMax) {
        this.gestationalDaysMax = gestationalDaysMax;
    }

    public String getTestStartDate() {
        return testStartDate;
    }

    public void setTestStartDate(String testStartDate) {
        this.testStartDate = testStartDate;
    }

    public String getTestEndDate() {
        return testEndDate;
    }

    public void setTestEndDate(String testEndDate) {
        this.testEndDate = testEndDate;
    }
}