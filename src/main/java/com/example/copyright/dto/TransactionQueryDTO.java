package com.example.copyright.dto;

import lombok.Data;

/**
 * 交易查询DTO
 */
@Data
public class TransactionQueryDTO {

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;

    /**
     * 交易哈希（精确查询）
     */
    private String transactionHash;

    /**
     * 区块号
     */
    private Long blockNumber;

    /**
     * 发送方地址
     */
    private String fromAddress;

    /**
     * 接收方地址
     */
    private String toAddress;

    /**
     * 交易状态（0=失败，1=成功）
     */
    private Integer status;
}
