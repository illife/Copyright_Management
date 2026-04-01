package com.example.copyright.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.copyright.entity.TransactionRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 交易记录Mapper
 */
@Mapper
public interface TransactionRecordMapper extends BaseMapper<TransactionRecord> {

    /**
     * 统计交易状态数量
     */
    @Select("SELECT " +
            "COUNT(*) as total, " +
            "SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) as success, " +
            "SUM(CASE WHEN status = 2 THEN 1 ELSE 0 END) as pending, " +
            "SUM(CASE WHEN status = 0 THEN 1 ELSE 0 END) as failed " +
            "FROM transactions")
    Map<String, Object> getTransactionStats();

    /**
     * 根据类型查询交易（通过functionName映射）
     */
    @Select("SELECT * FROM transactions WHERE functionName LIKE CONCAT('%', #{type}, '%') " +
            "ORDER BY blockNumber DESC LIMIT #{limit}")
    List<TransactionRecord> getTransactionsByType(@Param("type") String type, @Param("limit") int limit);

    /**
     * 根据状态查询交易
     */
    @Select("SELECT * FROM transactions WHERE status = #{status} " +
            "ORDER BY blockNumber DESC LIMIT #{limit}")
    List<TransactionRecord> getTransactionsByStatus(@Param("status") int status, @Param("limit") int limit);
}
