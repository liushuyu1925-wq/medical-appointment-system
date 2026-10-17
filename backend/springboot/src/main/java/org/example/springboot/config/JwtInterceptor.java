package org.example.springboot.config;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.springboot.common.ResultCode;
import org.example.springboot.entity.Doctor;
import org.example.springboot.entity.Patient;
import org.example.springboot.entity.User;
import org.example.springboot.mapper.DoctorMapper;
import org.example.springboot.mapper.PatientMapper;
import org.example.springboot.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;




@Component
public class JwtInterceptor implements HandlerInterceptor {
    public static final Logger LOGGER = LoggerFactory.getLogger(HandlerInterceptor.class);
    @Resource
    private UserService userService;
    @Resource
    private PatientMapper patientMapper;
    @Resource
    private DoctorMapper doctorMapper;

    @Override
    public boolean preHandle(HttpServletRequest request,  HttpServletResponse response,  Object handler) throws Exception {
        String requestPath = request.getRequestURI();
        String method = request.getMethod();
        if (isPublicRequest(method, requestPath)) {
            return true;
        }

        String token = request.getHeader("token");
        if (StringUtils.isBlank(token)) {
            token = request.getParameter("token");
        }
        if (StringUtils.isBlank(token)) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, ResultCode.UNAUTHORIZED.getCode(), "Token缺失");
            return false;
        }

        User user;
        try {
            String userId = JWT.decode(token).getAudience().get(0);
            user = userService.getUserById(Long.valueOf(userId));
        } catch (Exception e) {
            String errMsg = "token失效，重新登录！";
            LOGGER.error(errMsg + " ,token=" + token, e);
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, ResultCode.UNAUTHORIZED.getCode(), errMsg);
            return false;
        }
        if (user == null) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, ResultCode.UNAUTHORIZED.getCode(), "User not found");
            return false;
        }
        try {
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
            jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, ResultCode.UNAUTHORIZED.getCode(), "token认证失败，重新登录！");
            return false;
        }

        if (!hasPermission(user, method, requestPath)) {
            LOGGER.warn("权限不足，role={}, path={}, method={}", user.getRoleCode(), requestPath, method);
            writeError(response, HttpServletResponse.SC_FORBIDDEN, ResultCode.FORBIDDEN.getCode(), ResultCode.FORBIDDEN.getMsg());
            return false;
        }
        LOGGER.info("验证成功，允许放行。{}",user);
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    private boolean isPublicRequest(String method, String path) {
        if (path.startsWith("/api/img/") || path.startsWith("/img/")) {
            return true;
        }
        if (path.equals("/api/user/login") || path.equals("/api/user/register") || path.equals("/api/user/forget")) {
            return true;
        }
        if (path.equals("/api/patient/register")) {
            return true;
        }
        if (path.equals("/api/user/paytest") || path.equals("/api/payments/test") || path.equals("/api/payments/mock-callback")) {
            return true;
        }
        if (!"GET".equalsIgnoreCase(method)) {
            return false;
        }
        return path.equals("/api/department/list")
                || path.equals("/api/department/page")
                || path.matches("^/api/department/\\d+$")
                || path.equals("/api/doctor")
                || path.equals("/api/doctor/list")
                || path.equals("/api/doctor/page")
                || path.matches("^/api/doctor/\\d+$")
                || path.matches("^/api/doctor/department/\\d+$")
                || path.matches("^/api/schedule/doctor/\\d+$")
                || path.matches("^/api/schedule/date/.+$")
                || path.matches("^/api/api/review/doctor/\\d+/(rating|list)$");
    }

    private boolean hasPermission(User user, String method, String path) {
        String roleCode = user.getRoleCode();
        if ("ADMIN".equals(roleCode)) {
            return true;
        }

        if (isSelfServicePath(user, path)) {
            return true;
        }

        if ("DOCTOR".equals(roleCode)) {
            return isDoctorPath(user, method, path);
        }

        if ("PATIENT".equals(roleCode)) {
            return isPatientPath(user, method, path);
        }

        return false;
    }

    private boolean isSelfServicePath(User user, String path) {
        if (path.equals("/api/user/current")) {
            return true;
        }
        if (path.matches("^/api/user/\\d+$")) {
            return pathIdEquals(path, "^/api/user/(\\d+)$", user.getId());
        }
        if (path.matches("^/api/user/password/\\d+$")) {
            return pathIdEquals(path, "^/api/user/password/(\\d+)$", user.getId());
        }
        return path.startsWith("/api/notice/my/")
                || path.startsWith("/api/notice/read")
                || path.equals("/api/file/upload/img")
                || path.equals("/api/file/uploadMultiple");
    }

    private boolean isDoctorPath(User user, String method, String path) {
        if (path.matches("^/api/appointment/doctor/\\d+$")) {
            Long doctorId = getCurrentDoctorId(user.getId());
            return "GET".equalsIgnoreCase(method)
                    && doctorId != null
                    && pathIdEquals(path, "^/api/appointment/doctor/(\\d+)$", doctorId);
        }
        return path.startsWith("/api/diagnosis")
                || path.startsWith("/api/examination")
                || path.startsWith("/api/treatment")
                || path.startsWith("/api/medical-record")
                || path.startsWith("/api/prescription")
                || path.startsWith("/api/report")
                || path.startsWith("/api/follow-up")
                || path.equals("/api/patient/list")
                || path.equals("/api/doctor/current")
                || path.matches("^/api/api/review/.+");
    }

    private boolean isPatientPath(User user, String method, String path) {
        Long patientId = getCurrentPatientId(user.getId());
        if (path.equals("/api/patient/current")) {
            return true;
        }
        if (path.matches("^/api/patient/\\d+$")) {
            return patientId != null && pathIdEquals(path, "^/api/patient/(\\d+)$", patientId);
        }
        if (path.startsWith("/api/payments/create") || path.startsWith("/api/payments/query/")) {
            return true;
        }
        if (path.startsWith("/api/api/review/add")) {
            return true;
        }
        if (path.matches("^/api/api/review/patient/\\d+/list$")) {
            return patientId != null && pathIdEquals(path, "^/api/api/review/patient/(\\d+)/list$", patientId);
        }
        if (path.matches("^/api/medical-record/patient/\\d+$")) {
            return patientId != null && pathIdEquals(path, "^/api/medical-record/patient/(\\d+)$", patientId);
        }
        if (path.matches("^/api/prescription/patient/\\d+$")) {
            return patientId != null && pathIdEquals(path, "^/api/prescription/patient/(\\d+)$", patientId);
        }
        if (path.matches("^/api/follow-up/patient/\\d+$")) {
            return patientId != null && pathIdEquals(path, "^/api/follow-up/patient/(\\d+)$", patientId);
        }
        return ("POST".equalsIgnoreCase(method) && path.equals("/api/appointment"))
                || path.equals("/api/appointment/my")
                || path.startsWith("/api/appointment/cancel/")
                || path.startsWith("/api/medical-record/my")
                || path.startsWith("/api/prescription/my");
    }

    private boolean pathIdEquals(String path, String regex, Long expectedId) {
        Long actualId = extractId(path, regex);
        return actualId != null && actualId.equals(expectedId);
    }

    private Long extractId(String path, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(path);
        if (!matcher.matches()) {
            return null;
        }
        return Long.valueOf(matcher.group(1));
    }

    private Long getCurrentPatientId(Long userId) {
        Patient patient = patientMapper.selectOne(new LambdaQueryWrapper<Patient>().eq(Patient::getUserId, userId));
        return patient == null ? null : patient.getId();
    }

    private Long getCurrentDoctorId(Long userId) {
        Doctor doctor = doctorMapper.selectOne(new LambdaQueryWrapper<Doctor>().eq(Doctor::getUserId, userId));
        return doctor == null ? null : doctor.getId();
    }

    private void writeError(HttpServletResponse response, int status, String code, String message) throws Exception {
        response.setStatus(status);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print("{\"code\":\"" + code + "\",\"msg\":\"" + message + "\"}");
    }
}
