package com.example.copyright.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.copyright.entity.License;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 授权Mapper
 */
@Mapper
public interface LicenseMapper extends BaseMapper<License> {

    /**
     * 根据版权ID查询授权
     */
    @Select("SELECT * FROM licenses WHERE copyright_id = #{copyrightId}")
    List<License> selectByCopyrightId(@Param("copyrightId") Long copyrightId);

    /**
     * 根据合约ID查询授权
     */
    @Select("SELECT * FROM licenses WHERE contract_id = #{contractId}")
    License selectByContractId(@Param("contractId") String contractId);

    /**
     * 根据被授权方查询授权
     */
    @Select("SELECT * FROM licenses WHERE licensee_address = #{licenseeAddress}")
    List<License> selectByLicensee(@Param("licenseeAddress") String licenseeAddress);

    /**
     * 根据被授权方和状态查询
     */
    @Select("SELECT * FROM licenses WHERE licensee_address = #{licenseeAddress} AND status = #{status}")
    List<License> selectByLicenseeAndStatus(@Param("licenseeAddress") String licenseeAddress, @Param("status") String status);

    /**
     * 查询有效授权
     */
    @Select("SELECT * FROM licenses WHERE copyright_id = #{copyrightId} AND licensee_address = #{licenseeAddress} AND status = 'ACTIVE' AND start_time <= #{currentTime} AND end_time >= #{currentTime}")
    List<License> selectActiveLicenses(@Param("copyrightId") Long copyrightId, @Param("licenseeAddress") String licenseeAddress, @Param("currentTime") Long currentTime);

    /**
     * 查询待审批的授权
     */
    @Select("SELECT * FROM licenses WHERE status = 'PENDING'")
    List<License> selectPendingLicenses();
}
