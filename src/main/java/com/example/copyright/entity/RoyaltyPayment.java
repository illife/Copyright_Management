package com.example.copyright.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 版税记录实体类
 */
@Data
@TableName("royalty_payments")
public class RoyaltyPayment {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 版权ID
     */
    private Long copyrightId;

    /**
     * 支付方地址
     */
    private String payerAddress;

    /**
     * 收款方地址
     */
    private String payeeAddress;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 货币单位
     */
    private String currency;

    /**
     * 交易哈希
     */
    private String transactionHash;

    /**
     * 区块时间戳
     */
    private Long blockTimestamp;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
}
