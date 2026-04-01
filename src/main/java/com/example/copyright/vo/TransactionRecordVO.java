package com.example.copyright.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 交易记录VO - 返回给前端
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRecordVO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 交易哈希
     */
    private String transactionHash;

    /**
     * 交易类型（从functionName转换）
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
     * 交易类型显示文本
     */
    private String typeText;

    /**
     * 区块号
     */
    private Long blockNumber;

    /**
     * 发送方地址
     */
    private String fromAddress;

    /**
     * 接收方地址（合约地址）
     */
    private String toAddress;

    /**
     * 合约地址
     */
    private String contractAddress;

    /**
     * 交易状态：SUCCESS-成功, PENDING-待确认, FAILED-失败
     */
    private String status;

    /**
     * 交易时间戳（秒）
     */
    private Long timestamp;

    /**
     * 格式化的时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String formattedTime;

    /**
     * Gas使用量
     */
    private Long gasUsed;

    /**
     * 关联的版权ID（如果适用）
     */
    private Long copyrightId;

    /**
     * 关联的授权ID（如果适用）
     */
    private Long licenseId;

    /**
     * 交易描述
     */
    private String description;

    /**
     * 浏览器链接
     */
    private String browserUrl;

    /**
     * 扩展信息
     */
    private Map<String, Object> extra;
}
