package com.example.copyright.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.copyright.entity.User;
import com.example.copyright.entity.UserRegistration;
import com.example.copyright.mapper.UserRegistrationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 用户注册申请服务
 */
@Slf4j
@Service
public class UserRegistrationService {

    @Autowired
    private UserRegistrationMapper userRegistrationMapper;

    @Autowired
    private FiscoContractService fiscoContractService;

    @Autowired
    private UserService userService;

    /**
     * 提交注册申请
     */
    @Transactional
    public UserRegistration submitRegistration(UserRegistration registration) {
        // 检查是否已有待审核或已通过的申请
        QueryWrapper<UserRegistration> wrapper = new QueryWrapper<>();
        wrapper.eq("user_address", registration.getUserAddress())
                .in("status", Arrays.asList("PENDING", "APPROVED"));

        UserRegistration existing = userRegistrationMapper.selectOne(wrapper);
        if (existing != null) {
            if ("PENDING".equals(existing.getStatus())) {
                throw new RuntimeException("您已有待审核的申请，请勿重复提交");
            } else if ("APPROVED".equals(existing.getStatus())) {
                throw new RuntimeException("您已完成注册，无需再次申请");
            }
        }

        // 设置初始状态
        registration.setStatus("PENDING");
        registration.setCreateTime(LocalDateTime.now());

        userRegistrationMapper.insert(registration);
        log.info("用户注册申请提交成功: userAddress={}, username={}",
                registration.getUserAddress(), registration.getUsername());

        return registration;
    }

    /**
     * 审批通过 - 注册到区块链并创建用户记录
     */
    @Transactional
    public UserRegistration approveRegistration(Long id, String reviewer, String comment) {
        UserRegistration registration = userRegistrationMapper.selectById(id);
        if (registration == null) {
            throw new RuntimeException("注册申请不存在");
        }

        if (!"PENDING".equals(registration.getStatus())) {
            throw new RuntimeException("该申请已被处理，状态: " + registration.getStatus());
        }

        try {
            // 1. 调用智能合约注册用户到区块链
            // 新合约版本：只需要userAddress参数，不需要username
            String txHash = fiscoContractService.registerUser(
                    registration.getUserAddress()
            );
            log.info("用户注册到区块链成功: userAddress={}, txHash={}",
                    registration.getUserAddress(), txHash);

            // 2. 创建用户记录到数据库
            User user = new User();
            user.setAddress(registration.getUserAddress());
            user.setUsername(registration.getUsername());
            user.setEmail(registration.getEmail());
            user.setPhone(registration.getPhone());
            user.setRole("USER"); // 默认角色为普通用户
            user.setStatus(1); // 激活状态
            user.setCreatedTime(LocalDateTime.now());
            user.setUpdatedTime(LocalDateTime.now());

            userService.saveUser(user);
            log.info("用户记录创建成功: address={}, username={}",
                    user.getAddress(), user.getUsername());

            // 3. 更新申请状态
            registration.setStatus("APPROVED");
            registration.setReviewer(reviewer);
            registration.setReviewComment(comment);
            registration.setTxHash(txHash);
            registration.setReviewTime(LocalDateTime.now());

            userRegistrationMapper.updateById(registration);
            log.info("用户注册申请审批完成: id={}, userAddress={}", id, registration.getUserAddress());

            return registration;
        } catch (Exception e) {
            log.error("注册用户失败: {}", e.getMessage(), e);
            throw new RuntimeException("注册用户失败: " + e.getMessage(), e);
        }
    }

    /**
     * 审批拒绝
     */
    @Transactional
    public UserRegistration rejectRegistration(Long id, String reviewer, String comment) {
        UserRegistration registration = userRegistrationMapper.selectById(id);
        if (registration == null) {
            throw new RuntimeException("注册申请不存在");
        }

        if (!"PENDING".equals(registration.getStatus())) {
            throw new RuntimeException("该申请已被处理，状态: " + registration.getStatus());
        }

        registration.setStatus("REJECTED");
        registration.setReviewer(reviewer);
        registration.setReviewComment(comment);
        registration.setReviewTime(LocalDateTime.now());

        userRegistrationMapper.updateById(registration);
        log.info("用户注册申请被拒绝: id={}, userAddress={}", id, registration.getUserAddress());

        return registration;
    }

    /**
     * 查询申请详情
     */
    public UserRegistration getRegistrationById(Long id) {
        return userRegistrationMapper.selectById(id);
    }

    /**
     * 根据用户地址查询申请
     */
    public UserRegistration getRegistrationByUserAddress(String userAddress) {
        QueryWrapper<UserRegistration> wrapper = new QueryWrapper<>();
        wrapper.eq("user_address", userAddress)
                .orderByDesc("create_time")
                .last("LIMIT 1");
        return userRegistrationMapper.selectOne(wrapper);
    }

    /**
     * 分页查询注册申请列表
     */
    public IPage<UserRegistration> getRegistrationList(int page, int size, String status) {
        Page<UserRegistration> pageParam = new Page<>(page, size);
        QueryWrapper<UserRegistration> wrapper = new QueryWrapper<>();

        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }

        wrapper.orderByDesc("create_time");
        return userRegistrationMapper.selectPage(pageParam, wrapper);
    }

    /**
     * 获取待审核申请数量
     */
    public long getPendingCount() {
        QueryWrapper<UserRegistration> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "PENDING");
        return userRegistrationMapper.selectCount(wrapper);
    }
}
