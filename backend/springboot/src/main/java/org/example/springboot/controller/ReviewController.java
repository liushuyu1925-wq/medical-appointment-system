package org.example.springboot.controller;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.springboot.common.Result;
import org.example.springboot.entity.Review;
import org.example.springboot.service.impl.ReviewServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/review")
@Tag(name = "评价管理", description = "评价相关接口")
@ApiSupport(order = 1)
public class ReviewController {
    
    @Autowired
    private ReviewServiceImpl reviewService;
    
    @PostMapping("/add")
    @Operation(summary = "添加评价", description = "患者对已完成预约的医生进行评价")
    public Result addReview(@RequestBody Review review) {
        try {
            reviewService.addReview(review);
            return Result.success("评价成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/doctor/{doctorId}/rating")
    @Operation(summary = "查询医生评分", description = "获取医生的平均评分和评价总数")
    public Result getDoctorRating(
            @Parameter(description = "医生ID", required = true, example = "1")
            @PathVariable Long doctorId) {
        try {
            Double avgRating = reviewService.getDoctorAverageRating(doctorId);
            Integer count = reviewService.getDoctorReviewCount(doctorId);
            
            Map<String, Object> data = new HashMap<>();
            data.put("avgRating", avgRating != null ? avgRating : 0);
            data.put("reviewCount", count != null ? count : 0);
            
            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/doctor/{doctorId}/list")
    @Operation(summary = "查询医生评价列表", description = "获取某个医生的所有评价记录")
    public Result getDoctorReviews(
            @Parameter(description = "医生ID", required = true, example = "1")
            @PathVariable Long doctorId) {
        try {
            List<Review> list = reviewService.getReviewsByDoctorId(doctorId);
            return Result.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/patient/{patientId}/list")
    @Operation(summary = "查询患者评价列表", description = "获取某个患者的所有评价记录")
    public Result getPatientReviews(
            @Parameter(description = "患者ID", required = true, example = "1")
            @PathVariable Long patientId) {
        try {
            List<Review> list = reviewService.getReviewsByPatientId(patientId);
            return Result.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/reply")
    @Operation(summary = "医生回复评价", description = "医生对患者的评价进行回复")
    public Result replyReview(
            @Parameter(description = "评价ID", required = true, example = "1")
            @RequestParam Long reviewId,
            @Parameter(description = "回复内容", required = true, example = "感谢您的认可，祝您健康！")
            @RequestParam String reply) {
        try {
            reviewService.replyReview(reviewId, reply);
            return Result.success("回复成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "查询单条评价详情", description = "根据ID查询评价详细信息")
    public Result getById(
            @Parameter(description = "评价ID", required = true, example = "1")
            @PathVariable Long id) {
        try {
            Review review = reviewService.getById(id);
            if (review != null) {
                return Result.success(review);
            }
            return Result.error("评价不存在");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除评价", description = "根据ID删除评价记录")
    public Result deleteById(
            @Parameter(description = "评价ID", required = true, example = "1")
            @PathVariable Long id) {
        try {
            boolean success = reviewService.removeById(id);
            if (success) {
                return Result.success("删除成功");
            }
            return Result.error("删除失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/test/add")
    @Operation(summary = "测试添加评价", description = "用于测试的快速添加评价接口")
    public Result testAddReview() {
        try {
            Review review = new Review();
            review.setAppointmentId(1L);
            review.setPatientId(1L);
            review.setDoctorId(1L);
            review.setRating(5);
            review.setContent("测试评价内容");
            review.setStatus(1);
            
            reviewService.addReview(review);
            return Result.success("测试评价添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("添加失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/test/check")
    @Operation(summary = "检查数据库连接", description = "测试数据库连接是否正常")
    public Result testCheck() {
        try {
            long count = reviewService.count();
            return Result.success("数据库连接正常，当前 review 表有 " + count + " 条记录");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("数据库连接失败: " + e.getMessage());
        }
    }
}