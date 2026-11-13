package org.example.base_project.entity;

import java.math.BigDecimal;

/**
 * 租房数据实体
 */
public class RentalHouseData {
    public Long id;

    public Long sourceId;

    public BigDecimal rent;
    public Integer bedroom;
    public Integer livingroom;
    public Integer bathroom;
    public BigDecimal area;

    public String room;
    public String floorGrp;
    public String subway;
    public String region;
    public String heating;

    public String createTime;
}