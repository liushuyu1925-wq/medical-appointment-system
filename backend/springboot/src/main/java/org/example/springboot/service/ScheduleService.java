package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.example.springboot.entity.Appointment;
import org.example.springboot.entity.Department;
import org.example.springboot.entity.Doctor;
import org.example.springboot.entity.Schedule;
import org.example.springboot.exception.ServiceException;
import org.example.springboot.mapper.AppointmentMapper;
import org.example.springboot.mapper.DepartmentMapper;
import org.example.springboot.mapper.DoctorMapper;
import org.example.springboot.mapper.ScheduleMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduleService {
    @Resource
    private ScheduleMapper scheduleMapper;

    @Resource
    private DoctorMapper doctorMapper;

    @Resource
    private DepartmentMapper departmentMapper;

    @Resource
    private AppointmentMapper appointmentMapper;

    @Resource
    private NoticeService noticeService;
    
    @Resource
    @Lazy
    private AppointmentService appointmentService;
    
    @Resource
    private PaymentService paymentService;

    @Transactional
    public Schedule createSchedule(Schedule schedule) {
        Doctor doctor = doctorMapper.selectById(schedule.getDoctorId());
        if (doctor == null) {
            throw new ServiceException("Doctor not found");
        }

        LambdaQueryWrapper<Schedule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Schedule::getDoctorId, schedule.getDoctorId())
                .eq(Schedule::getScheduleDate, schedule.getScheduleDate())
                .eq(Schedule::getTimeSlot, schedule.getTimeSlot());
        if (scheduleMapper.selectCount(queryWrapper) > 0) {
            throw new ServiceException("Duplicate schedule is not allowed");
        }

        if (schedule.getCurrentPatients() == null) {
            schedule.setCurrentPatients(0);
        }
        if (schedule.getStatus() == null) {
            schedule.setStatus(1);
        }

        LocalDateTime now = LocalDateTime.now();
        schedule.setCreateTime(now);
        schedule.setUpdateTime(now);
        if (scheduleMapper.insert(schedule) <= 0) {
            throw new ServiceException("Create schedule failed");
        }
        return schedule;
    }

    @Transactional
    public void updateSchedule(Long id, Schedule schedule) {
        Schedule existingSchedule = scheduleMapper.selectById(id);
        if (existingSchedule == null) {
            throw new ServiceException("Schedule not found");
        }

        if (schedule.getDoctorId() != null && !schedule.getDoctorId().equals(existingSchedule.getDoctorId())) {
            Doctor doctor = doctorMapper.selectById(schedule.getDoctorId());
            if (doctor == null) {
                throw new ServiceException("Doctor not found");
            }

            LambdaQueryWrapper<Schedule> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Schedule::getDoctorId, schedule.getDoctorId())
                    .eq(Schedule::getScheduleDate, schedule.getScheduleDate() != null ? schedule.getScheduleDate() : existingSchedule.getScheduleDate())
                    .eq(Schedule::getTimeSlot, schedule.getTimeSlot() != null ? schedule.getTimeSlot() : existingSchedule.getTimeSlot())
                    .ne(Schedule::getId, id);
            if (scheduleMapper.selectCount(queryWrapper) > 0) {
                throw new ServiceException("Duplicate schedule is not allowed");
            }
        }

        if (schedule.getMaxPatients() != null && schedule.getMaxPatients() < existingSchedule.getCurrentPatients()) {
            throw new ServiceException("Max patients cannot be less than current patients");
        }

        schedule.setId(id);
        schedule.setUpdateTime(LocalDateTime.now());
        if (scheduleMapper.updateById(schedule) <= 0) {
            throw new ServiceException("Update schedule failed");
        }
    }

    public Schedule getScheduleById(Long id) {
        Schedule schedule = scheduleMapper.selectById(id);
        if (schedule == null) {
            throw new ServiceException("Schedule not found");
        }

        Doctor doctor = doctorMapper.selectById(schedule.getDoctorId());
        if (doctor != null && doctor.getDepartmentId() != null) {
            Department department = departmentMapper.selectById(doctor.getDepartmentId());
            doctor.setDepartment(department);
        }
        schedule.setDoctor(doctor);
        return schedule;
    }

    public Page<Schedule> getSchedulesByPage(Long doctorId, LocalDate startDate, LocalDate endDate,
                                             String timeSlot, Integer status,
                                             Integer currentPage, Integer size) {
        LambdaQueryWrapper<Schedule> queryWrapper = new LambdaQueryWrapper<>();
        if (doctorId != null) {
            queryWrapper.eq(Schedule::getDoctorId, doctorId);
        }
        if (startDate != null) {
            queryWrapper.ge(Schedule::getScheduleDate, startDate);
        }
        if (endDate != null) {
            queryWrapper.le(Schedule::getScheduleDate, endDate);
        }
        if (timeSlot != null && !timeSlot.isEmpty()) {
            queryWrapper.eq(Schedule::getTimeSlot, timeSlot);
        }
        if (status != null) {
            queryWrapper.eq(Schedule::getStatus, status);
        }
        queryWrapper.orderByAsc(Schedule::getScheduleDate)
                .orderByAsc(Schedule::getTimeSlot);

        Page<Schedule> page = scheduleMapper.selectPage(new Page<>(currentPage, size), queryWrapper);
        for (Schedule schedule : page.getRecords()) {
            Doctor doctor = doctorMapper.selectById(schedule.getDoctorId());
            if (doctor != null && doctor.getDepartmentId() != null) {
                Department department = departmentMapper.selectById(doctor.getDepartmentId());
                doctor.setDepartment(department);
            }
            schedule.setDoctor(doctor);
        }
        return page;
    }

    public List<Schedule> getSchedulesByDoctor(Long doctorId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<Schedule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Schedule::getDoctorId, doctorId);
        if (startDate != null) {
            queryWrapper.ge(Schedule::getScheduleDate, startDate);
        }
        if (endDate != null) {
            queryWrapper.le(Schedule::getScheduleDate, endDate);
        }
        queryWrapper.eq(Schedule::getStatus, 1)
                .orderByAsc(Schedule::getScheduleDate)
                .orderByAsc(Schedule::getTimeSlot);

        List<Schedule> schedules = scheduleMapper.selectList(queryWrapper);
        Doctor doctor = doctorMapper.selectById(doctorId);
        if (doctor != null && doctor.getDepartmentId() != null) {
            Department department = departmentMapper.selectById(doctor.getDepartmentId());
            doctor.setDepartment(department);
        }
        for (Schedule schedule : schedules) {
            schedule.setDoctor(doctor);
        }
        return schedules;
    }

    public List<Schedule> getSchedulesByDate(LocalDate date) {
        List<Schedule> schedules = scheduleMapper.selectList(new LambdaQueryWrapper<Schedule>()
                .eq(Schedule::getScheduleDate, date)
                .eq(Schedule::getStatus, 1)
                .orderByAsc(Schedule::getTimeSlot));

        for (Schedule schedule : schedules) {
            Doctor doctor = doctorMapper.selectById(schedule.getDoctorId());
            if (doctor != null && doctor.getDepartmentId() != null) {
                Department department = departmentMapper.selectById(doctor.getDepartmentId());
                doctor.setDepartment(department);
            }
            schedule.setDoctor(doctor);
        }
        return schedules;
    }

    @Transactional
    public void updateScheduleStatus(Long id, Integer status) {
        Schedule schedule = scheduleMapper.selectById(id);
        if (schedule == null) {
            throw new ServiceException("Schedule not found");
        }

        if (status == 0 && schedule.getCurrentPatients() > 0) {
            throw new ServiceException("Current rule does not allow cancelling a booked schedule");
        }

        Schedule updateSchedule = new Schedule();
        updateSchedule.setId(id);
        updateSchedule.setStatus(status);
        updateSchedule.setUpdateTime(LocalDateTime.now());
        if (scheduleMapper.updateById(updateSchedule) <= 0) {
            throw new ServiceException("Update schedule status failed");
        }

        noticeService.handleScheduleStatusChanged(id, status);
    }

    @Transactional
    public void batchCreateSchedule(List<Schedule> schedules) {
        for (Schedule schedule : schedules) {
            createSchedule(schedule);
        }
    }

    @Transactional
    public void deleteSchedule(Long id) {
        Schedule schedule = scheduleMapper.selectById(id);
        if (schedule == null) {
            throw new ServiceException("Schedule not found");
        }
        
        // 删除相关的预约记录
        LambdaQueryWrapper<Appointment> appointmentQueryWrapper = new LambdaQueryWrapper<>();
        appointmentQueryWrapper.eq(Appointment::getScheduleId, id);
        List<Appointment> appointments = appointmentMapper.selectList(appointmentQueryWrapper);
        for (Appointment appointment : appointments) {
            appointmentService.deleteAppointment(appointment.getId());
        }
        
        // 删除排班
        if (scheduleMapper.deleteById(id) <= 0) {
            throw new ServiceException("Delete schedule failed");
        }
    }
    
    public void batchDeleteByDoctorId(Long doctorId) {
        if (doctorId == null) {
            throw new ServiceException("Doctor ID cannot be null");
        }
        
        // 查找该医生的所有排班
        LambdaQueryWrapper<Schedule> scheduleQueryWrapper = new LambdaQueryWrapper<>();
        scheduleQueryWrapper.eq(Schedule::getDoctorId, doctorId);
        List<Schedule> schedules = scheduleMapper.selectList(scheduleQueryWrapper);
        
        if (schedules.isEmpty()) {
            return; // 没有排班记录，直接返回
        }
        
        // 遍历删除每个排班
        for (Schedule schedule : schedules) {
            deleteSchedule(schedule.getId());
        }
    }

    @Transactional
    public void updateSchedulePatients(Long id, int change) {
        Schedule schedule = scheduleMapper.selectById(id);
        if (schedule == null) {
            throw new ServiceException("Schedule not found");
        }

        int newCount = schedule.getCurrentPatients() + change;
        if (newCount > schedule.getMaxPatients()) {
            throw new ServiceException("Max patients exceeded");
        }
        if (newCount < 0) {
            throw new ServiceException("Current patients cannot be negative");
        }

        Schedule updateSchedule = new Schedule();
        updateSchedule.setId(id);
        updateSchedule.setCurrentPatients(newCount);
        updateSchedule.setUpdateTime(LocalDateTime.now());
        if (scheduleMapper.updateById(updateSchedule) <= 0) {
            throw new ServiceException("Update schedule patients failed");
        }
    }
}
