package com.example.copyright.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.copyright.entity.TransactionInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigInteger;
import java.util.List;

/**
 * 交易信息Mapper
 */
@Mapper
public interface TransactionInfoMapper extends BaseMapper<TransactionInfo> {

    /**
     * 根据交易哈希查询
     */
    @Select("SELECT * FROM transaction_info WHERE transaction_hash = #{transactionHash}")
    TransactionInfo selectByTransactionHash(@Param("transactionHash") String transactionHash);

    /**
     * 根据区块号查询交易
     */
    @Select("SELECT * FROM transaction_info WHERE block_number = #{blockNumber} ORDER BY transaction_index")
    List<TransactionInfo> selectByBlockNumber(@Param("blockNumber") BigInteger blockNumber);

    /**
     * 根据发送方地址查询交易
     */
    @Select("SELECT * FROM transaction_info WHERE `from` = #{address} ORDER BY block_number DESC LIMIT #{limit}")
    List<TransactionInfo> selectByFromAddress(@Param("address") String address, @Param("limit") int limit);

    /**
     * 根据接收方地址查询交易
     */
    @Select("SELECT * FROM transaction_info WHERE `to` = #{address} ORDER BY block_number DESC LIMIT #{limit}")
    List<TransactionInfo> selectByToAddress(@Param("address") String address, @Param("limit") int limit);

    /**
     * 查询最新的N笔交易
     */
    @Select("SELECT * FROM transaction_info ORDER BY block_number DESC, transaction_index DESC LIMIT #{limit}")
    List<TransactionInfo> selectLatestTransactions(@Param("limit") int limit);

    /**
     * 统计总交易数
     */
    @Select("SELECT COUNT(*) FROM transaction_info")
    Long countTotalTransactions();
}
