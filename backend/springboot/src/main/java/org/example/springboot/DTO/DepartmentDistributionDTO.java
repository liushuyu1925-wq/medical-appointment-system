package org.example.springboot.DTO;

import lombok.Data;

@Data
public class DepartmentDistributionDTO {
    private String departmentName;
    private long count;
    private double percentage;
}
