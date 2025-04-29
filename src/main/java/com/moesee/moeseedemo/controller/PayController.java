package com.moesee.moeseedemo.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class PayController {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @PostMapping("/pay")
    public ResponseEntity<String> processPayment(@RequestBody Map<String, Object> requestBody) {
        Long orderId = Long.valueOf(requestBody.get("orderId").toString());
        Long voucherId = Long.valueOf(requestBody.get("voucherId").toString());
        String userUid = requestBody.get("userUid").toString();
        boolean isSuccess = Boolean.parseBoolean(requestBody.get("isSuccess").toString());
        Map<String,String> message = new HashMap<>();
        message.put("Id",orderId.toString());
        message.put("voucherId",voucherId.toString());
        message.put("userUid",String.valueOf(userUid));
        if(isSuccess) {
            rabbitTemplate.convertAndSend("order.direct", "order.success", message);
            return ResponseEntity.ok("支付成功");
        }else{
            rabbitTemplate.convertAndSend("order.direct", "order.fail", message);
            return ResponseEntity.ok("支付失败");
        }
    }
}
