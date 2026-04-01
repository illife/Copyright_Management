-- 增强版权表，添加完整的区块链交易信息字段
-- 执行前请备份数据库！

-- 检查字段是否已存在，不存在则添加
ALTER TABLE `copyrights`
ADD COLUMN IF NOT EXISTS `transaction_index` INT(11) DEFAULT NULL COMMENT '交易索引(在区块中的位置)' AFTER `block_number`,
ADD COLUMN IF NOT EXISTS `gas_used` BIGINT(20) DEFAULT NULL COMMENT 'Gas使用量' AFTER `transaction_index`,
ADD COLUMN IF NOT EXISTS `gas_price` VARCHAR(100) DEFAULT NULL COMMENT 'Gas价格(Wei)' AFTER `gas_used`;

-- 添加索引以优化查询
ALTER TABLE `copyrights`
ADD INDEX IF NOT EXISTS `idx_transaction_hash` (`transaction_hash`),
ADD INDEX IF NOT EXISTS `idx_block_number` (`block_number`),
ADD INDEX IF NOT EXISTS `idx_contract_address` (`contract_address`);

-- 查看表结构
DESCRIBE `copyrights`;
