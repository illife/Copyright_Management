package com.example.copyright.vo;

import lombok.Data;

import java.math.BigInteger;

/**
 * 区块链统计信息VO
 */
@Data
public class ChainStatsVO {

    /**
     * 最新区块号
     */
    private BigInteger latestBlockNumber;

    /**
     * 最新区块哈希
     */
    private String latestBlockHash;

    /**
     * 总交易数
     */
    private BigInteger totalTransactions;

    /**
     * 平均区块时间（秒）
     */
    private Long averageBlockTime;

    /**
     * 节点数量
     */
    private Integer nodeCount;

    /**
     * 群组ID
     */
    private Integer groupId;

    /**
     * 链ID
     */
    private Integer chainId;

    /**
     * 共识算法
     */
    private String consensusType;

    /**
     * 系统版本
     */
    private String systemVersion;

    /**
     * PBFT视图
     */
    private Long pbftView;

    /**
     * 是否正在同步
     */
    private Boolean syncing;

    /**
     * 同步状态（如果正在同步）
     */
    private SyncStatus syncStatus;

    @Data
    public static class SyncStatus {
        private BigInteger currentBlock;
        private BigInteger highestBlock;
        private String startingBlock;
    }
}
