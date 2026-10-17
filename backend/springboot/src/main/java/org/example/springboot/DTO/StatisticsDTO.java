package org.example.springboot.DTO;

import lombok.Data;

@Data
public class StatisticsDTO {
    private long totalPatients;
    private long totalDoctors;
    private long totalRecords;
    private long totalReports;
    private long totalAppointments;
    private long totalReviews;
    private double avgRating;
}
