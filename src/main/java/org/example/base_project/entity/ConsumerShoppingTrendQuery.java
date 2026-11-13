package org.example.base_project.entity;

import java.math.BigDecimal;

public class ConsumerShoppingTrendQuery {
    private Integer current = 1;
    private Integer size = 10;

    private String gender;
    private String cityTier;
    private String shoppingPreference;

    private Integer ageMin;
    private Integer ageMax;

    private BigDecimal incomeMin;
    private BigDecimal incomeMax;

    private Integer ordersMin;
    private Integer ordersMax;

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

    public String getCityTier() {
        return cityTier;
    }

    public void setCityTier(String cityTier) {
        this.cityTier = cityTier;
    }

    public String getShoppingPreference() {
        return shoppingPreference;
    }

    public void setShoppingPreference(String shoppingPreference) {
        this.shoppingPreference = shoppingPreference;
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

    public BigDecimal getIncomeMin() {
        return incomeMin;
    }

    public void setIncomeMin(BigDecimal incomeMin) {
        this.incomeMin = incomeMin;
    }

    public BigDecimal getIncomeMax() {
        return incomeMax;
    }

    public void setIncomeMax(BigDecimal incomeMax) {
        this.incomeMax = incomeMax;
    }

    public Integer getOrdersMin() {
        return ordersMin;
    }

    public void setOrdersMin(Integer ordersMin) {
        this.ordersMin = ordersMin;
    }

    public Integer getOrdersMax() {
        return ordersMax;
    }

    public void setOrdersMax(Integer ordersMax) {
        this.ordersMax = ordersMax;
    }
}