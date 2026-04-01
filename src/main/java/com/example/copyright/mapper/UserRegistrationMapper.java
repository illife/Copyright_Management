package com.example.copyright.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.copyright.entity.UserRegistration;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户注册申请 Mapper
 */
@Mapper
public interface UserRegistrationMapper extends BaseMapper<UserRegistration> {
}
