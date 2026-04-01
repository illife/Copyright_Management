package com.example.copyright.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.copyright.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据地址查询用户
     */
    @Select("SELECT * FROM users WHERE address = #{address}")
    User selectByAddress(@Param("address") String address);

    /**
     * 根据角色查询用户列表
     */
    @Select("SELECT * FROM users WHERE role = #{role}")
    List<User> selectByRole(@Param("role") String role);

    /**
     * 检查用户是否已注册
     */
    @Select("SELECT COUNT(*) FROM users WHERE address = #{address}")
    int countByAddress(@Param("address") String address);
}
