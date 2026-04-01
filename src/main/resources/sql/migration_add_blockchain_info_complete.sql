-- 增强版权表，添加完整的区块链交易信息字段
-- 执行前请备份数据库！
-- 基于当前表结构增强，字段添加顺序很重要

-- 第一步：添加 block_number (在 contract_address 之后)
ALTER TABLE `copyrights`
ADD COLUMN `block_number` BIGINT(20) DEFAULT NULL COMMENT '区块号' AFTER `contract_address`;

-- 第二步：添加 transaction_index (在 block_number 之后)
ALTER TABLE `copyrights`
ADD COLUMN `transaction_index` INT(11) DEFAULT NULL COMMENT '交易索引(在区块中的位置)' AFTER `block_number`;

-- 第三步：添加 gas_used (在 transaction_index 之后)
ALTER TABLE `copyrights`
ADD COLUMN `gas_used` BIGINT(20) DEFAULT NULL COMMENT 'Gas使用量' AFTER `transaction_index`;

-- 第四步：添加 gas_price (在 gas_used 之后)
ALTER TABLE `copyrights`
ADD COLUMN `gas_price` VARCHAR(100) DEFAULT NULL COMMENT 'Gas价格(Wei)' AFTER `gas_used`;

-- 添加缺失的索引以优化查询
-- idx_transaction_hash 已存在，跳过
ALTER TABLE `copyrights`
ADD INDEX `idx_block_number` (`block_number`),
ADD INDEX `idx_contract_address` (`contract_address`);

-- 查看表结构验证
DESCRIBE `copyrights`;
