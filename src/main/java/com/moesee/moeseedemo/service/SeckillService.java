package com.moesee.moeseedemo.service;

import com.moesee.moeseedemo.dto.SeckillVoucherDTO;

public interface SeckillService {
    boolean addSeckillVoucher(SeckillVoucherDTO seckillVoucherDTO);
    Long trySeckill(Long voucherId, int userUid);
}
