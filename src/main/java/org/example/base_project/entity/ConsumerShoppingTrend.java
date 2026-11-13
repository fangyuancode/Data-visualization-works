package org.example.base_project.entity;

import java.math.BigDecimal;

/**
 * 消费者购物趋势分析数据实体
 *
 * 说明：
 * 为减少样板代码，这里使用 public 字段。
 * MyBatis 和 Jackson 都可以正常读写这些字段。
 */
public class ConsumerShoppingTrend {
    public Long id;

    public Integer age;
    public BigDecimal monthlyIncome;
    public BigDecimal dailyInternetHours;
    public Integer smartphoneUsageYears;
    public BigDecimal socialMediaHours;

    public Integer onlinePaymentTrustScore;
    public Integer techSavvyScore;

    public Integer monthlyOnlineOrders;
    public Integer monthlyStoreVisits;

    public BigDecimal avgOnlineSpend;
    public BigDecimal avgStoreSpend;

    public Integer discountSensitivity;
    public Integer returnFrequency;
    public Integer avgDeliveryDays;
    public Integer deliveryFeeSensitivity;
    public Integer freeReturnImportance;
    public Integer productAvailabilityOnline;

    public Integer impulseBuyingScore;
    public Integer needTouchFeelScore;
    public Integer brandLoyaltyScore;
    public Integer environmentalAwareness;
    public Integer timePressureLevel;

    public String gender;
    public String cityTier;
    public String shoppingPreference;

    public String createTime;
}