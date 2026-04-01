package com.example.copyright.dto;

import lombok.Data;

/**
 * 区块查询DTO
 */
@Data
public class BlockQueryDTO {

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;

    /**
     * 区块号（精确查询）
     */
    private Long blockNumber;

    /**
     * 区块哈希（精确查询）
     */
    private String blockHash;

    /**
     * 开始区块号
     */
    private Long startBlockNumber;

    /**
     * 结束区块号
     */
    private Long endBlockNumber;
}
