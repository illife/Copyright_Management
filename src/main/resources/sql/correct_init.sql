-- =====================================================
-- 版权管理平台数据库初始化脚本（修正版）
-- 使用 users 表（复数形式，与代码一致）
-- =====================================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS copyright_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE copyright_management;

-- =====================================================
-- 1. 用户表 (users) - 使用复数形式
-- =====================================================
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `address` VARCHAR(42) NOT NULL COMMENT '区块链地址',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色: ADMIN-管理员, USER-普通用户',
    `status` INT(1) NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
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
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
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
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
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
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_copyright_id` (`copyright_id`),
    KEY `idx_payer_address` (`payer_address`),
    KEY `idx_payee_address` (`payee_address`),
    KEY `idx_payment_time` (`payment_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='版税支付记录表';

-- =====================================================
-- 插入默认管理员账户到 users 表
-- =====================================================
INSERT INTO `users` (`address`, `username`, `email`, `role`, `status`)
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
SELECT COUNT(*) AS user_count FROM `users`;
SELECT * FROM `users` WHERE `address` = '0xb82ea6626cb8cd46081b2dc6ece702864490f366';
