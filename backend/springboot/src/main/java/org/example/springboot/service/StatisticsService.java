package org.example.springboot.service;

import org.example.springboot.DTO.StatisticsDTO;
import org.example.springboot.DTO.TrendDTO;
import org.example.springboot.DTO.DepartmentDistributionDTO;

import java.time.LocalDate;
import java.util.List;

public interface StatisticsService {
    StatisticsDTO getOverviewStatistics();
    List<TrendDTO> getPatientTrend(int months);
    List<DepartmentDistributionDTO> getDepartmentDistribution();
    List<TrendDTO> getAppointmentTrend(int months);
    List<TrendDTO> getReportTypeDistribution();
    List<TrendDTO> getFollowUpStatusDistribution();
}
