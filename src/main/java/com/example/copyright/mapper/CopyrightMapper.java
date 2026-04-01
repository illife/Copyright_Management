package com.example.copyright.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.copyright.entity.Copyright;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 版权Mapper
 */
@Mapper
public interface CopyrightMapper extends BaseMapper<Copyright> {

    /**
     * 根据所有者查询版权
     */
    @Select("SELECT * FROM copyrights WHERE owner_address = #{ownerAddress}")
    List<Copyright> selectByOwner(@Param("ownerAddress") String ownerAddress);

    /**
     * 根据合约ID查询版权
     */
    @Select("SELECT * FROM copyrights WHERE contract_id = #{contractId}")
    Copyright selectByContractId(@Param("contractId") String contractId);

    /**
     * 根据时间范围查询
     */
    @Select("SELECT * FROM copyrights WHERE register_time BETWEEN #{startTime} AND #{endTime}")
    List<Copyright> selectByTimeRange(@Param("startTime") Long startTime, @Param("endTime") Long endTime);

    /**
     * 根据标题模糊查询
     */
    @Select("SELECT * FROM copyrights WHERE title LIKE CONCAT('%', #{keyword}, '%')")
    List<Copyright> selectByTitleKeyword(@Param("keyword") String keyword);

    /**
     * 根据关键词搜索（标题、作者、描述）
     */
    @Select("SELECT * FROM copyrights WHERE title LIKE CONCAT('%', #{keyword}, '%') OR author LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%')")
    List<Copyright> selectByKeyword(@Param("keyword") String keyword);

    /**
     * 根据作者查询
     */
    @Select("SELECT * FROM copyrights WHERE author = #{author}")
    List<Copyright> selectByAuthor(@Param("author") String author);
}
