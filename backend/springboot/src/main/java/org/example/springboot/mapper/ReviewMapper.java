package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.springboot.entity.Review;

@Mapper
public interface ReviewMapper extends BaseMapper<Review> {
    
    @Select("SELECT IFNULL(AVG(rating), 0) FROM review WHERE doctor_id = #{doctorId} AND status = 1")
    Double getAverageRatingByDoctorId(Long doctorId);
    
    @Select("SELECT IFNULL(COUNT(*), 0) FROM review WHERE doctor_id = #{doctorId} AND status = 1")
    Integer getReviewCountByDoctorId(Long doctorId);
    
    @Select("SELECT IFNULL(AVG(rating), 0) FROM review WHERE status = 1")
    Double selectAvgRating();
}