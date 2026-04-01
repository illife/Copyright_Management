package com.example.copyright.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 区块信息VO
 */
@Data
public class BlockVO {

    /**
     * 区块号
     */
    private BigInteger blockNumber;

    /**
     * 区块哈希
     */
    private String blockHash;

    /**
     * 父区块哈希
     */
    private String parentHash;

    /**
     * 区块时间戳 (内部使用LocalDateTime)
     */
    @JsonIgnore
    private LocalDateTime timestamp;

    /**
     * 区块时间戳 (JSON序列化为Unix秒)
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
     * 交易数量
     */
    private Integer transactionCount;

    /**
     * 区块生成者
     */
    private String sealer;

    /**
     * gas使用量
     */
    private BigInteger gasUsed;

    /**
     * gas限制
     */
    private BigInteger gasLimit;

    /**
     * gas使用率（百分比）
     */
    private String gasUsageRate;

    /**
     * 区块大小（字节）
     */
    private BigInteger blockSize;

    /**
     * 区块大小（KB格式化）
     */
    private String blockSizeFormatted;

    /**
     * 状态（正常/孤块）
     */
    private String status;

    public String getGasUsageRate() {
        if (gasUsed != null && gasLimit != null && gasLimit.compareTo(BigInteger.ZERO) > 0) {
            return gasUsed.multiply(BigInteger.valueOf(100)).divide(gasLimit) + "%";
        }
        return "0%";
    }

    public String getBlockSizeFormatted() {
        if (blockSize != null) {
            long sizeInBytes = blockSize.longValue();
            if (sizeInBytes < 1024) {
                return sizeInBytes + " B";
            } else if (sizeInBytes < 1024 * 1024) {
                return String.format("%.2f KB", sizeInBytes / 1024.0);
            } else {
                return String.format("%.2f MB", sizeInBytes / (1024.0 * 1024.0));
            }
        }
        return "0 B";
    }
}
