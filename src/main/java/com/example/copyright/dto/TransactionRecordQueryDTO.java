package com.example.copyright.dto;

import lombok.Data;

/**
 * 交易记录查询DTO
 */
@Data
public class TransactionRecordQueryDTO {

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 12;

    /**
     * 交易类型
     * COPYRIGHT_REGISTER - 版权注册
     * LICENSE_APPLY - 授权申请
     * LICENSE_APPROVE - 授权批准
     * LICENSE_ACTIVATE - 授权激活
     * LICENSE_REVOKE - 授权撤销
     * ROYALTY_PAYMENT - 版税支付
     * USER_REGISTER - 用户注册
     */
    private String type;

    /**
     * 交易状态：SUCCESS, PENDING, FAILED
     */
    private String status;

    /**
     * 发送方地址
     */
    private String fromAddress;

    /**
     * 接收方地址
     */
    private String toAddress;

    /**
     * 区块号
     */
    private Long blockNumber;

    /**
     * 交易哈希（模糊查询）
     */
    private String transactionHash;
}
