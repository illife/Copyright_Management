package com.example.copyright.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 区块链交易信息
 * 用于封装从交易回执中提取的完整区块链信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockchainInfo {

    /**
     * 交易哈希 (0x开头的66位十六进制)
     */
    private String transactionHash;

    /**
     * 区块哈希 (0x开头的66位十六进制)
     */
    private String blockHash;

    /**
     * 区块号 (区块链高度)
     */
    private Long blockNumber;

    /**
     * 合约地址
     */
    private String contractAddress;

    /**
     * 交易索引 (在区块中的位置)
     */
    private Integer transactionIndex;

    /**
     * Gas使用量
     */
    private Long gasUsed;

    /**
     * Gas价格 (Wei)
     */
    private String gasPrice;

    /**
     * 实际支付的手续费 (Gas Used * Gas Price)
     */
    private String transactionFee;

    /**
     * 交易状态 (Success/Failed)
     */
    private String status;

    /**
     * 交易时间戳 (从区块时间转换)
     */
    private Long blockTimestamp;

    /**
     * 从地址 (交易发送者)
     */
    private String from;

    /**
     * 到地址 (合约地址)
     */
    private String to;

    /**
     * 输入数据 (方法调用参数编码)
     */
    private String input;

    /**
     * 合约调用方法名
     */
    private String methodName;
}
