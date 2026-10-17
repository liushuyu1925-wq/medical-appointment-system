package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.example.springboot.DTO.NoticeLogPageQueryDTO;
import org.example.springboot.DTO.NoticePageQueryDTO;
import org.example.springboot.common.Result;
import org.example.springboot.entity.Notice;
import org.example.springboot.entity.NoticeLog;
import org.example.springboot.service.NoticeService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "通知管理接口")
@RestController
@RequestMapping("/notice")
public class NoticeController {
    @Resource
    private NoticeService noticeService;

    @Operation(summary = "获取我的通知分页列表")
    @GetMapping("/my/page")
    public Result<?> getMyNoticePage(@ModelAttribute NoticePageQueryDTO queryDTO) {
        Page<Notice> page = noticeService.getMyNoticePage(queryDTO);
        return Result.success(page);
    }

    @Operation(summary = "获取我的未读通知数量")
    @GetMapping("/my/unread-count")
    public Result<?> getMyUnreadCount() {
        return Result.success(noticeService.getMyUnreadCount());
    }

    @Operation(summary = "获取我的通知详情")
    @GetMapping("/{id}")
    public Result<?> getMyNoticeDetail(@PathVariable Long id) {
        return Result.success(noticeService.getMyNoticeDetail(id));
    }

    @Operation(summary = "标记单条通知已读")
    @PutMapping("/read/{id}")
    public Result<?> markRead(@PathVariable Long id) {
        noticeService.markRead(id);
        return Result.success();
    }

    @Operation(summary = "全部标记已读")
    @PutMapping("/read-all")
    public Result<?> markAllRead() {
        noticeService.markAllRead();
        return Result.success();
    }

    @Operation(summary = "后台分页查询通知记录")
    @GetMapping("/admin/page")
    public Result<?> getAdminNoticePage(@ModelAttribute NoticePageQueryDTO queryDTO) {
        return Result.success(noticeService.getAdminNoticePage(queryDTO));
    }

    @Operation(summary = "后台查看通知详情")
    @GetMapping("/admin/{id}")
    public Result<?> getAdminNoticeDetail(@PathVariable Long id) {
        return Result.success(noticeService.getAdminNoticeDetail(id));
    }

    @Operation(summary = "后台分页查询通知审计日志")
    @GetMapping("/log/page")
    public Result<?> getNoticeLogPage(@ModelAttribute NoticeLogPageQueryDTO queryDTO) {
        Page<NoticeLog> page = noticeService.getNoticeLogPage(queryDTO);
        return Result.success(page);
    }

    @Operation(summary = "后台查看通知审计日志详情")
    @GetMapping("/log/{id}")
    public Result<?> getNoticeLogDetail(@PathVariable Long id) {
        return Result.success(noticeService.getNoticeLogDetail(id));
    }
    
    @Operation(summary = "后台删除通知")
    @DeleteMapping("/admin/{id}")
    public Result<?> deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return Result.success();
    }
}
