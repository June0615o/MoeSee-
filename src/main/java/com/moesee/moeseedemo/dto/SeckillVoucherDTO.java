package com.moesee.moeseedemo.dto;

public class SeckillVoucherDTO {
    private Long voucherId;
    private Integer stock;
    private String beginTime;
    private String endTime;

    public SeckillVoucherDTO() {
    }

    public SeckillVoucherDTO(Long voucherId, Integer stock, String beginTime, String endTime) {
        this.voucherId = voucherId;
        this.stock = stock;
        this.beginTime = beginTime;
        this.endTime = endTime;
    }

    public Long getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(Long voucherId) {
        this.voucherId = voucherId;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
