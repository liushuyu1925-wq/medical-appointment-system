package org.example.springboot.task;

import jakarta.annotation.Resource;
import org.example.springboot.service.NoticeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NoticeReminderTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(NoticeReminderTask.class);

    @Resource
    private NoticeService noticeService;

    @Scheduled(cron = "0 0 8 * * ?")
    public void runVisitReminderTask() {
        LOGGER.info("开始执行就诊前一天提醒任务");
        noticeService.executeVisitReminderTask();
        LOGGER.info("就诊前一天提醒任务执行完成");
    }
}
