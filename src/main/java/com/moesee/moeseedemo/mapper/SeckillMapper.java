package com.moesee.moeseedemo.mapper;

import com.moesee.moeseedemo.dto.SeckillVoucherDTO;
import com.moesee.moeseedemo.pojo.VoucherOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SeckillMapper {

    int insertSeckillVoucher(SeckillVoucherDTO seckillVoucherDTO);

    void decrementStock(Long voucherId);
    void createOrder(VoucherOrder voucherOrder);

}
