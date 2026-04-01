package com.example.copyright.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 区块信息实体类
 * 用于区块链浏览器展示区块信息
 */
@Data
@TableName("block_info")
public class BlockInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 区块号
     */
    private BigInteger blockNumber;

    /**
     * 区块哈希
     */
    private String blockHash;

    /**
     * 父区块哈希
     */
    private String parentHash;

    /**
     * 区块时间戳
     */
    private LocalDateTime timestamp;

    /**
     * 交易数量
     */
    private Integer transactionCount;

    /**
     * 区块生成者
     */
    private String sealer;

    /**
     * gas使用量
     */
    private BigInteger gasUsed;

    /**
     * gas限制
     */
    private BigInteger gasLimit;

    /**
     * 区块大小（字节）
     */
    private BigInteger blockSize;

    /**
     * 创建时间（数据库记录时间）
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
