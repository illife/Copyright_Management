package com.example.copyright.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 版权信息VO（包含完整区块链信息）
 */
@Data
public class CopyrightVO {

    // ========== 基本信息 ==========
    private Long id;
    private String contractId;           // 区块链版权ID
    private String ownerAddress;         // 所有者地址
    private String title;                // 作品标题
    private String author;               // 作者
    private String fileHash;             // 文件哈希
    private String description;          // 版权描述
    private Long registerTime;           // 注册时间戳
    private LocalDateTime createdTime;

    // ========== 区块链交易信息 ==========
    private String transactionHash;      // 交易哈希
    private String blockHash;            // 区块哈希
    private Long blockNumber;            // 区块号
    private String contractAddress;      // 合约地址
    private Integer transactionIndex;    // 交易索引
    private Long gasUsed;               // Gas使用量
    private String gasPrice;             // Gas价格
    private String transactionFee;       // 交易手续费
    private String status;               // 交易状态
    private Long blockTimestamp;         // 区块时间戳
    private String from;                 // 交易发送者
    private String to;                   // 交易接收者（合约地址）

    // ========== 浏览器链接（便捷访问） ==========
    private String transactionBrowserUrl;  // 交易浏览器URL
    private String addressBrowserUrl;      // 地址浏览器URL
    private String blockBrowserUrl;        // 区块浏览器URL
    private String ipfsGatewayUrl;         // IPFS网关URL（如果使用IPFS）

    // ========== 文件信息 ==========
    private String storageType;          // 存储类型: ipfs / local
    private String fileDownloadUrl;      // 文件下载URL（本地文件）
}
