package org.example.springboot.service;

import org.example.springboot.entity.PaymentOrder;
import org.example.springboot.entity.Appointment;
import org.example.springboot.mapper.PaymentOrderMapper;
import org.example.springboot.mapper.AppointmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentOrderMapper paymentOrderMapper;
    
    @Autowired
    private AppointmentMapper appointmentMapper;

    public PaymentOrder createOrder(Long appointmentId, BigDecimal amount) {
        PaymentOrder order = new PaymentOrder();
        order.setOrderNo("ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4));
        order.setAppointmentId(appointmentId);
        order.setAmount(amount);
        order.setPaymentMethod("simulate");
        order.setPaymentStatus(0);
        order.setExpireTime(LocalDateTime.now().plusMinutes(30));
        paymentOrderMapper.insert(order);
        return order;
    }

    public PaymentOrder getOrderByNo(String orderNo) {
        return paymentOrderMapper.selectByOrderNo(orderNo);
    }

    @Transactional
    public boolean mockPaySuccess(String orderNo) {
        // 更新支付状态
        int rows = paymentOrderMapper.updatePaymentStatus(orderNo, 1, "MOCK_" + System.currentTimeMillis(), LocalDateTime.now());
        
        if (rows > 0) {
            // 获取支付订单
            PaymentOrder order = paymentOrderMapper.selectByOrderNo(orderNo);
            if (order != null && order.getAppointmentId() != null) {
                // 更新预约状态为已支付
                Appointment appointment = appointmentMapper.selectById(order.getAppointmentId());
                if (appointment != null) {
                    appointment.setStatus(1); // 1表示已支付
                    appointment.setUpdateTime(LocalDateTime.now());
                    appointmentMapper.updateById(appointment);
                }
            }
        }
        
        return rows > 0;
    }
    
    public void deleteByAppointmentId(Long appointmentId) {
        paymentOrderMapper.deleteByAppointmentId(appointmentId);
    }
}