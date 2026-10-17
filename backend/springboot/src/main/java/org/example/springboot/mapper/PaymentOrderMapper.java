package org.example.springboot.mapper;

import org.apache.ibatis.annotations.*;
import org.example.springboot.entity.PaymentOrder;
import java.time.LocalDateTime;   // 添加这行

@Mapper
public interface PaymentOrderMapper {

    @Insert("INSERT INTO payment_order(order_no, appointment_id, amount, payment_method, payment_status, expire_time) " +
            "VALUES(#{orderNo}, #{appointmentId}, #{amount}, #{paymentMethod}, #{paymentStatus}, #{expireTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PaymentOrder order);

    @Select("SELECT * FROM payment_order WHERE order_no = #{orderNo}")
    PaymentOrder selectByOrderNo(String orderNo);

    @Update("UPDATE payment_order SET payment_status = #{status}, transaction_id = #{transactionId}, payment_time = #{paymentTime} WHERE order_no = #{orderNo}")
    int updatePaymentStatus(@Param("orderNo") String orderNo, @Param("status") Integer status,
                            @Param("transactionId") String transactionId, @Param("paymentTime") LocalDateTime paymentTime);
    
    @Delete("DELETE FROM payment_order WHERE appointment_id = #{appointmentId}")
    int deleteByAppointmentId(@Param("appointmentId") Long appointmentId);
}