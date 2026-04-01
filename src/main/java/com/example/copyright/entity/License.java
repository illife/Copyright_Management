package com.example.copyright.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 授权实体类
 */
@Data
@TableName("licenses")
public class License {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 合约中的授权ID
     */
    private String contractId;

    /**
     * 版权ID
     */
    private Long copyrightId;

    /**
     * 申请人地址
     */
    private String applicantAddress;

    /**
     * 被授权方地址
     */
    private String licenseeAddress;

    /**
     * 开始时间戳
     */
    private Long startTime;

    /**
     * 结束时间戳
     */
    private Long endTime;

    /**
     * 是否独占授权
     */
    private Boolean isExclusive;

    /**
     * 授权状态：NONE, PENDING, APPROVED, ACTIVE, REVOKED
     */
    private String status;

    /**
     * 交易哈希
     */
    private String transactionHash;

    /**
     * 区块哈希
     */
    private String blockHash;

    /**
     * 合约地址
     */
    private String contractAddress;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
}
