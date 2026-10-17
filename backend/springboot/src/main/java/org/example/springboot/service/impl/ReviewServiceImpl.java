package org.example.springboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.springboot.entity.Appointment;
import org.example.springboot.entity.Review;
import org.example.springboot.mapper.AppointmentMapper;
import org.example.springboot.mapper.ReviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> {
    
    @Autowired
    private AppointmentMapper appointmentMapper;
    
    /**
     * 添加评价
     */
    @Transactional
    public boolean addReview(Review review) {
        // 1. 检查是否重复评价
        Long count = lambdaQuery()
            .eq(Review::getAppointmentId, review.getAppointmentId())
            .count();
        if (count > 0) {
            throw new RuntimeException("该预约已评价过");
        }
        
        // 2. 检查预约是否存在且已完成
        if (review.getAppointmentId() != null) {
            Appointment appointment = appointmentMapper.selectById(review.getAppointmentId());
            if (appointment == null) {
                throw new RuntimeException("预约不存在");
            }
            // 3. 检查预约状态是否为已就诊（status = 2 表示已就诊）
            Integer status = appointment.getStatus();
            if (status == null || status != 2) {
                throw new RuntimeException("只有已就诊的预约才能评价，当前预约状态：" + status);
            }
            // 4. 从预约中获取患者ID和医生ID
            review.setPatientId(appointment.getPatientId());
            review.setDoctorId(appointment.getDoctorId());
        } else {
            throw new RuntimeException("预约ID不能为空");
        }
        
        // 3. 设置默认状态为已发布
        if (review.getStatus() == null) {
            review.setStatus(1);
        }
        
        return save(review);
    }
    
    /**
     * 获取医生平均评分
     */
    public Double getDoctorAverageRating(Long doctorId) {
        if (doctorId == null) {
            return 0.0;
        }
        try {
            return baseMapper.getAverageRatingByDoctorId(doctorId);
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    /**
     * 获取医生评价数量
     */
    public Integer getDoctorReviewCount(Long doctorId) {
        if (doctorId == null) {
            return 0;
        }
        try {
            Integer count = baseMapper.getReviewCountByDoctorId(doctorId);
            return count != null ? count : 0;
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * 获取医生的评价列表
     */
    public List<Review> getReviewsByDoctorId(Long doctorId) {
        if (doctorId == null) {
            return List.of();
        }
        return lambdaQuery()
            .eq(Review::getDoctorId, doctorId)
            .eq(Review::getStatus, 1)
            .orderByDesc(Review::getCreateTime)
            .list();
    }
    
    /**
     * 获取患者的评价列表
     */
    public List<Review> getReviewsByPatientId(Long patientId) {
        if (patientId == null) {
            return List.of();
        }
        return lambdaQuery()
            .eq(Review::getPatientId, patientId)
            .eq(Review::getStatus, 1)
            .orderByDesc(Review::getCreateTime)
            .list();
    }
    
    /**
     * 医生回复评价
     */
    @Transactional
    public boolean replyReview(Long reviewId, String reply) {
        Review review = getById(reviewId);
        if (review == null) {
            throw new RuntimeException("评价不存在");
        }
        review.setReply(reply);
        return updateById(review);
    }
}