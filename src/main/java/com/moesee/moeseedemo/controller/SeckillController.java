package com.moesee.moeseedemo.controller;

import com.moesee.moeseedemo.dto.SeckillVoucherDTO;
import com.moesee.moeseedemo.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/seckill")
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    @PostMapping("/addVoucher")
    public ResponseEntity<?> addSeckillVoucher(@RequestBody SeckillVoucherDTO seckillVoucherDTO){
        boolean success = seckillService.addSeckillVoucher(seckillVoucherDTO);
        if(success){
            return ResponseEntity.ok("秒杀券添加成功");
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("秒杀券添加失败");
        }
    }

    /*
    核心业务:秒杀下单
    @Param voucherId 秒杀券的ID
    @Param userUid 用户的Uid
    @return 秒杀结果
     */
    @PostMapping("/order/{voucherId}")
    public ResponseEntity<?> placeOrder(@PathVariable("voucherId")Long voucherId,
                                        @RequestBody int userUid) {
        Long orderId = seckillService.trySeckill(voucherId,userUid);
        if (orderId > 0) {
            return ResponseEntity.ok("秒杀成功!请前往订单界面查看.订单编号:"+orderId);
        } else if (orderId == -1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("秒杀未开始或已结束");
        } else if (orderId == -2) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("库存不足");
        } else if (orderId == -3) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("已购买过该秒杀券");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("秒杀失败，请稍后再试");
        }
    }
}
