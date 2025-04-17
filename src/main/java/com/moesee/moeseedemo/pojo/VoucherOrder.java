package com.moesee.moeseedemo.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VoucherOrder {
    private Long id;
    private int userUid;
    private Long voucherId;
    private Integer payType;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime payTime;
    private LocalDateTime useTime;
    private LocalDateTime refundTime;
    private LocalDateTime updateTime;

    public VoucherOrder() {
    }

    public VoucherOrder(Long voucherId, Long id, int userId) {
        this.voucherId = voucherId;
        this.id = id;
        this.userUid = userUid;
    }
}
