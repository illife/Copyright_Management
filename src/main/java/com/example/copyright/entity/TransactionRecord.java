package com.example.copyright.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 交易记录实体类
 */
@Data
@TableName("transactions")
public class TransactionRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 交易哈希
     */
    private String transactionHash;

    /**
     * 合约地址
     */
    private String contractAddress;

    /**
     * 函数名称
     */
    private String functionName;

    /**
     * 发送方地址
     */
    private String fromAddress;

    /**
     * 接收方地址
     */
    private String toAddress;

    /**
     * 消耗Gas
     */
    private Long gasUsed;

    /**
     * Gas价格
     */
    private Long gasPrice;

    /**
     * 交易状态：1-成功，0-失败
     */
    private Integer status;

    /**
     * 区块号
     */
    private Long blockNumber;

    /**
     * 区块时间戳
     */
    private Long blockTimestamp;

    /**
     * 输入数据
     */
    private String inputData;

    /**
     * 输出数据
     */
    private String outputData;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
}
