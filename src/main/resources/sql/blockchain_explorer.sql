-- 区块链浏览器表结构
-- 用于缓存区块链数据，提高查询性能

-- 区块信息表
CREATE TABLE IF NOT EXISTS `block_info` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `block_number` BIGINT(20) NOT NULL COMMENT '区块号',
    `block_hash` VARCHAR(128) NOT NULL COMMENT '区块哈希',
    `parent_hash` VARCHAR(128) NOT NULL COMMENT '父区块哈希',
    `timestamp` DATETIME NOT NULL COMMENT '区块时间戳',
    `transaction_count` INT(11) NOT NULL DEFAULT 0 COMMENT '交易数量',
    `sealer` VARCHAR(64) DEFAULT NULL COMMENT '区块生成者',
    `gas_used` BIGINT(20) DEFAULT NULL COMMENT 'gas使用量',
    `gas_limit` BIGINT(20) DEFAULT NULL COMMENT 'gas限制',
    `block_size` BIGINT(20) DEFAULT NULL COMMENT '区块大小（字节）',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_block_number` (`block_number`),
    UNIQUE KEY `uk_block_hash` (`block_hash`),
    KEY `idx_timestamp` (`timestamp`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='区块信息表';

-- 交易信息表
CREATE TABLE IF NOT EXISTS `transaction_info` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `transaction_hash` VARCHAR(128) NOT NULL COMMENT '交易哈希',
    `block_number` BIGINT(20) NOT NULL COMMENT '区块号',
    `block_hash` VARCHAR(128) NOT NULL COMMENT '区块哈希',
    `transaction_index` INT(11) NOT NULL COMMENT '交易索引',
    `from` VARCHAR(64) NOT NULL COMMENT '发送方地址',
    `to` VARCHAR(64) DEFAULT NULL COMMENT '接收方地址',
    `value` BIGINT(20) DEFAULT 0 COMMENT '交易值（Wei）',
    `gas_price` BIGINT(20) DEFAULT NULL COMMENT 'gas价格',
    `gas_used` BIGINT(20) DEFAULT NULL COMMENT 'gas使用量',
    `input_data` TEXT DEFAULT NULL COMMENT '输入数据',
    `status` INT(1) NOT NULL DEFAULT 0 COMMENT '交易状态（0=失败，1=成功）',
    `method_name` VARCHAR(64) DEFAULT NULL COMMENT '方法名称',
    `timestamp` DATETIME NOT NULL COMMENT '交易时间戳',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_transaction_hash` (`transaction_hash`),
    KEY `idx_block_number` (`block_number`),
    KEY `idx_from` (`from`),
    KEY `idx_to` (`to`),
    KEY `idx_timestamp` (`timestamp`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易信息表';
