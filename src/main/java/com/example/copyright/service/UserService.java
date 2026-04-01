package com.example.copyright.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.copyright.dto.UserRegisterDTO;
import com.example.copyright.entity.User;
import com.example.copyright.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务
 */
@Slf4j
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FiscoContractService fiscoContractService;

    /**
     * 用户注册
     */
    @Transactional(rollbackFor = Exception.class)
    public User register(UserRegisterDTO dto) {
        // 检查用户是否已存在
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("address", dto.getAddress());
        User existingUser = userMapper.selectOne(wrapper);
        if (existingUser != null) {
            throw new RuntimeException("用户已存在");
        }

        // 先调用区块链合约注册用户
        String txHash;
        try {
            txHash = fiscoContractService.registerUser(dto.getAddress());
            log.info("User registered on blockchain, txHash: {}", txHash);
        } catch (Exception e) {
            log.error("Failed to register user on blockchain: {}", e.getMessage(), e);
            throw new RuntimeException("区块链注册失败: " + e.getMessage(), e);
        }

        // 区块链注册成功后，创建用户记录
        User user = new User();
        user.setAddress(dto.getAddress());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setRole("USER"); // 默认角色
        user.setStatus(1); // 激活状态
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());

        userMapper.insert(user);

        return user;
    }

    /**
     * 保存用户（用于注册申请审批通过后创建用户）
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveUser(User user) {
        // 检查用户是否已存在
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("address", user.getAddress());
        User existingUser = userMapper.selectOne(wrapper);
        if (existingUser != null) {
            log.warn("用户已存在，跳过创建: address={}", user.getAddress());
            return;
        }

        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());
        userMapper.insert(user);
        log.info("用户保存成功: address={}, username={}", user.getAddress(), user.getUsername());
    }

    /**
     * 根据地址查询用户
     */
    public User getByAddress(String address) {
        return userMapper.selectByAddress(address);
    }

    /**
     * 根据ID查询用户
     */
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    /**
     * 查询所有用户
     */
    public List<User> listAll() {
        return userMapper.selectList(null);
    }

    /**
     * 根据角色查询用户
     */
    public List<User> listByRole(String role) {
        return userMapper.selectByRole(role);
    }

    /**
     * 更新用户角色
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(Long userId, String role) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setRole(role);
        user.setUpdatedTime(LocalDateTime.now());
        userMapper.updateById(user);

        log.info("User role updated: userId={}, role={}", userId, role);
    }

    /**
     * 更新用户状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setStatus(status);
        user.setUpdatedTime(LocalDateTime.now());
        userMapper.updateById(user);

        log.info("User status updated: userId={}, status={}", userId, status);
    }
}
