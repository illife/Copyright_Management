-- =====================================================
-- 版权管理平台完整数据库初始化脚本
-- =====================================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS copyright_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE copyright_management;

-- =====================================================
-- 1. 用户表
-- =====================================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `address` VARCHAR(42) NOT NULL COMMENT '区块链地址',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色: ADMIN-管理员, USER-普通用户',
    `status` INT(1) NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_address` (`address`),
    KEY `idx_username` (`username`),
    KEY `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- =====================================================
-- 2. 用户注册申请表
-- =====================================================
DROP TABLE IF EXISTS `user_registration`;
CREATE TABLE `user_registration` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_address` VARCHAR(42) NOT NULL COMMENT '用户区块链地址',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    `email` VARCHAR(100) NOT NULL COMMENT '联系邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `description` TEXT COMMENT '申请说明',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '审核状态: PENDING-待审核, APPROVED-已通过, REJECTED-已拒绝',
    `reviewer` VARCHAR(50) DEFAULT NULL COMMENT '审核人',
    `review_comment` TEXT COMMENT '审核意见',
    `tx_hash` VARCHAR(66) DEFAULT NULL COMMENT '区块链交易哈希',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    `review_time` DATETIME DEFAULT NULL COMMENT '审核时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_address` (`user_address`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户注册申请表';

-- =====================================================
-- 3. 版权表
-- =====================================================
DROP TABLE IF EXISTS `copyrights`;
CREATE TABLE `copyrights` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `contract_id` VARCHAR(64) NOT NULL COMMENT '区块链版权ID',
    `owner_address` VARCHAR(42) NOT NULL COMMENT '版权所有者地址',
    `title` VARCHAR(200) NOT NULL COMMENT '作品标题',
    `author` VARCHAR(100) NOT NULL COMMENT '作者',
    `file_hash` VARCHAR(256) DEFAULT NULL COMMENT '文件哈希(IPFS)',
    `description` TEXT COMMENT '作品描述',
    `register_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `transaction_hash` VARCHAR(66) DEFAULT NULL COMMENT '交易哈希',
    `block_hash` VARCHAR(66) DEFAULT NULL COMMENT '区块哈希',
    `block_number` BIGINT(20) DEFAULT NULL COMMENT '区块号',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_contract_id` (`contract_id`),
    KEY `idx_owner_address` (`owner_address`),
    KEY `idx_title` (`title`),
    KEY `idx_author` (`author`),
    KEY `idx_register_time` (`register_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='版权表';

-- =====================================================
-- 4. 授权表
-- =====================================================
DROP TABLE IF EXISTS `licenses`;
CREATE TABLE `licenses` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `contract_id` VARCHAR(64) NOT NULL COMMENT '区块链授权ID',
    `copyright_id` BIGINT(20) NOT NULL COMMENT '版权ID',
    `copyright_contract_id` VARCHAR(64) NOT NULL COMMENT '版权合约ID',
    `applicant_address` VARCHAR(42) NOT NULL COMMENT '申请人地址',
    `licensee_address` VARCHAR(42) NOT NULL COMMENT '被授权人地址',
    `start_time` BIGINT(20) NOT NULL COMMENT '开始时间(Unix时间戳)',
    `end_time` BIGINT(20) NOT NULL COMMENT '结束时间(Unix时间戳)',
    `is_exclusive` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否独占授权: 0-非独占, 1-独占',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING-待审批, APPROVED-已批准, ACTIVE-已激活, REVOKED-已撤销, EXPIRED-已过期',
    `description` TEXT COMMENT '授权说明',
    `apply_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    `approve_time` DATETIME DEFAULT NULL COMMENT '批准时间',
    `activate_time` DATETIME DEFAULT NULL COMMENT '激活时间',
    `transaction_hash` VARCHAR(66) DEFAULT NULL COMMENT '最新交易哈希',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_contract_id` (`contract_id`),
    KEY `idx_copyright_id` (`copyright_id`),
    KEY `idx_applicant_address` (`applicant_address`),
    KEY `idx_licensee_address` (`licensee_address`),
    KEY `idx_status` (`status`),
    KEY `idx_apply_time` (`apply_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='授权表';

-- =====================================================
-- 5. 版税支付记录表
-- =====================================================
DROP TABLE IF EXISTS `royalty_payments`;
CREATE TABLE `royalty_payments` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `copyright_id` BIGINT(20) NOT NULL COMMENT '版权ID',
    `copyright_contract_id` VARCHAR(64) NOT NULL COMMENT '版权合约ID',
    `payer_address` VARCHAR(42) NOT NULL COMMENT '支付方地址',
    `payee_address` VARCHAR(42) NOT NULL COMMENT '接收方地址',
    `amount` BIGINT(20) NOT NULL COMMENT '金额(分)',
    `currency` VARCHAR(20) NOT NULL DEFAULT 'CNY' COMMENT '货币类型',
    `description` TEXT COMMENT '支付说明',
    `payment_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '支付时间',
    `transaction_hash` VARCHAR(66) DEFAULT NULL COMMENT '交易哈希',
    `block_hash` VARCHAR(66) DEFAULT NULL COMMENT '区块哈希',
    `block_number` BIGINT(20) DEFAULT NULL COMMENT '区块号',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_copyright_id` (`copyright_id`),
    KEY `idx_payer_address` (`payer_address`),
    KEY `idx_payee_address` (`payee_address`),
    KEY `idx_payment_time` (`payment_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='版税支付记录表';

-- =====================================================
-- 6. 交易记录表
-- =====================================================
DROP TABLE IF EXISTS `transaction_records`;
CREATE TABLE `transaction_records` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `transaction_hash` VARCHAR(66) NOT NULL COMMENT '交易哈希',
    `from_address` VARCHAR(42) NOT NULL COMMENT '发送方地址',
    `to_address` VARCHAR(42) NOT NULL COMMENT '接收方地址',
    `contract_address` VARCHAR(42) NOT NULL COMMENT '合约地址',
    `method_name` VARCHAR(100) NOT NULL COMMENT '调用的方法名',
    `input_params` TEXT COMMENT '输入参数(JSON)',
    `output_result` TEXT COMMENT '输出结果(JSON)',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING-待确认, CONFIRMED-已确认, FAILED-失败',
    `gas_used` BIGINT(20) DEFAULT NULL COMMENT '使用的gas',
    `block_number` BIGINT(20) DEFAULT NULL COMMENT '区块号',
    `block_hash` VARCHAR(66) DEFAULT NULL COMMENT '区块哈希',
    `transaction_time` DATETIME DEFAULT NULL COMMENT '交易时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_transaction_hash` (`transaction_hash`),
    KEY `idx_from_address` (`from_address`),
    KEY `idx_contract_address` (`contract_address`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易记录表';

-- =====================================================
-- 7. 合约事件表
-- =====================================================
DROP TABLE IF EXISTS `contract_events`;
CREATE TABLE `contract_events` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `contract_address` VARCHAR(42) NOT NULL COMMENT '合约地址',
    `event_name` VARCHAR(100) NOT NULL COMMENT '事件名称',
    `transaction_hash` VARCHAR(66) NOT NULL COMMENT '交易哈希',
    `block_hash` VARCHAR(66) DEFAULT NULL COMMENT '区块哈希',
    `block_number` BIGINT(20) DEFAULT NULL COMMENT '区块号',
    `event_data` TEXT COMMENT '事件数据(JSON)',
    `event_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '事件时间',
    `processed` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已处理: 0-未处理, 1-已处理',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_contract_address` (`contract_address`),
    KEY `idx_event_name` (`event_name`),
    KEY `idx_transaction_hash` (`transaction_hash`),
    KEY `idx_event_time` (`event_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合约事件表';

-- =====================================================
-- 8. 审计日志表
-- =====================================================
DROP TABLE IF EXISTS `audit_logs`;
CREATE TABLE `audit_logs` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_address` VARCHAR(42) DEFAULT NULL COMMENT '用户地址',
    `username` VARCHAR(50) DEFAULT NULL COMMENT '用户名',
    `operation` VARCHAR(100) NOT NULL COMMENT '操作类型',
    `resource_type` VARCHAR(50) DEFAULT NULL COMMENT '资源类型',
    `resource_id` BIGINT(20) DEFAULT NULL COMMENT '资源ID',
    `details` TEXT COMMENT '操作详情(JSON)',
    `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    `user_agent` VARCHAR(500) DEFAULT NULL COMMENT '用户代理',
    `status` VARCHAR(20) NOT NULL DEFAULT 'SUCCESS' COMMENT '状态: SUCCESS-成功, FAILED-失败',
    `error_message` TEXT COMMENT '错误信息',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_address` (`user_address`),
    KEY `idx_operation` (`operation`),
    KEY `idx_resource_type` (`resource_type`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审计日志表';

-- =====================================================
-- 插入默认管理员账户
-- =====================================================
INSERT INTO `user` (`address`, `username`, `email`, `role`, `status`)
VALUES (
    '0xb82ea6626cb8cd46081b2dc6ece702864490f366',
    'admin',
    'admin@copyright.com',
    'ADMIN',
    1
) ON DUPLICATE KEY UPDATE `username` = 'admin', `role` = 'ADMIN';

-- =====================================================
-- 验证数据
-- =====================================================
SELECT 'Database initialized successfully!' AS message;
SELECT COUNT(*) AS user_count FROM `user`;
SELECT * FROM `user` WHERE `address` = '0xb82ea6626cb8cd46081b2dc6ece702864490f366';
