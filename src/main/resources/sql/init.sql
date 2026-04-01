na-- 版权管理平台数据库初始化脚本

-- 1. 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
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

-- 2. 创建用户注册申请表
CREATE TABLE IF NOT EXISTS `user_registration` (
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

-- 3. 插入默认管理员账户
-- 地址: 0xb82ea6626cb8cd46081b2dc6ece702864490f366
-- 用户名: admin
-- 角色: ADMIN
INSERT INTO `user` (`address`, `username`, `email`, `role`, `status`)
VALUES (
    '0xb82ea6626cb8cd46081b2dc6ece702864490f366',
    'admin',
    'admin@copyright.com',
    'ADMIN',
    1
) ON DUPLICATE KEY UPDATE `username` = 'admin', `role` = 'ADMIN';

-- 查询确认管理员已创建
SELECT * FROM `user` WHERE `address` = '0xb82ea6626cb8cd46081b2dc6ece702864490f366';
