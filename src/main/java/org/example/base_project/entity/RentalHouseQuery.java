package org.example.base_project.entity;

import java.math.BigDecimal;

public class RentalHouseQuery {
    private Integer current = 1;
    private Integer size = 10;

    private String room;
    private String floorGrp;
    private String subway;
    private String region;
    private String heating;

    private Integer bedroom;
    private Integer livingroom;
    private Integer bathroom;

    private BigDecimal rentMin;
    private BigDecimal rentMax;

    private BigDecimal areaMin;
    private BigDecimal areaMax;

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

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getFloorGrp() {
        return floorGrp;
    }

    public void setFloorGrp(String floorGrp) {
        this.floorGrp = floorGrp;
    }

    public String getSubway() {
        return subway;
    }

    public void setSubway(String subway) {
        this.subway = subway;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getHeating() {
        return heating;
    }

    public void setHeating(String heating) {
        this.heating = heating;
    }

    public Integer getBedroom() {
        return bedroom;
    }

    public void setBedroom(Integer bedroom) {
        this.bedroom = bedroom;
    }

    public Integer getLivingroom() {
        return livingroom;
    }

    public void setLivingroom(Integer livingroom) {
        this.livingroom = livingroom;
    }

    public Integer getBathroom() {
        return bathroom;
    }

    public void setBathroom(Integer bathroom) {
        this.bathroom = bathroom;
    }

    public BigDecimal getRentMin() {
        return rentMin;
    }

    public void setRentMin(BigDecimal rentMin) {
        this.rentMin = rentMin;
    }

    public BigDecimal getRentMax() {
        return rentMax;
    }

    public void setRentMax(BigDecimal rentMax) {
        this.rentMax = rentMax;
    }

    public BigDecimal getAreaMin() {
        return areaMin;
    }

    public void setAreaMin(BigDecimal areaMin) {
        this.areaMin = areaMin;
    }

    public BigDecimal getAreaMax() {
        return areaMax;
    }

    public void setAreaMax(BigDecimal areaMax) {
        this.areaMax = areaMax;
    }
}