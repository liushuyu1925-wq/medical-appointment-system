-- Table definitions only. No exported records or account credentials.
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE `appointment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `patient_id` bigint NOT NULL COMMENT '患者ID',
  `doctor_id` bigint NOT NULL COMMENT '医生ID',
  `schedule_id` bigint NOT NULL COMMENT '排班ID',
  `appointment_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '预约编号',
  `appointment_date` date NOT NULL COMMENT '预约日期',
  `time_slot` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '时间段(上午/下午/晚上)',
  `symptoms` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '症状描述',
  `status` tinyint DEFAULT '1' COMMENT '状态(0:取消,1:待就诊,2:已就诊)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_appointment_no` (`appointment_no`) USING BTREE,
  KEY `idx_patient_id` (`patient_id`) USING BTREE,
  KEY `idx_doctor_id` (`doctor_id`) USING BTREE,
  KEY `idx_schedule_id` (`schedule_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='预约表';

CREATE TABLE `department` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门ID',
  `dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '部门名称',
  `dept_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '部门编码',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '部门描述',
  `director_id` bigint DEFAULT NULL COMMENT '负责人ID',
  `status` tinyint DEFAULT '1' COMMENT '状态(0:禁用,1:启用)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_dept_code` (`dept_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='部门表';

CREATE TABLE `diagnosis_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '诊断记录ID',
  `record_id` bigint DEFAULT NULL COMMENT '关联就诊记录ID',
  `diagnosis_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '诊断代码',
  `diagnosis_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '诊断名称',
  `severity` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '严重程度',
  `diagnosis_date` date DEFAULT NULL COMMENT '诊断日期',
  `status` int DEFAULT '1' COMMENT '状态: 1-有效, 0-无效',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `record_id` (`record_id`),
  CONSTRAINT `diagnosis_record_ibfk_1` FOREIGN KEY (`record_id`) REFERENCES `medical_record` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='诊断记录表';

CREATE TABLE `doctor` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '医生ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `doctor_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '医生编号',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '姓名',
  `department_id` bigint NOT NULL COMMENT '科室ID',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '职称',
  `expertise` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '专长',
  `introduction` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '简介',
  `status` tinyint DEFAULT '1' COMMENT '状态(0:离职,1:在职)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_doctor_no` (`doctor_no`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_department_id` (`department_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='医生表';

CREATE TABLE `examination_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '检查记录ID',
  `record_id` bigint DEFAULT NULL COMMENT '关联就诊记录ID',
  `examination_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '检查类型',
  `examination_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '检查项目名称',
  `examination_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '检查结果',
  `normal_range` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '正常范围',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '单位',
  `examination_date` date DEFAULT NULL COMMENT '检查日期',
  `status` int DEFAULT '1' COMMENT '状态: 1-正常, 0-异常',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `record_id` (`record_id`),
  CONSTRAINT `examination_record_ibfk_1` FOREIGN KEY (`record_id`) REFERENCES `medical_record` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='检查记录表';

CREATE TABLE `follow_up_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '随访记录ID',
  `record_id` bigint DEFAULT NULL COMMENT '关联就诊记录ID',
  `patient_id` bigint DEFAULT NULL COMMENT '患者ID',
  `doctor_id` bigint DEFAULT NULL COMMENT '医生ID',
  `follow_up_date` date DEFAULT NULL COMMENT '随访日期',
  `follow_up_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '随访方式',
  `follow_up_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '随访内容',
  `next_follow_up` date DEFAULT NULL COMMENT '下次随访日期',
  `status` int DEFAULT '1' COMMENT '状态: 1-已完成, 0-未完成',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `record_id` (`record_id`),
  KEY `patient_id` (`patient_id`),
  KEY `doctor_id` (`doctor_id`),
  CONSTRAINT `follow_up_record_ibfk_1` FOREIGN KEY (`record_id`) REFERENCES `medical_record` (`id`) ON DELETE SET NULL,
  CONSTRAINT `follow_up_record_ibfk_2` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`id`) ON DELETE SET NULL,
  CONSTRAINT `follow_up_record_ibfk_3` FOREIGN KEY (`doctor_id`) REFERENCES `doctor` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='随访记录表';

CREATE TABLE `medical_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `record_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '记录编号',
  `patient_id` bigint NOT NULL COMMENT '患者ID',
  `doctor_id` bigint NOT NULL COMMENT '医生ID',
  `appointment_id` bigint DEFAULT NULL COMMENT '预约ID',
  `diagnosis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '诊断结果',
  `treatment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '治疗方案',
  `record_date` date NOT NULL COMMENT '就诊日期',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '医生备注',
  `follow_up` date DEFAULT NULL COMMENT '随访日期',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_record_no` (`record_no`) USING BTREE,
  KEY `idx_patient_id` (`patient_id`) USING BTREE,
  KEY `idx_doctor_id` (`doctor_id`) USING BTREE,
  KEY `idx_appointment_id` (`appointment_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='就诊记录表';

CREATE TABLE `medicine` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '药品ID',
  `medicine_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '药品编码',
  `medicine_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '药品名称',
  `specification` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '规格',
  `dosage_form` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '剂型(片剂/胶囊/注射剂等)',
  `manufacturer` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '生产厂家',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '类别(处方药/非处方药)',
  `price` decimal(10,2) DEFAULT NULL COMMENT '单价',
  `stock` int DEFAULT '0' COMMENT '库存量',
  `instructions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '使用说明',
  `status` tinyint DEFAULT '1' COMMENT '状态(0:下架,1:上架)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `category_id` int DEFAULT NULL COMMENT '分类id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_medicine_code` (`medicine_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='药品表';

CREATE TABLE `medicine_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `category_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `category_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类编码',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '分类描述',
  `status` tinyint DEFAULT '1' COMMENT '状态(0:禁用,1:启用)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_category_code` (`category_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='药品分类表';

CREATE TABLE `notice` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `recipient_user_id` bigint NOT NULL COMMENT '接收用户ID',
  `recipient_role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '接收角色编码',
  `appointment_id` bigint DEFAULT NULL COMMENT '预约ID',
  `doctor_id` bigint DEFAULT NULL COMMENT '医生ID',
  `department_id` bigint DEFAULT NULL COMMENT '科室ID',
  `notice_type` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知类型',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知标题',
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知内容',
  `read_status` tinyint NOT NULL DEFAULT '0' COMMENT '已读状态(0:未读,1:已读)',
  `send_status` tinyint NOT NULL DEFAULT '1' COMMENT '发送状态(0:失败,1:成功)',
  `retry_count` int NOT NULL DEFAULT '0' COMMENT '重试次数',
  `trigger_time` datetime DEFAULT NULL COMMENT '触发时间',
  `read_time` datetime DEFAULT NULL COMMENT '已读时间',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态(0:停用,1:有效)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_notice_appointment_type_recipient` (`appointment_id`,`notice_type`,`recipient_user_id`,`trigger_time`) USING BTREE,
  KEY `idx_recipient_user_id` (`recipient_user_id`) USING BTREE,
  KEY `idx_notice_type` (`notice_type`) USING BTREE,
  KEY `idx_read_status` (`read_status`) USING BTREE,
  KEY `idx_trigger_time` (`trigger_time`) USING BTREE,
  KEY `idx_appointment_id` (`appointment_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='站内通知表';

CREATE TABLE `notice_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知日志ID',
  `notice_id` bigint DEFAULT NULL COMMENT '通知ID',
  `recipient_user_id` bigint DEFAULT NULL COMMENT '接收用户ID',
  `action_type` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作类型',
  `action_result` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作结果',
  `action_message` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '操作说明',
  `operator_user_id` bigint DEFAULT NULL COMMENT '操作人ID',
  `operator_role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '操作人角色',
  `task_batch_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '任务批次号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_notice_id` (`notice_id`) USING BTREE,
  KEY `idx_recipient_user_id` (`recipient_user_id`) USING BTREE,
  KEY `idx_action_type` (`action_type`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='通知审计日志表';

CREATE TABLE `order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单编号',
  `appointment_id` bigint NOT NULL COMMENT '关联预约ID',
  `patient_id` bigint NOT NULL COMMENT '患者ID',
  `doctor_id` bigint NOT NULL COMMENT '医生ID',
  `amount` decimal(10,2) NOT NULL COMMENT '订单金额',
  `order_status` tinyint NOT NULL DEFAULT '0' COMMENT '0待支付 1成功 2失败 3取消 4退款',
  `payment_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'simulate',
  `expire_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_appointment_id` (`appointment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `patient` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '患者ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `patient_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '患者编号',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '姓名',
  `id_card` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '身份证号',
  `birthday` date DEFAULT NULL COMMENT '出生日期',
  `sex` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '性别',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '联系电话',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '住址',
  `medical_history` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '病史',
  `allergies` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '过敏史',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_patient_no` (`patient_no`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='患者表';

CREATE TABLE `payment_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(50) NOT NULL COMMENT '订单号（唯一）',
  `appointment_id` bigint NOT NULL COMMENT '关联的预约ID',
  `amount` decimal(10,2) NOT NULL COMMENT '支付金额',
  `payment_method` varchar(20) DEFAULT 'simulate' COMMENT '支付方式：simulate/wechat/alipay',
  `payment_status` tinyint NOT NULL DEFAULT '0' COMMENT '支付状态：0待支付 1支付成功 2支付失败 3已取消 4已过期',
  `transaction_id` varchar(100) DEFAULT NULL COMMENT '第三方支付流水号',
  `payment_time` datetime DEFAULT NULL COMMENT '支付完成时间',
  `expire_time` datetime DEFAULT NULL COMMENT '订单过期时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_appointment_id` (`appointment_id`),
  CONSTRAINT `fk_payment_appointment` FOREIGN KEY (`appointment_id`) REFERENCES `appointment` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='支付订单表';

CREATE TABLE `prescription` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '处方ID',
  `prescription_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '处方编号',
  `patient_id` bigint NOT NULL COMMENT '患者ID',
  `doctor_id` bigint NOT NULL COMMENT '医生ID',
  `record_id` bigint NOT NULL COMMENT '就诊记录ID',
  `prescription_date` date NOT NULL COMMENT '处方日期',
  `diagnosis` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '诊断',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '备注',
  `status` tinyint DEFAULT '0' COMMENT '状态(0:未取药,1:已取药)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_prescription_no` (`prescription_no`) USING BTREE,
  KEY `idx_patient_id` (`patient_id`) USING BTREE,
  KEY `idx_doctor_id` (`doctor_id`) USING BTREE,
  KEY `idx_record_id` (`record_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='处方表';

CREATE TABLE `prescription_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `prescription_id` bigint NOT NULL COMMENT '处方ID',
  `medicine_id` bigint NOT NULL COMMENT '药品ID',
  `dosage` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用量',
  `frequency` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '频次(一日三次/每日一次)',
  `days` int DEFAULT NULL COMMENT '用药天数',
  `quantity` int DEFAULT NULL COMMENT '数量',
  `usage` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用法(口服/外用等)',
  `notes` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_prescription_id` (`prescription_id`) USING BTREE,
  KEY `idx_medicine_id` (`medicine_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='处方明细表';

CREATE TABLE `report` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '报告ID',
  `report_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '报告编号',
  `record_id` bigint DEFAULT NULL COMMENT '关联就诊记录ID',
  `patient_id` bigint DEFAULT NULL COMMENT '患者ID',
  `doctor_id` bigint DEFAULT NULL COMMENT '医生ID',
  `report_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '报告类型',
  `report_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '报告名称',
  `report_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '报告内容',
  `report_date` date DEFAULT NULL COMMENT '报告日期',
  `status` int DEFAULT '1' COMMENT '状态: 1-正常, 0-异常',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `report_no` (`report_no`),
  KEY `record_id` (`record_id`),
  KEY `patient_id` (`patient_id`),
  KEY `doctor_id` (`doctor_id`),
  CONSTRAINT `report_ibfk_1` FOREIGN KEY (`record_id`) REFERENCES `medical_record` (`id`) ON DELETE SET NULL,
  CONSTRAINT `report_ibfk_2` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`id`) ON DELETE SET NULL,
  CONSTRAINT `report_ibfk_3` FOREIGN KEY (`doctor_id`) REFERENCES `doctor` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报告表';

CREATE TABLE `review` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `appointment_id` bigint NOT NULL COMMENT '预约ID',
  `patient_id` bigint NOT NULL COMMENT '患者ID',
  `doctor_id` bigint NOT NULL COMMENT '医生ID',
  `rating` int NOT NULL COMMENT '评分（1-5）',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '评价内容',
  `reply` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '医生回复',
  `status` tinyint DEFAULT '1' COMMENT '状态：1-正常，0-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_appointment_id` (`appointment_id`),
  CONSTRAINT `review_ibfk_1` FOREIGN KEY (`appointment_id`) REFERENCES `appointment` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医生评价表';

CREATE TABLE `schedule` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '排班ID',
  `doctor_id` bigint NOT NULL COMMENT '医生ID',
  `schedule_date` date NOT NULL COMMENT '排班日期',
  `time_slot` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '时间段(上午/下午/晚上)',
  `max_patients` int DEFAULT '0' COMMENT '最大接诊人数',
  `current_patients` int DEFAULT '0' COMMENT '当前预约人数',
  `status` tinyint DEFAULT '1' COMMENT '状态(0:停诊,1:正常)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_doctor_id` (`doctor_id`) USING BTREE,
  KEY `idx_schedule_date` (`schedule_date`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2200 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='排班表';

CREATE TABLE `treatment_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '治疗记录ID',
  `record_id` bigint DEFAULT NULL COMMENT '关联就诊记录ID',
  `treatment_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '治疗类型',
  `treatment_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '治疗项目名称',
  `dosage` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '剂量',
  `frequency` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '频率',
  `duration` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '疗程',
  `start_date` date DEFAULT NULL COMMENT '开始日期',
  `end_date` date DEFAULT NULL COMMENT '结束日期',
  `status` int DEFAULT '1' COMMENT '状态: 1-进行中, 0-已完成',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `record_id` (`record_id`),
  CONSTRAINT `treatment_record_ibfk_1` FOREIGN KEY (`record_id`) REFERENCES `medical_record` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='治疗记录表';

CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码(加密存储)',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '真实姓名',
  `avatar` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '头像地址',
  `sex` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '性别',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '邮箱',
  `role_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '角色代码',
  `status` tinyint DEFAULT '1' COMMENT '状态(0:禁用,1:启用)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_username` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='用户表';

SET FOREIGN_KEY_CHECKS = 1;
