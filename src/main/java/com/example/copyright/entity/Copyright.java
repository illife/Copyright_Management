package com.example.copyright.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 版权实体类
 */
@Data
@TableName("copyrights")
public class Copyright {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 合约中的版权ID
     */
    private String contractId;

    /**
     * 版权所有者地址
     */
    private String ownerAddress;

    /**
     * 版权标题
     */
    private String title;

    /**
     * 作者
     */
    private String author;

    /**
     * 文件哈希(IPFS)
     */
    private String fileHash;

    /**
     * 版权描述
     */
    private String description;

    /**
     * 注册时间戳
     */
    private Long registerTime;

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
     * 合约地址 (CopyrightRegistry合约地址，从配置读取)
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
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
}
