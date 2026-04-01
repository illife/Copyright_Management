package com.example.copyright.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.copyright.entity.BlockInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigInteger;
import java.util.List;

/**
 * 区块信息Mapper
 */
@Mapper
public interface BlockInfoMapper extends BaseMapper<BlockInfo> {

    /**
     * 根据区块号查询
     */
    @Select("SELECT * FROM block_info WHERE block_number = #{blockNumber}")
    BlockInfo selectByBlockNumber(@Param("blockNumber") BigInteger blockNumber);

    /**
     * 根据区块哈希查询
     */
    @Select("SELECT * FROM block_info WHERE block_hash = #{blockHash}")
    BlockInfo selectByBlockHash(@Param("blockHash") String blockHash);

    /**
     * 查询最新的N个区块
     */
    @Select("SELECT * FROM block_info ORDER BY block_number DESC LIMIT #{limit}")
    List<BlockInfo> selectLatestBlocks(@Param("limit") int limit);

    /**
     * 查询区块号范围内的区块
     */
    @Select("SELECT * FROM block_info WHERE block_number BETWEEN #{start} AND #{end} ORDER BY block_number DESC")
    List<BlockInfo> selectByRange(@Param("start") BigInteger start, @Param("end") BigInteger end);

    /**
     * 统计总区块数
     */
    @Select("SELECT COUNT(*) FROM block_info")
    Long countTotalBlocks();
}
