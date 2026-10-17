package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.example.springboot.DTO.NoticeLogPageQueryDTO;
import org.example.springboot.DTO.NoticePageQueryDTO;
import org.example.springboot.entity.Appointment;
import org.example.springboot.entity.Department;
import org.example.springboot.entity.Doctor;
import org.example.springboot.entity.Notice;
import org.example.springboot.entity.NoticeLog;
import org.example.springboot.entity.Patient;
import org.example.springboot.entity.User;
import org.example.springboot.enumClass.NoticeActionResultEnum;
import org.example.springboot.enumClass.NoticeActionTypeEnum;
import org.example.springboot.enumClass.NoticeTypeEnum;
import org.example.springboot.exception.ServiceException;
import org.example.springboot.mapper.AppointmentMapper;
import org.example.springboot.mapper.DepartmentMapper;
import org.example.springboot.mapper.DoctorMapper;
import org.example.springboot.mapper.NoticeLogMapper;
import org.example.springboot.mapper.NoticeMapper;
import org.example.springboot.mapper.PatientMapper;
import org.example.springboot.mapper.UserMapper;
import org.example.springboot.util.JwtTokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class NoticeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NoticeService.class);
    private static final String SYSTEM_ROLE = "SYSTEM";
    private static final Integer STATUS_VALID = 1;
    private static final Integer STATUS_UNREAD = 0;
    private static final Integer STATUS_READ = 1;
    private static final Integer SEND_SUCCESS = 1;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Resource
    private NoticeMapper noticeMapper;
    @Resource
    private NoticeLogMapper noticeLogMapper;
    @Resource
    private AppointmentMapper appointmentMapper;
    @Resource
    private PatientMapper patientMapper;
    @Resource
    private DoctorMapper doctorMapper;
    @Resource
    private DepartmentMapper departmentMapper;
    @Resource
    private UserMapper userMapper;

    public Page<Notice> getMyNoticePage(NoticePageQueryDTO queryDTO) {
        Long currentUserId = requireCurrentUserId();
        Page<Notice> page = buildPage(queryDTO.getCurrentPage(), queryDTO.getSize());
        LambdaQueryWrapper<Notice> queryWrapper = buildNoticeQuery(queryDTO);
        queryWrapper.eq(Notice::getRecipientUserId, currentUserId)
                .eq(Notice::getStatus, STATUS_VALID)
                .orderByDesc(Notice::getCreateTime);
        Page<Notice> resultPage = noticeMapper.selectPage(page, queryWrapper);
        fillNoticeInfo(resultPage.getRecords());
        return resultPage;
    }

    public Long getMyUnreadCount() {
        Long currentUserId = requireCurrentUserId();
        return noticeMapper.selectCount(new LambdaQueryWrapper<Notice>()
                .eq(Notice::getRecipientUserId, currentUserId)
                .eq(Notice::getReadStatus, STATUS_UNREAD)
                .eq(Notice::getStatus, STATUS_VALID));
    }

    public Notice getMyNoticeDetail(Long id) {
        Notice notice = requireNoticeById(id);
        Long currentUserId = requireCurrentUserId();
        if (!currentUserId.equals(notice.getRecipientUserId())) {
            throw new ServiceException("无权查看该通知");
        }
        fillNoticeInfo(Collections.singletonList(notice));
        return notice;
    }

    public void markRead(Long id) {
        Long currentUserId = requireCurrentUserId();
        User currentUser = requireCurrentUser();
        Notice notice = requireNoticeById(id);
        if (!currentUserId.equals(notice.getRecipientUserId())) {
            throw new ServiceException("无权操作该通知");
        }
        if (STATUS_READ.equals(notice.getReadStatus())) {
            return;
        }

        Notice updateNotice = new Notice();
        updateNotice.setId(id);
        updateNotice.setReadStatus(STATUS_READ);
        updateNotice.setReadTime(LocalDateTime.now());
        updateNotice.setUpdateTime(LocalDateTime.now());
        if (noticeMapper.updateById(updateNotice) <= 0) {
            throw new ServiceException("通知已读更新失败");
        }

        saveNoticeLog(id, notice.getRecipientUserId(), NoticeActionTypeEnum.NOTICE_READ.getCode(),
                NoticeActionResultEnum.SUCCESS.getCode(), "通知已标记为已读",
                currentUser.getId(), currentUser.getRoleCode(), null);
    }

    public void markAllRead() {
        Long currentUserId = requireCurrentUserId();
        User currentUser = requireCurrentUser();
        List<Notice> unreadNotices = noticeMapper.selectList(new LambdaQueryWrapper<Notice>()
                .eq(Notice::getRecipientUserId, currentUserId)
                .eq(Notice::getReadStatus, STATUS_UNREAD)
                .eq(Notice::getStatus, STATUS_VALID));
        if (unreadNotices.isEmpty()) {
            return;
        }

        LambdaUpdateWrapper<Notice> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Notice::getRecipientUserId, currentUserId)
                .eq(Notice::getReadStatus, STATUS_UNREAD)
                .eq(Notice::getStatus, STATUS_VALID)
                .set(Notice::getReadStatus, STATUS_READ)
                .set(Notice::getReadTime, LocalDateTime.now())
                .set(Notice::getUpdateTime, LocalDateTime.now());
        noticeMapper.update(null, updateWrapper);

        saveNoticeLog(null, currentUserId, NoticeActionTypeEnum.NOTICE_READ_ALL.getCode(),
                NoticeActionResultEnum.SUCCESS.getCode(), "全部已读数量:" + unreadNotices.size(),
                currentUser.getId(), currentUser.getRoleCode(), null);
    }

    public Page<Notice> getAdminNoticePage(NoticePageQueryDTO queryDTO) {
        requireAdmin();
        Page<Notice> page = buildPage(queryDTO.getCurrentPage(), queryDTO.getSize());
        LambdaQueryWrapper<Notice> queryWrapper = buildNoticeQuery(queryDTO);
        appendRecipientKeywordFilter(queryWrapper, queryDTO.getRecipientKeyword());
        queryWrapper.eq(Notice::getStatus, STATUS_VALID)
                .orderByDesc(Notice::getCreateTime);
        Page<Notice> resultPage = noticeMapper.selectPage(page, queryWrapper);
        fillNoticeInfo(resultPage.getRecords());
        return resultPage;
    }

    public Notice getAdminNoticeDetail(Long id) {
        requireAdmin();
        Notice notice = requireNoticeById(id);
        fillNoticeInfo(Collections.singletonList(notice));
        return notice;
    }

    public Page<NoticeLog> getNoticeLogPage(NoticeLogPageQueryDTO queryDTO) {
        requireAdmin();
        Page<NoticeLog> page = buildPage(queryDTO.getCurrentPage(), queryDTO.getSize());
        LambdaQueryWrapper<NoticeLog> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(queryDTO.getActionType())) {
            queryWrapper.eq(NoticeLog::getActionType, queryDTO.getActionType());
        }
        if (StringUtils.isNotBlank(queryDTO.getActionResult())) {
            queryWrapper.eq(NoticeLog::getActionResult, queryDTO.getActionResult());
        }
        if (StringUtils.isNotBlank(queryDTO.getTaskBatchNo())) {
            queryWrapper.like(NoticeLog::getTaskBatchNo, queryDTO.getTaskBatchNo());
        }

        LocalDateTime startTime = parseDateTime(queryDTO.getStartTime(), false);
        LocalDateTime endTime = parseDateTime(queryDTO.getEndTime(), true);
        if (startTime != null) {
            queryWrapper.ge(NoticeLog::getCreateTime, startTime);
        }
        if (endTime != null) {
            queryWrapper.le(NoticeLog::getCreateTime, endTime);
        }
        appendNoticeLogRecipientKeywordFilter(queryWrapper, queryDTO.getRecipientKeyword());
        queryWrapper.orderByDesc(NoticeLog::getCreateTime);
        Page<NoticeLog> resultPage = noticeLogMapper.selectPage(page, queryWrapper);
        fillNoticeLogInfo(resultPage.getRecords());
        return resultPage;
    }

    public NoticeLog getNoticeLogDetail(Long id) {
        requireAdmin();
        NoticeLog noticeLog = noticeLogMapper.selectById(id);
        if (noticeLog == null) {
            throw new ServiceException("通知日志不存在");
        }
        fillNoticeLogInfo(Collections.singletonList(noticeLog));
        return noticeLog;
    }
    
    public void deleteNotice(Long id) {
        requireAdmin();
        Notice notice = requireNoticeById(id);
        
        // 软删除通知
        Notice updateNotice = new Notice();
        updateNotice.setId(id);
        updateNotice.setStatus(0); // 0表示已删除
        updateNotice.setUpdateTime(LocalDateTime.now());
        if (noticeMapper.updateById(updateNotice) <= 0) {
            throw new ServiceException("通知删除失败");
        }
        
        // 记录删除操作日志
        User currentUser = requireCurrentUser();
        saveNoticeLog(id, notice.getRecipientUserId(), "NOTICE_DELETE",
                NoticeActionResultEnum.SUCCESS.getCode(), "通知已删除",
                currentUser.getId(), currentUser.getRoleCode(), null);
    }

    public void createAppointmentSuccessNotice(Long appointmentId) {
        User operator = JwtTokenUtils.getCurrentUser();
        submitAfterCommit(() -> safeCreateNotice(appointmentId, NoticeTypeEnum.APPOINTMENT_SUCCESS, operator, null));
    }

    public void createAppointmentCancelNotice(Long appointmentId) {
        User operator = JwtTokenUtils.getCurrentUser();
        submitAfterCommit(() -> safeCreateNotice(appointmentId, NoticeTypeEnum.APPOINTMENT_CANCEL, operator, null));
    }

    public void createRescheduleSuccessNotice(Long appointmentId) {
        User operator = JwtTokenUtils.getCurrentUser();
        submitAfterCommit(() -> safeCreateNotice(appointmentId, NoticeTypeEnum.RESCHEDULE_SUCCESS, operator, null));
    }

    public void handleScheduleStatusChanged(Long scheduleId, Integer status) {
        if (!Integer.valueOf(0).equals(status)) {
            return;
        }
        User operator = JwtTokenUtils.getCurrentUser();
        submitAfterCommit(() -> createScheduleCancelNotices(scheduleId, operator));
    }

    public void executeVisitReminderTask() {
        String batchNo = "REMINDER-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        LocalDate targetDate = LocalDate.now().plusDays(1);
        List<Appointment> appointments = appointmentMapper.selectList(new LambdaQueryWrapper<Appointment>()
                .eq(Appointment::getAppointmentDate, targetDate)
                .eq(Appointment::getStatus, 1)
                .orderByAsc(Appointment::getId));

        int createdCount = 0;
        int skippedCount = 0;
        for (Appointment appointment : appointments) {
            boolean created = safeCreateNotice(appointment.getId(), NoticeTypeEnum.VISIT_REMINDER, null, batchNo);
            if (created) {
                createdCount++;
            } else {
                skippedCount++;
            }
        }

        saveNoticeLog(null, null, NoticeActionTypeEnum.REMINDER_SCAN.getCode(),
                NoticeActionResultEnum.SUCCESS.getCode(),
                String.format("提醒任务执行完成，目标日期:%s，扫描数量:%d，生成数量:%d，跳过数量:%d",
                        targetDate, appointments.size(), createdCount, skippedCount),
                null, SYSTEM_ROLE, batchNo);
    }

    private boolean safeCreateNotice(Long appointmentId, NoticeTypeEnum noticeType, User operator, String taskBatchNo) {
        try {
            return createNotice(appointmentId, noticeType, operator, taskBatchNo);
        } catch (Exception e) {
            LOGGER.error("通知生成失败，appointmentId={}, noticeType={}", appointmentId, noticeType.getCode(), e);
            saveNoticeLog(null, resolveRecipientUserId(appointmentId), NoticeActionTypeEnum.NOTICE_CREATE.getCode(),
                    NoticeActionResultEnum.FAILED.getCode(), "通知生成失败:" + e.getMessage(),
                    operator == null ? null : operator.getId(),
                    operator == null ? SYSTEM_ROLE : operator.getRoleCode(),
                    taskBatchNo);
            return false;
        }
    }

    private boolean createNotice(Long appointmentId, NoticeTypeEnum noticeType, User operator, String taskBatchNo) {
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            saveNoticeLog(null, null, NoticeActionTypeEnum.NOTICE_CREATE.getCode(),
                    NoticeActionResultEnum.FAILED.getCode(), "预约不存在，无法生成通知",
                    operator == null ? null : operator.getId(),
                    operator == null ? SYSTEM_ROLE : operator.getRoleCode(),
                    taskBatchNo);
            return false;
        }

        Patient patient = patientMapper.selectById(appointment.getPatientId());
        if (patient == null || patient.getUserId() == null) {
            saveNoticeLog(null, null, NoticeActionTypeEnum.NOTICE_CREATE.getCode(),
                    NoticeActionResultEnum.FAILED.getCode(), "患者或患者用户信息不存在，无法生成通知",
                    operator == null ? null : operator.getId(),
                    operator == null ? SYSTEM_ROLE : operator.getRoleCode(),
                    taskBatchNo);
            return false;
        }

        User recipient = userMapper.selectById(patient.getUserId());
        if (recipient == null) {
            saveNoticeLog(null, patient.getUserId(), NoticeActionTypeEnum.NOTICE_CREATE.getCode(),
                    NoticeActionResultEnum.FAILED.getCode(), "接收用户不存在，无法生成通知",
                    operator == null ? null : operator.getId(),
                    operator == null ? SYSTEM_ROLE : operator.getRoleCode(),
                    taskBatchNo);
            return false;
        }

        Doctor doctor = doctorMapper.selectById(appointment.getDoctorId());
        Department department = null;
        if (doctor != null && doctor.getDepartmentId() != null) {
            department = departmentMapper.selectById(doctor.getDepartmentId());
        }

        LocalDateTime triggerTime = null;
        if (NoticeTypeEnum.VISIT_REMINDER == noticeType) {
            triggerTime = appointment.getAppointmentDate().minusDays(1).atTime(8, 0);
            if (existsReminderNotice(patient.getUserId(), appointment.getId(), noticeType.getCode(), triggerTime)) {
                saveNoticeLog(null, patient.getUserId(), NoticeActionTypeEnum.REMINDER_CREATE.getCode(),
                        NoticeActionResultEnum.SKIPPED.getCode(), "同一预约提醒已生成，跳过重复生成",
                        operator == null ? null : operator.getId(),
                        operator == null ? SYSTEM_ROLE : operator.getRoleCode(),
                        taskBatchNo);
                return false;
            }
        } else if (NoticeTypeEnum.APPOINTMENT_SUCCESS == noticeType
                || NoticeTypeEnum.APPOINTMENT_CANCEL == noticeType
                || NoticeTypeEnum.SCHEDULE_CANCEL == noticeType) {
            if (existsBusinessNotice(patient.getUserId(), appointment.getId(), noticeType.getCode())) {
                saveNoticeLog(null, patient.getUserId(), NoticeActionTypeEnum.NOTICE_CREATE.getCode(),
                        NoticeActionResultEnum.SKIPPED.getCode(), "通知已存在，跳过重复生成",
                        operator == null ? null : operator.getId(),
                        operator == null ? SYSTEM_ROLE : operator.getRoleCode(),
                        taskBatchNo);
                return false;
            }
        }

        Notice notice = buildNotice(appointment, recipient, doctor, department, noticeType, triggerTime);
        if (noticeMapper.insert(notice) <= 0) {
            throw new ServiceException("通知保存失败");
        }

        String actionType = NoticeTypeEnum.VISIT_REMINDER == noticeType
                ? NoticeActionTypeEnum.REMINDER_CREATE.getCode()
                : NoticeActionTypeEnum.NOTICE_CREATE.getCode();
        saveNoticeLog(notice.getId(), notice.getRecipientUserId(), actionType,
                NoticeActionResultEnum.SUCCESS.getCode(), "通知生成成功:" + noticeType.getDescription(),
                operator == null ? null : operator.getId(),
                operator == null ? SYSTEM_ROLE : operator.getRoleCode(),
                taskBatchNo);
        return true;
    }

    private Notice buildNotice(Appointment appointment, User recipient, Doctor doctor,
                               Department department, NoticeTypeEnum noticeType, LocalDateTime triggerTime) {
        Notice notice = new Notice();
        notice.setRecipientUserId(recipient.getId());
        notice.setRecipientRole(recipient.getRoleCode());
        notice.setAppointmentId(appointment.getId());
        notice.setDoctorId(doctor == null ? null : doctor.getId());
        notice.setDepartmentId(department == null ? null : department.getId());
        notice.setNoticeType(noticeType.getCode());
        notice.setTitle(buildNoticeTitle(noticeType));
        notice.setContent(buildNoticeContent(appointment, doctor, department, noticeType));
        notice.setReadStatus(STATUS_UNREAD);
        notice.setSendStatus(SEND_SUCCESS);
        notice.setRetryCount(0);
        notice.setTriggerTime(triggerTime);
        notice.setStatus(STATUS_VALID);
        notice.setCreateTime(LocalDateTime.now());
        notice.setUpdateTime(LocalDateTime.now());
        return notice;
    }

    private String buildNoticeTitle(NoticeTypeEnum noticeType) {
        return switch (noticeType) {
            case APPOINTMENT_SUCCESS -> "预约成功通知";
            case APPOINTMENT_CANCEL -> "预约取消通知";
            case RESCHEDULE_SUCCESS -> "改约成功通知";
            case VISIT_REMINDER -> "就诊提醒通知";
            case SCHEDULE_CANCEL -> "停诊通知";
        };
    }

    private String buildNoticeContent(Appointment appointment, Doctor doctor, Department department, NoticeTypeEnum noticeType) {
        String doctorName = doctor == null ? "医生" : doctor.getName();
        String departmentName = department == null ? "相关科室" : department.getDeptName();
        String visitDate = appointment.getAppointmentDate() == null ? "待定日期" : appointment.getAppointmentDate().toString();
        String timeSlot = StringUtils.defaultIfBlank(appointment.getTimeSlot(), "待定时段");
        String appointmentNo = StringUtils.defaultIfBlank(appointment.getAppointmentNo(), "未知编号");

        return switch (noticeType) {
            case APPOINTMENT_SUCCESS ->
                    String.format("您已成功预约%s %s，科室：%s，医生：%s，预约编号：%s。请按时到院就诊，如无法就诊请及时取消预约。",
                            visitDate, timeSlot, departmentName, doctorName, appointmentNo);
            case APPOINTMENT_CANCEL ->
                    String.format("您已取消%s %s的预约，科室：%s，医生：%s，预约编号：%s。如需继续就诊，请重新预约。",
                            visitDate, timeSlot, departmentName, doctorName, appointmentNo);
            case RESCHEDULE_SUCCESS ->
                    String.format("您的预约已改约成功，新的就诊时间为%s %s，科室：%s，医生：%s，预约编号：%s。请按时到院就诊。",
                            visitDate, timeSlot, departmentName, doctorName, appointmentNo);
            case VISIT_REMINDER ->
                    String.format("您的预约将于明天%s %s开始，科室：%s，医生：%s，预约编号：%s。请提前安排时间到院，如无法就诊请及时取消预约。",
                            visitDate, timeSlot, departmentName, doctorName, appointmentNo);
            case SCHEDULE_CANCEL ->
                    String.format("您预约的%s %s门诊已停诊，科室：%s，医生：%s，预约编号：%s。请尽快重新预约其他可用时段。",
                            visitDate, timeSlot, departmentName, doctorName, appointmentNo);
        };
    }

    private boolean existsReminderNotice(Long recipientUserId, Long appointmentId, String noticeType, LocalDateTime triggerTime) {
        return noticeMapper.selectCount(new LambdaQueryWrapper<Notice>()
                .eq(Notice::getRecipientUserId, recipientUserId)
                .eq(Notice::getAppointmentId, appointmentId)
                .eq(Notice::getNoticeType, noticeType)
                .eq(Notice::getTriggerTime, triggerTime)
                .eq(Notice::getStatus, STATUS_VALID)) > 0;
    }

    private boolean existsBusinessNotice(Long recipientUserId, Long appointmentId, String noticeType) {
        return noticeMapper.selectCount(new LambdaQueryWrapper<Notice>()
                .eq(Notice::getRecipientUserId, recipientUserId)
                .eq(Notice::getAppointmentId, appointmentId)
                .eq(Notice::getNoticeType, noticeType)
                .eq(Notice::getStatus, STATUS_VALID)) > 0;
    }

    private void createScheduleCancelNotices(Long scheduleId, User operator) {
        List<Appointment> affectedAppointments = appointmentMapper.selectList(new LambdaQueryWrapper<Appointment>()
                .eq(Appointment::getScheduleId, scheduleId)
                .eq(Appointment::getStatus, 1));

        if (affectedAppointments.isEmpty()) {
            saveNoticeLog(null, null, NoticeActionTypeEnum.SCHEDULE_CANCEL_SCAN.getCode(),
                    NoticeActionResultEnum.SKIPPED.getCode(),
                    "当前停诊流程下未发现受影响预约，现有规则可能已阻止存在预约的排班停诊",
                    operator == null ? null : operator.getId(),
                    operator == null ? SYSTEM_ROLE : operator.getRoleCode(),
                    null);
            return;
        }

        int createdCount = 0;
        int skippedCount = 0;
        for (Appointment appointment : affectedAppointments) {
            boolean created = safeCreateNotice(appointment.getId(), NoticeTypeEnum.SCHEDULE_CANCEL, operator, null);
            if (created) {
                createdCount++;
            } else {
                skippedCount++;
            }
        }

        saveNoticeLog(null, null, NoticeActionTypeEnum.SCHEDULE_CANCEL_SCAN.getCode(),
                NoticeActionResultEnum.SUCCESS.getCode(),
                String.format("停诊通知处理完成，受影响预约:%d，生成:%d，跳过:%d",
                        affectedAppointments.size(), createdCount, skippedCount),
                operator == null ? null : operator.getId(),
                operator == null ? SYSTEM_ROLE : operator.getRoleCode(),
                null);
    }

    private void submitAfterCommit(Runnable task) {
        if (TransactionSynchronizationManager.isSynchronizationActive()
                && TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    task.run();
                }
            });
            return;
        }
        task.run();
    }

    private LambdaQueryWrapper<Notice> buildNoticeQuery(NoticePageQueryDTO queryDTO) {
        LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(queryDTO.getNoticeType())) {
            queryWrapper.eq(Notice::getNoticeType, queryDTO.getNoticeType());
        }
        if (queryDTO.getReadStatus() != null) {
            queryWrapper.eq(Notice::getReadStatus, queryDTO.getReadStatus());
        }
        if (queryDTO.getSendStatus() != null) {
            queryWrapper.eq(Notice::getSendStatus, queryDTO.getSendStatus());
        }

        LocalDateTime startTime = parseDateTime(queryDTO.getStartTime(), false);
        LocalDateTime endTime = parseDateTime(queryDTO.getEndTime(), true);
        if (startTime != null) {
            queryWrapper.ge(Notice::getCreateTime, startTime);
        }
        if (endTime != null) {
            queryWrapper.le(Notice::getCreateTime, endTime);
        }
        return queryWrapper;
    }

    private void appendRecipientKeywordFilter(LambdaQueryWrapper<Notice> queryWrapper, String recipientKeyword) {
        if (StringUtils.isBlank(recipientKeyword)) {
            return;
        }
        List<Long> userIds = findUserIdsByKeyword(recipientKeyword);
        if (userIds.isEmpty()) {
            queryWrapper.eq(Notice::getRecipientUserId, -1L);
            return;
        }
        queryWrapper.in(Notice::getRecipientUserId, userIds);
    }

    private void appendNoticeLogRecipientKeywordFilter(LambdaQueryWrapper<NoticeLog> queryWrapper, String recipientKeyword) {
        if (StringUtils.isBlank(recipientKeyword)) {
            return;
        }
        List<Long> userIds = findUserIdsByKeyword(recipientKeyword);
        if (userIds.isEmpty()) {
            queryWrapper.eq(NoticeLog::getRecipientUserId, -1L);
            return;
        }
        queryWrapper.in(NoticeLog::getRecipientUserId, userIds);
    }

    private List<Long> findUserIdsByKeyword(String keyword) {
        return userMapper.selectList(new LambdaQueryWrapper<User>()
                        .and(wrapper -> wrapper.like(User::getName, keyword)
                                .or()
                                .like(User::getUsername, keyword)
                                .or()
                                .like(User::getPhone, keyword)))
                .stream()
                .map(User::getId)
                .toList();
    }

    private <T> Page<T> buildPage(Integer currentPage, Integer size) {
        long pageNo = currentPage == null || currentPage < 1 ? 1 : currentPage;
        long pageSize = size == null || size < 1 ? 10 : size;
        return new Page<>(pageNo, pageSize);
    }

    private Notice requireNoticeById(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null || !STATUS_VALID.equals(notice.getStatus())) {
            throw new ServiceException("通知不存在");
        }
        return notice;
    }

    private User requireCurrentUser() {
        User currentUser = JwtTokenUtils.getCurrentUser();
        if (currentUser == null) {
            throw new ServiceException("请先登录");
        }
        return currentUser;
    }

    private Long requireCurrentUserId() {
        Long currentUserId = JwtTokenUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new ServiceException("请先登录");
        }
        return currentUserId;
    }

    private void requireAdmin() {
        User currentUser = requireCurrentUser();
        if (!"ADMIN".equals(currentUser.getRoleCode())) {
            throw new ServiceException("无权限访问通知管理");
        }
    }

    private void saveNoticeLog(Long noticeId, Long recipientUserId, String actionType, String actionResult,
                               String actionMessage, Long operatorUserId, String operatorRole, String taskBatchNo) {
        NoticeLog noticeLog = new NoticeLog();
        noticeLog.setNoticeId(noticeId);
        noticeLog.setRecipientUserId(recipientUserId);
        noticeLog.setActionType(actionType);
        noticeLog.setActionResult(actionResult);
        noticeLog.setActionMessage(actionMessage);
        noticeLog.setOperatorUserId(operatorUserId);
        noticeLog.setOperatorRole(operatorRole);
        noticeLog.setTaskBatchNo(taskBatchNo);
        noticeLog.setCreateTime(LocalDateTime.now());
        noticeLogMapper.insert(noticeLog);
    }

    private Long resolveRecipientUserId(Long appointmentId) {
        if (appointmentId == null) {
            return null;
        }
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            return null;
        }
        Patient patient = patientMapper.selectById(appointment.getPatientId());
        return patient == null ? null : patient.getUserId();
    }

    private void fillNoticeInfo(List<Notice> notices) {
        if (notices == null || notices.isEmpty()) {
            return;
        }

        Map<Long, User> userMap = listUsers(toIdSet(notices, Notice::getRecipientUserId));
        Map<Long, Doctor> doctorMap = listDoctors(toIdSet(notices, Notice::getDoctorId));
        Map<Long, Department> departmentMap = listDepartments(toIdSet(notices, Notice::getDepartmentId));
        Map<Long, Appointment> appointmentMap = listAppointments(toIdSet(notices, Notice::getAppointmentId));

        for (Notice notice : notices) {
            User recipient = userMap.get(notice.getRecipientUserId());
            if (recipient != null) {
                notice.setRecipientName(recipient.getName());
                notice.setRecipientUsername(recipient.getUsername());
            }
            Doctor doctor = doctorMap.get(notice.getDoctorId());
            if (doctor != null) {
                notice.setDoctorName(doctor.getName());
            }
            Department department = departmentMap.get(notice.getDepartmentId());
            if (department != null) {
                notice.setDepartmentName(department.getDeptName());
            }
            Appointment appointment = appointmentMap.get(notice.getAppointmentId());
            if (appointment != null) {
                notice.setAppointmentNo(appointment.getAppointmentNo());
                notice.setAppointmentDate(appointment.getAppointmentDate());
                notice.setTimeSlot(appointment.getTimeSlot());
            }
        }
    }

    private void fillNoticeLogInfo(List<NoticeLog> noticeLogs) {
        if (noticeLogs == null || noticeLogs.isEmpty()) {
            return;
        }

        Map<Long, Notice> noticeMap = listNotices(toIdSet(noticeLogs, NoticeLog::getNoticeId));
        Set<Long> userIds = new HashSet<>(toIdSet(noticeLogs, NoticeLog::getRecipientUserId));
        userIds.addAll(toIdSet(noticeLogs, NoticeLog::getOperatorUserId));
        Map<Long, User> userMap = listUsers(userIds);

        for (NoticeLog noticeLog : noticeLogs) {
            Notice notice = noticeMap.get(noticeLog.getNoticeId());
            if (notice != null) {
                noticeLog.setNoticeTitle(notice.getTitle());
            }
            User recipient = userMap.get(noticeLog.getRecipientUserId());
            if (recipient != null) {
                noticeLog.setRecipientName(recipient.getName());
            }
            User operator = userMap.get(noticeLog.getOperatorUserId());
            if (operator != null) {
                noticeLog.setOperatorName(operator.getName());
            }
        }
    }

    private Map<Long, User> listUsers(Set<Long> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }
        return userMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }

    private Map<Long, Doctor> listDoctors(Set<Long> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }
        return doctorMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(Doctor::getId, Function.identity()));
    }

    private Map<Long, Department> listDepartments(Set<Long> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }
        return departmentMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(Department::getId, Function.identity()));
    }

    private Map<Long, Appointment> listAppointments(Set<Long> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }
        return appointmentMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(Appointment::getId, Function.identity()));
    }

    private Map<Long, Notice> listNotices(Set<Long> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }
        return noticeMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(Notice::getId, Function.identity()));
    }

    private <T> Set<Long> toIdSet(Collection<T> source, Function<T, Long> mapper) {
        return source.stream()
                .map(mapper)
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toSet());
    }

    private LocalDateTime parseDateTime(String text, boolean endOfDay) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        try {
            if (text.length() == 10) {
                LocalDate date = LocalDate.parse(text);
                return endOfDay ? date.atTime(LocalTime.MAX.withNano(0)) : date.atStartOfDay();
            }
            return LocalDateTime.parse(text, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ServiceException("时间格式不正确，应为yyyy-MM-dd或yyyy-MM-dd HH:mm:ss");
        }
    }
}
