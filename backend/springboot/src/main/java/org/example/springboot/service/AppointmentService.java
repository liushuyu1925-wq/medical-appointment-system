package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.example.springboot.entity.Appointment;
import org.example.springboot.entity.Department;
import org.example.springboot.entity.Doctor;
import org.example.springboot.entity.Patient;
import org.example.springboot.entity.Schedule;
import org.example.springboot.exception.ServiceException;
import org.example.springboot.mapper.AppointmentMapper;
import org.example.springboot.mapper.DepartmentMapper;
import org.example.springboot.mapper.DoctorMapper;
import org.example.springboot.mapper.PatientMapper;
import org.example.springboot.mapper.ScheduleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AppointmentService {
    @Resource
    private AppointmentMapper appointmentMapper;

    @Resource
    private PatientMapper patientMapper;

    @Resource
    private DoctorMapper doctorMapper;

    @Resource
    private ScheduleMapper scheduleMapper;

    @Resource
    private DepartmentMapper departmentMapper;

    @Resource
    private ScheduleService scheduleService;

    @Resource
    private NoticeService noticeService;
    
    @Resource
    private PaymentService paymentService;

    @Transactional
    public Appointment createAppointment(Appointment appointment) {
        Patient patient = patientMapper.selectById(appointment.getPatientId());
        if (patient == null) {
            throw new ServiceException("Patient not found");
        }

        Doctor doctor = doctorMapper.selectById(appointment.getDoctorId());
        if (doctor == null) {
            throw new ServiceException("Doctor not found");
        }

        Schedule schedule = scheduleMapper.selectById(appointment.getScheduleId());
        if (schedule == null) {
            throw new ServiceException("Schedule not found");
        }

        if (schedule.getStatus() != 1) {
            throw new ServiceException("This schedule is unavailable");
        }

        if (schedule.getCurrentPatients() >= schedule.getMaxPatients()) {
            throw new ServiceException("No appointment quota left");
        }

        LambdaQueryWrapper<Appointment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Appointment::getPatientId, appointment.getPatientId())
                .eq(Appointment::getDoctorId, appointment.getDoctorId())
                .eq(Appointment::getAppointmentDate, schedule.getScheduleDate())
                .eq(Appointment::getStatus, 1);
        if (appointmentMapper.selectCount(queryWrapper) > 0) {
            throw new ServiceException("Duplicate appointment is not allowed");
        }

        appointment.setAppointmentNo(generateAppointmentNo());
        appointment.setAppointmentDate(schedule.getScheduleDate());
        appointment.setTimeSlot(schedule.getTimeSlot());
        appointment.setStatus(1);
        LocalDateTime now = LocalDateTime.now();
        appointment.setCreateTime(now);
        appointment.setUpdateTime(now);

        if (appointmentMapper.insert(appointment) <= 0) {
            throw new ServiceException("Create appointment failed");
        }

        scheduleService.updateSchedulePatients(schedule.getId(), 1);
        
        // 生成支付订单
        // 这里可以根据实际业务逻辑设置金额，暂时设置为100.00
        java.math.BigDecimal amount = new java.math.BigDecimal(100.00);
        paymentService.createOrder(appointment.getId(), amount);
        
        noticeService.createAppointmentSuccessNotice(appointment.getId());
        return appointment;
    }

    @Transactional
    public void cancelAppointment(Long id) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            throw new ServiceException("Appointment not found");
        }

        if (appointment.getStatus() != 1) {
            throw new ServiceException("Only pending appointments can be cancelled");
        }

        if (appointment.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new ServiceException("Expired appointment cannot be cancelled");
        }

        Appointment updateAppointment = new Appointment();
        updateAppointment.setId(id);
        updateAppointment.setStatus(0);
        updateAppointment.setUpdateTime(LocalDateTime.now());
        if (appointmentMapper.updateById(updateAppointment) <= 0) {
            throw new ServiceException("Cancel appointment failed");
        }

        scheduleService.updateSchedulePatients(appointment.getScheduleId(), -1);
        noticeService.createAppointmentCancelNotice(id);
    }

    @Transactional
    public void completeAppointment(Long id) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            throw new ServiceException("Appointment not found");
        }

        if (appointment.getStatus() != 1) {
            throw new ServiceException("Only pending appointments can be completed");
        }

        Appointment updateAppointment = new Appointment();
        updateAppointment.setId(id);
        updateAppointment.setStatus(2);
        updateAppointment.setUpdateTime(LocalDateTime.now());
        if (appointmentMapper.updateById(updateAppointment) <= 0) {
            throw new ServiceException("Complete appointment failed");
        }
    }

    public Appointment getAppointmentById(Long id) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            throw new ServiceException("Appointment not found");
        }

        Patient patient = patientMapper.selectById(appointment.getPatientId());
        appointment.setPatient(patient);

        Doctor doctor = doctorMapper.selectById(appointment.getDoctorId());
        if (doctor != null && doctor.getDepartmentId() != null) {
            Department department = departmentMapper.selectById(doctor.getDepartmentId());
            doctor.setDepartment(department);
        }
        appointment.setDoctor(doctor);
        appointment.setSchedule(scheduleMapper.selectById(appointment.getScheduleId()));
        return appointment;
    }

    public Page<Appointment> getAppointmentsByPage(Long patientId, Long doctorId, String patientName, String doctorName,
                                                   LocalDate startDate, LocalDate endDate, Integer status,
                                                   Integer currentPage, Integer size) {
        Page<Appointment> page = new Page<>(currentPage, size);
        LambdaQueryWrapper<Appointment> queryWrapper = new LambdaQueryWrapper<>();

        if (patientId != null) {
            queryWrapper.eq(Appointment::getPatientId, patientId);
        }
        if (doctorId != null) {
            queryWrapper.eq(Appointment::getDoctorId, doctorId);
        }
        if (startDate != null) {
            queryWrapper.ge(Appointment::getAppointmentDate, startDate);
        }
        if (endDate != null) {
            queryWrapper.le(Appointment::getAppointmentDate, endDate);
        }
        if (status != null) {
            queryWrapper.eq(Appointment::getStatus, status);
        }

        if (StringUtils.isNotBlank(patientName)) {
            List<Long> patientIds = patientMapper.selectList(new LambdaQueryWrapper<Patient>()
                            .like(Patient::getName, patientName))
                    .stream()
                    .map(Patient::getId)
                    .toList();
            if (patientIds.isEmpty()) {
                return new Page<>(currentPage, size);
            }
            queryWrapper.in(Appointment::getPatientId, patientIds);
        }

        if (StringUtils.isNotBlank(doctorName)) {
            List<Long> doctorIds = doctorMapper.selectList(new LambdaQueryWrapper<Doctor>()
                            .like(Doctor::getName, doctorName))
                    .stream()
                    .map(Doctor::getId)
                    .toList();
            if (doctorIds.isEmpty()) {
                return new Page<>(currentPage, size);
            }
            queryWrapper.in(Appointment::getDoctorId, doctorIds);
        }

        queryWrapper.orderByDesc(Appointment::getCreateTime);
        Page<Appointment> resultPage = appointmentMapper.selectPage(page, queryWrapper);
        fillAppointmentsInfo(resultPage.getRecords());
        return resultPage;
    }

    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        List<Appointment> appointments = appointmentMapper.selectList(new LambdaQueryWrapper<Appointment>()
                .eq(Appointment::getPatientId, patientId)
                .orderByDesc(Appointment::getAppointmentDate)
                .orderByAsc(Appointment::getTimeSlot));

        for (Appointment appointment : appointments) {
            Doctor doctor = doctorMapper.selectById(appointment.getDoctorId());
            if (doctor != null && doctor.getDepartmentId() != null) {
                Department department = departmentMapper.selectById(doctor.getDepartmentId());
                doctor.setDepartment(department);
            }
            appointment.setDoctor(doctor);
            appointment.setSchedule(scheduleMapper.selectById(appointment.getScheduleId()));
        }
        return appointments;
    }

    public List<Appointment> getAppointmentsByDoctor(Long doctorId, LocalDate date) {
        LambdaQueryWrapper<Appointment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Appointment::getDoctorId, doctorId);
        if (date != null) {
            queryWrapper.eq(Appointment::getAppointmentDate, date);
        }
        queryWrapper.orderByAsc(Appointment::getAppointmentDate)
                .orderByAsc(Appointment::getTimeSlot);

        List<Appointment> appointments = appointmentMapper.selectList(queryWrapper);
        for (Appointment appointment : appointments) {
            appointment.setPatient(patientMapper.selectById(appointment.getPatientId()));
            appointment.setSchedule(scheduleMapper.selectById(appointment.getScheduleId()));
        }
        return appointments;
    }

    @Transactional
    public void updateAppointment(Long id, Appointment appointment) {
        Appointment existingAppointment = appointmentMapper.selectById(id);
        if (existingAppointment == null) {
            throw new ServiceException("Appointment not found");
        }

        if (existingAppointment.getStatus() != 1) {
            throw new ServiceException("Only pending appointments can be updated");
        }

        if (existingAppointment.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new ServiceException("Expired appointment cannot be updated");
        }

        boolean scheduleChanged = false;
        if (appointment.getScheduleId() != null && !appointment.getScheduleId().equals(existingAppointment.getScheduleId())) {
            scheduleChanged = true;
            Schedule newSchedule = scheduleMapper.selectById(appointment.getScheduleId());
            if (newSchedule == null) {
                throw new ServiceException("Schedule not found");
            }
            if (newSchedule.getStatus() != 1) {
                throw new ServiceException("This schedule is unavailable");
            }
            if (newSchedule.getCurrentPatients() >= newSchedule.getMaxPatients()) {
                throw new ServiceException("No appointment quota left");
            }

            scheduleService.updateSchedulePatients(existingAppointment.getScheduleId(), -1);
            scheduleService.updateSchedulePatients(appointment.getScheduleId(), 1);
            appointment.setAppointmentDate(newSchedule.getScheduleDate());
            appointment.setTimeSlot(newSchedule.getTimeSlot());
        }

        appointment.setId(id);
        appointment.setUpdateTime(LocalDateTime.now());
        if (appointmentMapper.updateById(appointment) <= 0) {
            throw new ServiceException("Update appointment failed");
        }

        if (scheduleChanged) {
            noticeService.createRescheduleSuccessNotice(id);
        }
    }

    private String generateAppointmentNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomStr = String.format("%06d", (int) (Math.random() * 1000000));
        return "A" + dateStr + randomStr;
    }

    private void fillAppointmentsInfo(List<Appointment> appointments) {
        if (appointments == null || appointments.isEmpty()) {
            return;
        }

        for (Appointment appointment : appointments) {
            appointment.setPatient(patientMapper.selectById(appointment.getPatientId()));
            Doctor doctor = doctorMapper.selectById(appointment.getDoctorId());
            if (doctor != null && doctor.getDepartmentId() != null) {
                Department department = departmentMapper.selectById(doctor.getDepartmentId());
                doctor.setDepartment(department);
            }
            appointment.setDoctor(doctor);
            appointment.setSchedule(scheduleMapper.selectById(appointment.getScheduleId()));
        }
    }

    @Transactional
    public void deleteAppointment(Long id) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            throw new ServiceException("Appointment not found");
        }

        // 释放医生的预约名额
        if (appointment.getStatus() == 1 && appointment.getScheduleId() != null) {
            scheduleService.updateSchedulePatients(appointment.getScheduleId(), -1);
        }

        // 删除相关的支付订单
        paymentService.deleteByAppointmentId(id);

        // 删除预约
        if (appointmentMapper.deleteById(id) <= 0) {
            throw new ServiceException("Delete appointment failed");
        }
    }
}
