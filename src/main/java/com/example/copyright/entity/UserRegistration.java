package com.example.copyright.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户注册申请实体
 */
@Data
@TableName("user_registration")
public class UserRegistration {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户区块链地址
     */
    private String userAddress;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 联系邮箱
     */
    private String email;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 申请说明
     */
    private String description;

    /**
     * 审核状态: PENDING-待审核, APPROVED-已通过, REJECTED-已拒绝
     */
    private String status;

    /**
     * 审核人
     */
    private String reviewer;

    /**
     * 审核意见
     */
    private String reviewComment;

    /**
     * 区块链交易哈希（审核通过后注册到链上）
     */
    private String txHash;

    /**
     * 申请时间
     */
    private LocalDateTime createTime;

    /**
     * 审核时间
     */
    private LocalDateTime reviewTime;
}
