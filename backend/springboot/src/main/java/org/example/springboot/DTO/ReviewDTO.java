package org.example.springboot.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewDTO {
    private Long id;
    private Long appointmentId;
    private Long patientId;
    private Long doctorId;
    private Integer rating;
    private String content;
    private String reply;
    private Integer status;
    private String patientName;
    private String doctorName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}