package com.example.copyright.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.copyright.entity.AuditLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 审计日志Mapper
 */
@Mapper
public interface AuditLogMapper extends BaseMapper<AuditLog> {

    /**
     * 根据用户地址查询审计日志
     */
    @Select("SELECT * FROM audit_logs WHERE user_address = #{userAddress} ORDER BY created_time DESC LIMIT #{limit}")
    List<AuditLog> selectByUserAddress(@Param("userAddress") String userAddress, @Param("limit") int limit);

    /**
     * 根据操作类型查询审计日志
     */
    @Select("SELECT * FROM audit_logs WHERE operation_type = #{operationType} ORDER BY created_time DESC")
    List<AuditLog> selectByOperationType(@Param("operationType") String operationType);

    /**
     * 根据资源类型和ID查询审计日志
     */
    @Select("SELECT * FROM audit_logs WHERE resource_type = #{resourceType} AND resource_id = #{resourceId} ORDER BY created_time DESC")
    List<AuditLog> selectByResource(@Param("resourceType") String resourceType, @Param("resourceId") String resourceId);
}
