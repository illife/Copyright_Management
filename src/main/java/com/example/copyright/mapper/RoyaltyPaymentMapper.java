package com.example.copyright.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.copyright.entity.RoyaltyPayment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 版税记录Mapper
 */
@Mapper
public interface RoyaltyPaymentMapper extends BaseMapper<RoyaltyPayment> {

    /**
     * 根据版权ID查询版税记录
     */
    @Select("SELECT * FROM royalty_payments WHERE copyright_id = #{copyrightId}")
    List<RoyaltyPayment> selectByCopyrightId(@Param("copyrightId") Long copyrightId);

    /**
     * 根据支付方查询版税记录
     */
    @Select("SELECT * FROM royalty_payments WHERE payer_address = #{payerAddress}")
    List<RoyaltyPayment> selectByPayer(@Param("payerAddress") String payerAddress);

    /**
     * 根据收款方查询版税记录
     */
    @Select("SELECT * FROM royalty_payments WHERE payee_address = #{payeeAddress}")
    List<RoyaltyPayment> selectByPayee(@Param("payeeAddress") String payeeAddress);
}
