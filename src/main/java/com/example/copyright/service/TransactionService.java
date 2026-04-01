package com.example.copyright.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.copyright.dto.TransactionRecordQueryDTO;
import com.example.copyright.entity.Copyright;
import com.example.copyright.entity.License;
import com.example.copyright.entity.RoyaltyPayment;
import com.example.copyright.entity.TransactionRecord;
import com.example.copyright.mapper.CopyrightMapper;
import com.example.copyright.mapper.LicenseMapper;
import com.example.copyright.mapper.RoyaltyPaymentMapper;
import com.example.copyright.mapper.TransactionRecordMapper;
import com.example.copyright.vo.TransactionRecordVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 交易记录服务
 * 整合版权、授权、版税等所有表的交易记录
 */
@Slf4j
@Service
public class TransactionService {

    @Autowired(required = false)
    private TransactionRecordMapper transactionRecordMapper;

    @Autowired(required = false)
    private CopyrightMapper copyrightMapper;

    @Autowired(required = false)
    private LicenseMapper licenseMapper;

    @Autowired(required = false)
    private RoyaltyPaymentMapper royaltyPaymentMapper;

    /**
     * 获取交易统计信息
     */
    public Map<String, Object> getTransactionStats() {
        Map<String, Object> stats = new HashMap<>();

        // 优先从transactions表查询
        if (transactionRecordMapper != null) {
            try {
                Map<String, Object> dbStats = transactionRecordMapper.getTransactionStats();
                if (dbStats != null && !dbStats.isEmpty()) {
                    stats.put("total", dbStats.get("total"));
                    stats.put("success", dbStats.get("success"));
                    stats.put("pending", dbStats.get("pending"));
                    stats.put("failed", dbStats.get("failed"));
                    return stats;
                }
            } catch (Exception e) {
                log.warn("Failed to get stats from transactions table: {}", e.getMessage());
            }
        }

        // 备用：从各个业务表聚合统计
        long copyrightCount = copyrightMapper != null ? copyrightMapper.selectCount(null) : 0;
        long licenseCount = licenseMapper != null ? licenseMapper.selectCount(null) : 0;
        long royaltyCount = royaltyPaymentMapper != null ? royaltyPaymentMapper.selectCount(null) : 0;
        long total = copyrightCount + licenseCount + royaltyCount;

        stats.put("total", total);
        stats.put("success", total); // 假设都已成功
        stats.put("pending", 0);
        stats.put("failed", 0);

        return stats;
    }

    /**
     * 获取交易记录列表（分页）
     */
    public Page<TransactionRecordVO> getTransactionList(TransactionRecordQueryDTO dto) {
        Page<TransactionRecord> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        Page<TransactionRecordVO> voPage = new Page<>(dto.getPageNum(), dto.getPageSize());

        // 优先从transactions表查询
        if (transactionRecordMapper != null) {
            try {
                QueryWrapper<TransactionRecord> wrapper = buildQueryWrapper(dto);
                wrapper.orderByDesc("block_number");

                Page<TransactionRecord> resultPage = transactionRecordMapper.selectPage(page, wrapper);
                List<TransactionRecordVO> voList = resultPage.getRecords().stream()
                        .map(this::convertToVO)
                        .collect(Collectors.toList());

                voPage.setRecords(voList);
                voPage.setTotal(resultPage.getTotal());
                return voPage;
            } catch (Exception e) {
                log.warn("Failed to query transactions table: {}", e.getMessage());
            }
        }

        // 备用：从各个业务表聚合数据
        return getAggregatedTransactionList(dto);
    }

    /**
     * 从各个业务表聚合交易记录
     */
    private Page<TransactionRecordVO> getAggregatedTransactionList(TransactionRecordQueryDTO dto) {
        List<TransactionRecordVO> allRecords = new ArrayList<>();

        // 从版权表获取
        if (copyrightMapper != null) {
            try {
                List<Copyright> copyrights = copyrightMapper.selectList(null);
                for (Copyright cp : copyrights) {
                    if (cp.getTransactionHash() != null && !cp.getTransactionHash().isEmpty()) {
                        TransactionRecordVO vo = TransactionRecordVO.builder()
                                .id(cp.getId())
                                .transactionHash(cp.getTransactionHash())
                                .type("COPYRIGHT_REGISTER")
                                .typeText("版权注册")
                                .blockNumber(cp.getBlockNumber())
                                .fromAddress(cp.getOwnerAddress())
                                .toAddress(cp.getContractAddress())
                                .contractAddress(cp.getContractAddress())
                                .status("SUCCESS")
                                .timestamp(cp.getRegisterTime() != null ? cp.getRegisterTime() / 1000 : System.currentTimeMillis() / 1000)
                                .gasUsed(cp.getGasUsed())
                                .copyrightId(cp.getId())
                                .description("注册版权: " + cp.getTitle())
                                .browserUrl(buildBrowserUrl(cp.getTransactionHash(), null))
                                .build();
                        allRecords.add(vo);
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to load copyright transactions: {}", e.getMessage());
            }
        }

        // 从授权表获取
        if (licenseMapper != null) {
            try {
                List<License> licenses = licenseMapper.selectList(null);
                for (License license : licenses) {
                    if (license.getTransactionHash() != null && !license.getTransactionHash().isEmpty()) {
                        String type = mapLicenseStatusToType(license.getStatus());
                        TransactionRecordVO vo = TransactionRecordVO.builder()
                                .id(license.getId())
                                .transactionHash(license.getTransactionHash())
                                .type(type)
                                .typeText(getLicenseTypeText(type))
                                .blockNumber(null) // License表没有blockNumber字段
                                .fromAddress(license.getLicenseeAddress())
                                .toAddress(license.getContractAddress())
                                .contractAddress(license.getContractAddress())
                                .status("SUCCESS")
                                .timestamp(license.getCreatedTime() != null ? java.time.ZonedDateTime.of(license.getCreatedTime(), java.time.ZoneId.systemDefault()).toEpochSecond() : System.currentTimeMillis() / 1000)
                                .licenseId(license.getId())
                                .copyrightId(license.getCopyrightId())
                                .description("授权申请: LC-" + String.format("%04d", license.getId()))
                                .browserUrl(buildBrowserUrl(license.getTransactionHash(), null))
                                .build();
                        allRecords.add(vo);
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to load license transactions: {}", e.getMessage());
            }
        }

        // 从版税表获取
        if (royaltyPaymentMapper != null) {
            try {
                List<RoyaltyPayment> payments = royaltyPaymentMapper.selectList(null);
                for (RoyaltyPayment payment : payments) {
                    if (payment.getTransactionHash() != null && !payment.getTransactionHash().isEmpty()) {
                        TransactionRecordVO vo = TransactionRecordVO.builder()
                                .id(payment.getId())
                                .transactionHash(payment.getTransactionHash())
                                .type("ROYALTY_PAYMENT")
                                .typeText("版税支付")
                                .fromAddress(payment.getPayerAddress())
                                .toAddress(payment.getPayeeAddress())
                                .status("SUCCESS")
                                .timestamp(payment.getBlockTimestamp() != null ? payment.getBlockTimestamp() :
                                    (payment.getCreatedTime() != null ? java.time.ZonedDateTime.of(payment.getCreatedTime(), java.time.ZoneId.systemDefault()).toEpochSecond() : System.currentTimeMillis() / 1000))
                                .copyrightId(payment.getCopyrightId())
                                .description("支付版税: " + payment.getAmount() + " " + payment.getCurrency())
                                .browserUrl(buildBrowserUrl(payment.getTransactionHash(), null))
                                .build();
                        allRecords.add(vo);
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to load royalty transactions: {}", e.getMessage());
            }
        }

        // 应用筛选
        List<TransactionRecordVO> filteredList = allRecords.stream()
                .filter(vo -> applyFilters(vo, dto))
                .sorted((a, b) -> {
                    // 按时间戳降序排序
                    long timeA = a.getTimestamp() != null ? a.getTimestamp() : 0;
                    long timeB = b.getTimestamp() != null ? b.getTimestamp() : 0;
                    return Long.compare(timeB, timeA);
                })
                .collect(Collectors.toList());

        // 分页
        int start = (dto.getPageNum() - 1) * dto.getPageSize();
        int end = Math.min(start + dto.getPageSize(), filteredList.size());
        List<TransactionRecordVO> pagedList = start < filteredList.size()
                ? filteredList.subList(start, end)
                : new ArrayList<>();

        Page<TransactionRecordVO> resultPage = new Page<>(dto.getPageNum(), dto.getPageSize());
        resultPage.setRecords(pagedList);
        resultPage.setTotal(filteredList.size());
        return resultPage;
    }

    /**
     * 构建查询条件
     */
    private QueryWrapper<TransactionRecord> buildQueryWrapper(TransactionRecordQueryDTO dto) {
        QueryWrapper<TransactionRecord> wrapper = new QueryWrapper<>();

        // 类型筛选
        if (dto.getType() != null && !dto.getType().isEmpty()) {
            wrapper.like("function_name", dto.getType());
        }

        // 状态筛选
        if (dto.getStatus() != null && !dto.getStatus().isEmpty()) {
            int statusValue = mapStatusValue(dto.getStatus());
            wrapper.eq("status", statusValue);
        }

        // 地址筛选
        if (dto.getFromAddress() != null && !dto.getFromAddress().isEmpty()) {
            wrapper.eq("from_address", dto.getFromAddress());
        }

        if (dto.getToAddress() != null && !dto.getToAddress().isEmpty()) {
            wrapper.eq("to_address", dto.getToAddress());
        }

        // 区块号筛选
        if (dto.getBlockNumber() != null) {
            wrapper.eq("block_number", dto.getBlockNumber());
        }

        // 交易哈希模糊查询
        if (dto.getTransactionHash() != null && !dto.getTransactionHash().isEmpty()) {
            wrapper.like("transaction_hash", dto.getTransactionHash());
        }

        return wrapper;
    }

    /**
     * 应用筛选条件
     */
    private boolean applyFilters(TransactionRecordVO vo, TransactionRecordQueryDTO dto) {
        // 类型筛选
        if (dto.getType() != null && !dto.getType().isEmpty() && !dto.getType().equals(vo.getType())) {
            return false;
        }

        // 状态筛选
        if (dto.getStatus() != null && !dto.getStatus().isEmpty() && !dto.getStatus().equals(vo.getStatus())) {
            return false;
        }

        // 地址筛选
        if (dto.getFromAddress() != null && !dto.getFromAddress().isEmpty()) {
            if (vo.getFromAddress() == null || !vo.getFromAddress().toLowerCase().contains(dto.getFromAddress().toLowerCase())) {
                return false;
            }
        }

        if (dto.getToAddress() != null && !dto.getToAddress().isEmpty()) {
            if (vo.getToAddress() == null || !vo.getToAddress().toLowerCase().contains(dto.getToAddress().toLowerCase())) {
                return false;
            }
        }

        // 交易哈希筛选
        if (dto.getTransactionHash() != null && !dto.getTransactionHash().isEmpty()) {
            if (vo.getTransactionHash() == null || !vo.getTransactionHash().toLowerCase().contains(dto.getTransactionHash().toLowerCase())) {
                return false;
            }
        }

        return true;
    }

    /**
     * 转换为VO
     */
    private TransactionRecordVO convertToVO(TransactionRecord record) {
        // 从functionName推断交易类型
        String type = inferTransactionType(record.getFunctionName());
        String typeText = getTransactionTypeText(type);

        // 状态转换
        String status = mapStatusCode(record.getStatus());

        return TransactionRecordVO.builder()
                .id(record.getId())
                .transactionHash(record.getTransactionHash())
                .type(type)
                .typeText(typeText)
                .blockNumber(record.getBlockNumber())
                .fromAddress(record.getFromAddress())
                .toAddress(record.getToAddress())
                .contractAddress(record.getContractAddress())
                .status(status)
                .timestamp(record.getBlockTimestamp())
                .gasUsed(record.getGasUsed())
                .description(buildDescription(type))
                .browserUrl(buildBrowserUrl(record.getTransactionHash(), record.getBlockNumber()))
                .build();
    }

    /**
     * 推断交易类型
     */
    private String inferTransactionType(String functionName) {
        if (functionName == null) {
            return "UNKNOWN";
        }

        String upper = functionName.toUpperCase();
        if (upper.contains("COPYRIGHT") && upper.contains("REGISTER")) {
            return "COPYRIGHT_REGISTER";
        } else if (upper.contains("LICENSE") && upper.contains("APPLY")) {
            return "LICENSE_APPLY";
        } else if (upper.contains("LICENSE") && upper.contains("APPROVE")) {
            return "LICENSE_APPROVE";
        } else if (upper.contains("LICENSE") && upper.contains("ACTIVATE")) {
            return "LICENSE_ACTIVATE";
        } else if (upper.contains("LICENSE") && upper.contains("REVOKE")) {
            return "LICENSE_REVOKE";
        } else if (upper.contains("ROYALTY") || upper.contains("PAYMENT")) {
            return "ROYALTY_PAYMENT";
        } else if (upper.contains("USER") && upper.contains("REGISTER")) {
            return "USER_REGISTER";
        }

        return "UNKNOWN";
    }

    /**
     * 授权状态转交易类型
     */
    private String mapLicenseStatusToType(String licenseStatus) {
        if (licenseStatus == null) {
            return "LICENSE_APPLY";
        }
        switch (licenseStatus.toUpperCase()) {
            case "PENDING":
                return "LICENSE_APPLY";
            case "APPROVED":
                return "LICENSE_APPROVE";
            case "ACTIVE":
                return "LICENSE_ACTIVATE";
            case "REVOKED":
                return "LICENSE_REVOKE";
            default:
                return "LICENSE_APPLY";
        }
    }

    /**
     * 获取交易类型显示文本
     */
    private String getTransactionTypeText(String type) {
        switch (type) {
            case "COPYRIGHT_REGISTER":
                return "版权注册";
            case "LICENSE_APPLY":
                return "授权申请";
            case "LICENSE_APPROVE":
                return "授权批准";
            case "LICENSE_ACTIVATE":
                return "授权激活";
            case "LICENSE_REVOKE":
                return "授权撤销";
            case "ROYALTY_PAYMENT":
                return "版税支付";
            case "USER_REGISTER":
                return "用户注册";
            default:
                return "未知交易";
        }
    }

    /**
     * 获取授权类型显示文本
     */
    private String getLicenseTypeText(String type) {
        return getTransactionTypeText(type);
    }

    /**
     * 映射状态值
     */
    private int mapStatusValue(String status) {
        switch (status.toUpperCase()) {
            case "SUCCESS":
                return 1;
            case "PENDING":
                return 2;
            case "FAILED":
                return 0;
            default:
                return 1;
        }
    }

    /**
     * 映射状态码到状态文本
     */
    private String mapStatusCode(Integer status) {
        if (status == null) {
            return "SUCCESS";
        }
        switch (status) {
            case 1:
                return "SUCCESS";
            case 2:
                return "PENDING";
            case 0:
                return "FAILED";
            default:
                return "SUCCESS";
        }
    }

    /**
     * 构建交易描述
     */
    private String buildDescription(String type) {
        switch (type) {
            case "COPYRIGHT_REGISTER":
                return "注册版权";
            case "LICENSE_APPLY":
                return "申请授权";
            case "LICENSE_APPROVE":
                return "审批授权";
            case "LICENSE_ACTIVATE":
                return "激活授权";
            case "LICENSE_REVOKE":
                return "撤销授权";
            case "ROYALTY_PAYMENT":
                return "支付版税";
            case "USER_REGISTER":
                return "注册用户";
            default:
                return "区块链交易";
        }
    }

    /**
     * 构建浏览器链接
     */
    private String buildBrowserUrl(String txHash, Long blockNumber) {
        String baseUrl = "https://browser.fisco-bcos.org";
        if (txHash != null && !txHash.isEmpty()) {
            return baseUrl + "/transaction?hash=" + txHash;
        }
        if (blockNumber != null) {
            return baseUrl + "/block?number=" + blockNumber;
        }
        return baseUrl;
    }
}
