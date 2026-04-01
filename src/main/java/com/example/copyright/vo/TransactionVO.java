package com.example.copyright.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 交易信息VO
 */
@Data
public class TransactionVO {

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
     * 交易索引
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
     * 是否合约创建
     */
    private Boolean contractCreation;

    /**
     * 交易值（Wei）
     */
    private BigInteger value;

    /**
     * 交易值（格式化）
     */
    private String valueFormatted;

    /**
     * gas价格
     */
    private BigInteger gasPrice;

    /**
     * gas价格（Gwei格式化）
     */
    private String gasPriceFormatted;

    /**
     * gas使用量
     */
    private BigInteger gasUsed;

    /**
     * 实际花费（gasPrice * gasUsed）
     */
    private String actualCost;

    /**
     * 输入数据
     */
    private String inputData;

    /**
     * 输入数据（截断显示）
     */
    private String inputDataShort;

    /**
     * 交易状态（0=失败，1=成功）
     */
    private Integer status;

    /**
     * 交易状态描述
     */
    private String statusDescription;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 交易时间戳 (内部使用LocalDateTime)
     */
    @JsonIgnore
    private LocalDateTime timestamp;

    /**
     * 交易时间戳 (JSON序列化为Unix秒)
     */
    @JsonProperty("timestamp")
    public Long getTimestampAsLong() {
        if (timestamp != null) {
            return timestamp.atZone(ZoneId.systemDefault()).toEpochSecond();
        }
        return null;
    }

    /**
     * 设置时间戳（从Unix秒）
     */
    public void setTimestampFromLong(Long timestampSeconds) {
        if (timestampSeconds != null) {
            this.timestamp = LocalDateTime.ofEpochSecond(
                timestampSeconds,
                0,
                ZoneId.systemDefault().getRules().getOffset(java.time.Instant.EPOCH)
            );
        }
    }

    /**
     * 确认数
     */
    private BigInteger confirmations;

    public String getValueFormatted() {
        if (value == null || value.equals(BigInteger.ZERO)) {
            return "0";
        }
        // 转换为Ether（假设18位小数）
        return value.toString() + " Wei";
    }

    public String getGasPriceFormatted() {
        if (gasPrice != null) {
            // 转换为Gwei（1 Gwei = 10^9 Wei）
            return gasPrice.divide(BigInteger.valueOf(1_000_000_000)) + " Gwei";
        }
        return "0 Gwei";
    }

    public String getActualCost() {
        if (gasPrice != null && gasUsed != null) {
            BigInteger cost = gasPrice.multiply(gasUsed);
            return cost.toString() + " Wei";
        }
        return "0 Wei";
    }

    public String getInputDataShort() {
        if (inputData != null && inputData.length() > 50) {
            return inputData.substring(0, 50) + "...";
        }
        return inputData;
    }

    public String getStatusDescription() {
        if (status == null) {
            return "未知";
        }
        return status == 1 ? "成功" : "失败";
    }

    public Boolean getContractCreation() {
        // 如果to地址为空或为0x0，则是合约创建交易
        return to == null || to.isEmpty() || "0x0000000000000000000000000000000000000000".equals(to);
    }
}
