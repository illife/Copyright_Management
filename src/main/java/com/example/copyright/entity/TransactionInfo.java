package com.example.copyright.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 交易信息实体类
 * 用于区块链浏览器展示交易信息
 */
@Data
@TableName("transaction_info")
public class TransactionInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 交易哈希
     */
    private String transactionHash;

    /**
     * 区块号
     */
    private BigInteger blockNumber;

    /**
     * 区块哈希
     */
    private String blockHash;

    /**
     * 交易索引（在区块中的位置）
     */
    private Integer transactionIndex;

    /**
     * 发送方地址
     */
    private String from;

    /**
     * 接收方地址
     */
    private String to;

    /**
     * 交易值（金额）
     */
    private BigInteger value;

    /**
     * gas价格
     */
    private BigInteger gasPrice;

    /**
     * gas使用量
     */
    private BigInteger gasUsed;

    /**
     * 输入数据
     */
    private String inputData;

    /**
     * 交易状态（0=失败，1=成功）
     */
    private Integer status;

    /**
     * 交易时间戳
     */
    private LocalDateTime timestamp;

    /**
     * 方法名称（解析的函数调用）
     */
    private String methodName;

    /**
     * 创建时间（数据库记录时间）
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
