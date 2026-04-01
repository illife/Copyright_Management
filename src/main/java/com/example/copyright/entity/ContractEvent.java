package com.example.copyright.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 合约事件记录实体类
 */
@Data
@TableName("contract_events")
public class ContractEvent {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 事件名称
     */
    private String eventName;

    /**
     * 合约地址
     */
    private String contractAddress;

    /**
     * 区块号
     */
    private Long blockNumber;

    /**
     * 区块时间戳
     */
    private Long blockTimestamp;

    /**
     * 交易哈希
     */
    private String transactionHash;

    /**
     * 日志索引
     */
    private Integer logIndex;

    /**
     * 事件主题
     */
    private String topics;

    /**
     * 事件数据
     */
    private String data;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
}
