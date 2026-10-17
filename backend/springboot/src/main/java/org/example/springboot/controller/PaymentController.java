package org.example.springboot.controller;

import org.example.springboot.entity.PaymentOrder;
import org.example.springboot.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/test")
    public String test() {
        return "PaymentController is working!";
    }

    @PostMapping("/create")
    public Map<String, Object> createOrder(@RequestBody Map<String, Object> req) {
        Long appointmentId = Long.valueOf(req.get("appointmentId").toString());
        BigDecimal amount = new BigDecimal(req.get("amount").toString());
        PaymentOrder order = paymentService.createOrder(appointmentId, amount);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        Map<String, Object> data = new HashMap<>();
        data.put("orderNo", order.getOrderNo());
        data.put("amount", order.getAmount());
        data.put("paymentStatus", order.getPaymentStatus());
        data.put("expireTime", order.getExpireTime());
        result.put("data", data);
        return result;
    }

    @GetMapping("/query/{orderNo}")
    public Map<String, Object> queryPayment(@PathVariable String orderNo) {
        PaymentOrder order = paymentService.getOrderByNo(orderNo);
        Map<String, Object> result = new HashMap<>();
        if (order == null) {
            result.put("code", 404);
            result.put("message", "订单不存在");
            return result;
        }
        result.put("code", 200);
        result.put("message", "success");
        Map<String, Object> data = new HashMap<>();
        data.put("orderNo", order.getOrderNo());
        data.put("paymentStatus", order.getPaymentStatus());
        data.put("transactionId", order.getTransactionId());
        data.put("paymentTime", order.getPaymentTime());
        result.put("data", data);
        return result;
    }

    @PostMapping("/mock-callback")
    public Map<String, Object> mockCallback(@RequestBody Map<String, String> req) {
        String orderNo = req.get("orderNo");
        String result = req.get("result");
        Map<String, Object> res = new HashMap<>();
        if ("SUCCESS".equals(result)) {
            paymentService.mockPaySuccess(orderNo);
            res.put("code", 200);
            res.put("message", "支付状态已更新为成功");
        } else {
            res.put("code", 500);
            res.put("message", "支付失败");
        }
        return res;
    }
}