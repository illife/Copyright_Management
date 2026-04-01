package com.example.copyright.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.copyright.dto.LicenseApplyDTO;
import com.example.copyright.entity.Copyright;
import com.example.copyright.entity.License;
import com.example.copyright.mapper.CopyrightMapper;
import com.example.copyright.mapper.LicenseMapper;
import com.example.copyright.vo.LicenseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 授权服务
 */
@Slf4j
@Service
public class LicenseService {

    @Autowired
    private LicenseMapper licenseMapper;

    @Autowired
    private CopyrightMapper copyrightMapper;

    @Autowired
    private FiscoContractService fiscoContractService;

    /**
     * 申请授权
     */
    @Transactional(rollbackFor = Exception.class)
    public License apply(LicenseApplyDTO dto) {
        // 验证版权存在
        QueryWrapper<Copyright> wrapper = new QueryWrapper<>();
        wrapper.eq("contract_id", dto.getCopyrightId());
        Copyright copyright = copyrightMapper.selectOne(wrapper);
        if (copyright == null) {
            throw new RuntimeException("版权不存在");
        }

        // 验证时间范围（后端验证，不再依赖合约的时间检查）
        long copyrightRegisterTimeMs = copyright.getRegisterTime();
        long currentTimeMs = System.currentTimeMillis();

        // 确定开始时间（如果未指定，使用当前时间）
        long startTimeMs = dto.getStartTime() != null ? dto.getStartTime() : currentTimeMs;
        long endTimeMs = dto.getEndTime();

        log.info("时间验证 - 版权注册时间: {}, 当前时间: {}, startTime: {}, endTime: {}",
            copyrightRegisterTimeMs, currentTimeMs, startTimeMs, endTimeMs);

        // 后端验证：开始时间不能早于版权注册时间
        if (startTimeMs < copyrightRegisterTimeMs) {
            throw new RuntimeException("开始时间不能早于版权注册时间");
        }

        // 后端验证：时间范围有效性
        if (startTimeMs >= endTimeMs) {
            throw new RuntimeException("时间范围无效：开始时间必须早于结束时间");
        }

        // 后端验证：结束时间必须在未来（至少1分钟后）
        if (endTimeMs < currentTimeMs + 60000) {
            throw new RuntimeException("结束时间必须至少在1分钟后");
        }

        // 后端验证：授权期限不能超过10年
        long maxDurationMs = 365L * 10 * 24 * 60 * 60 * 1000; // 10年
        if (endTimeMs - startTimeMs > maxDurationMs) {
            throw new RuntimeException("授权期限不能超过10年");
        }

        // 将毫秒转换为秒传递给合约
        long startTimeSeconds = startTimeMs / 1000;
        long endTimeSeconds = endTimeMs / 1000;

        log.info("传递给合约的时间戳（秒） - startTime: {}, endTime: {}", startTimeSeconds, endTimeSeconds);

        // 调用区块链合约申请授权（传递秒级时间戳）
        String contractId;
        try {
            contractId = fiscoContractService.applyForLicense(
                new BigInteger(dto.getCopyrightId()),
                dto.getLicensee(),
                BigInteger.valueOf(startTimeSeconds),
                BigInteger.valueOf(endTimeSeconds),
                dto.getIsExclusive()
            );
        } catch (Exception e) {
            log.error("Failed to apply license on blockchain: {}", e.getMessage(), e);
            throw new RuntimeException("区块链申请失败: " + e.getMessage(), e);
        }

        // 保存授权记录到数据库（使用秒级时间戳）
        License license = new License();
        license.setContractId(contractId);
        license.setCopyrightId(copyright.getId());
        license.setApplicantAddress(dto.getLicensee()); // 简化处理，申请人和被授权人为同一人
        license.setLicenseeAddress(dto.getLicensee());
        license.setStartTime(startTimeSeconds);  // 保存秒级时间戳
        license.setEndTime(endTimeSeconds);      // 保存秒级时间戳
        license.setIsExclusive(dto.getIsExclusive());
        license.setStatus("PENDING");

        licenseMapper.insert(license);
        log.info("License applied: contractId={}", contractId);

        return license;
    }

    /**
     * 批准授权
     */
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long licenseId) {
        License license = licenseMapper.selectById(licenseId);
        if (license == null) {
            throw new RuntimeException("授权不存在");
        }

        if (!"PENDING".equals(license.getStatus())) {
            throw new RuntimeException("授权状态不是待审批");
        }

        // 调用区块链合约批准授权
        try {
            String txHash = fiscoContractService.approveLicense(new BigInteger(license.getContractId()));
            log.info("License approved on blockchain: txHash={}", txHash);
        } catch (Exception e) {
            log.error("Failed to approve license on blockchain: {}", e.getMessage(), e);
            throw new RuntimeException("区块链批准失败: " + e.getMessage(), e);
        }

        // 更新数据库状态
        license.setStatus("APPROVED");
        licenseMapper.updateById(license);

        log.info("License approved: licenseId={}", licenseId);
    }

    /**
     * 拒绝授权
     */
    @Transactional(rollbackFor = Exception.class)
    public void reject(Long licenseId) {
        License license = licenseMapper.selectById(licenseId);
        if (license == null) {
            throw new RuntimeException("授权不存在");
        }

        if (!"PENDING".equals(license.getStatus())) {
            throw new RuntimeException("授权状态不是待审批");
        }

        // 调用区块链合约拒绝授权
        try {
            String txHash = fiscoContractService.rejectLicense(new BigInteger(license.getContractId()));
            log.info("License rejected on blockchain: txHash={}", txHash);
        } catch (Exception e) {
            log.error("Failed to reject license on blockchain: {}", e.getMessage(), e);
            throw new RuntimeException("区块链拒绝失败: " + e.getMessage(), e);
        }

        // 更新数据库状态
        license.setStatus("REVOKED");
        licenseMapper.updateById(license);

        log.info("License rejected: licenseId={}", licenseId);
    }

    /**
     * 激活授权
     */
    @Transactional(rollbackFor = Exception.class)
    public void activate(Long licenseId, String userAddress) {
        License license = licenseMapper.selectById(licenseId);
        if (license == null) {
            throw new RuntimeException("授权不存在");
        }

        if (!"APPROVED".equals(license.getStatus())) {
            throw new RuntimeException("授权状态不是已批准");
        }

        // 后端权限验证：检查用户是否有权限激活授权
        String licensee = license.getLicenseeAddress();
        String applicant = license.getApplicantAddress();

        // 获取版权所有者地址
        Copyright copyright = copyrightMapper.selectById(license.getCopyrightId());
        if (copyright == null) {
            throw new RuntimeException("关联的版权不存在");
        }
        String owner = copyright.getOwnerAddress();

        // 只有被授权方、申请人或版权所有者可以激活授权
        boolean canActivate = userAddress.equals(licensee) ||
                               userAddress.equals(applicant) ||
                               userAddress.equals(owner);

        if (!canActivate) {
            throw new RuntimeException("无权限激活授权：只有被授权方、申请人或版权所有者才能激活");
        }

        // ⚠️ 临时注释：时间验证已转移到后端，但旧合约仍在检查时间
        // 重新部署新合约后，可以保留这些验证作为双重保障
        /*
        long currentTime = System.currentTimeMillis() / 1000;
        if (currentTime < license.getStartTime()) {
            throw new RuntimeException("授权尚未开始");
        }

        if (currentTime > license.getEndTime()) {
            throw new RuntimeException("授权已过期");
        }
        */

        // 调用区块链合约激活授权
        try {
            String txHash = fiscoContractService.activateLicense(new BigInteger(license.getContractId()));
            log.info("License activated on blockchain: txHash={}, user={}", txHash, userAddress);
        } catch (Exception e) {
            log.error("Failed to activate license on blockchain: {}", e.getMessage(), e);
            throw new RuntimeException("区块链激活失败: " + e.getMessage(), e);
        }

        // 更新数据库状态
        license.setStatus("ACTIVE");
        licenseMapper.updateById(license);

        log.info("License activated: licenseId={}, user={}", licenseId, userAddress);
    }

    /**
     * 撤销授权
     */
    @Transactional(rollbackFor = Exception.class)
    public void revoke(Long licenseId) {
        License license = licenseMapper.selectById(licenseId);
        if (license == null) {
            throw new RuntimeException("授权不存在");
        }

        if (!"APPROVED".equals(license.getStatus()) && !"ACTIVE".equals(license.getStatus())) {
            throw new RuntimeException("只能撤销已批准或已激活的授权");
        }

        // 调用区块链合约撤销授权
        try {
            String txHash = fiscoContractService.revokeLicense(new BigInteger(license.getContractId()));
            log.info("License revoked on blockchain: txHash={}", txHash);
        } catch (Exception e) {
            log.error("Failed to revoke license on blockchain: {}", e.getMessage(), e);
            throw new RuntimeException("区块链撤销失败: " + e.getMessage(), e);
        }

        // 更新数据库状态
        license.setStatus("REVOKED");
        licenseMapper.updateById(license);

        log.info("License revoked: licenseId={}", licenseId);
    }

    /**
     * 根据ID查询授权
     */
    public LicenseVO getById(Long licenseId) {
        License license = licenseMapper.selectById(licenseId);
        return convertToVO(license);
    }

    /**
     * 根据版权ID查询授权列表
     */
    public List<LicenseVO> listByCopyrightId(Long copyrightId) {
        List<License> licenses = licenseMapper.selectByCopyrightId(copyrightId);
        return licenses.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    /**
     * 根据被授权方查询授权列表
     */
    public List<LicenseVO> listByLicensee(String licenseeAddress) {
        List<License> licenses = licenseMapper.selectByLicensee(licenseeAddress);
        return licenses.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    /**
     * 查询待审批的授权
     */
    public List<LicenseVO> listPending() {
        List<License> licenses = licenseMapper.selectPendingLicenses();
        return licenses.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    /**
     * 分页查询所有授权
     */
    public Page<LicenseVO> page(int pageNum, int pageSize) {
        Page<License> page = new Page<>(pageNum, pageSize);
        Page<License> result = licenseMapper.selectPage(page, null);

        Page<LicenseVO> voPage = new Page<>(pageNum, pageSize);
        voPage.setRecords(result.getRecords().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList()));
        voPage.setTotal(result.getTotal());

        return voPage;
    }

    /**
     * 转换为VO
     */
    private LicenseVO convertToVO(License license) {
        if (license == null) {
            return null;
        }
        LicenseVO vo = new LicenseVO();
        BeanUtils.copyProperties(license, vo);

        // 查询并设置版权标题
        if (license.getCopyrightId() != null) {
            Copyright copyright = copyrightMapper.selectById(license.getCopyrightId());
            if (copyright != null) {
                vo.setCopyrightTitle(copyright.getTitle());
            }
        }

        return vo;
    }
}
