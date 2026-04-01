package com.example.copyright.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.copyright.dto.CopyrightRegisterDTO;
import com.example.copyright.entity.Copyright;
import com.example.copyright.entity.User;
import com.example.copyright.mapper.CopyrightMapper;
import com.example.copyright.vo.BlockchainInfo;
import com.example.copyright.vo.CopyrightVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 版权服务
 */
@Slf4j
@Service
public class CopyrightService {

    @Autowired
    private CopyrightMapper copyrightMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private FiscoContractService fiscoContractService;

    /**
     * 注册版权（完整版，保存完整区块链信息）
     * 新合约版本：owner从当前登录用户获取，不再从DTO接收
     */
    @Transactional(rollbackFor = Exception.class)
    public Copyright register(CopyrightRegisterDTO dto, String ownerAddress) {
        // 验证用户存在
        User user = userService.getByAddress(ownerAddress);
        if (user == null || user.getStatus() != 1) {
            throw new RuntimeException("用户不存在或未激活");
        }

        // 调用区块链合约注册版权，获取完整的交易信息
        java.util.Map<String, Object> blockchainResult;
        String contractId;
        BlockchainInfo blockchainInfo;

        try {
            blockchainResult = fiscoContractService.registerCopyrightWithBlockchainInfo(
                dto.getTitle(),
                dto.getAuthor(),
                dto.getFileHash()
            );
            contractId = (String) blockchainResult.get("copyrightId");
            blockchainInfo = (BlockchainInfo) blockchainResult.get("blockchainInfo");
        } catch (Exception e) {
            log.error("Failed to register copyright on blockchain: {}", e.getMessage(), e);
            throw new RuntimeException("区块链注册失败: " + e.getMessage(), e);
        }

        // 保存版权记录到数据库（包含完整区块链信息）
        Copyright copyright = new Copyright();
        copyright.setContractId(contractId);
        copyright.setOwnerAddress(ownerAddress);
        copyright.setTitle(dto.getTitle());
        copyright.setAuthor(dto.getAuthor());
        copyright.setFileHash(dto.getFileHash());
        copyright.setDescription(dto.getDescription());
        copyright.setRegisterTime(System.currentTimeMillis());

        // 保存区块链交易信息
        copyright.setTransactionHash(blockchainInfo.getTransactionHash());
        copyright.setBlockHash(blockchainInfo.getBlockHash());
        copyright.setBlockNumber(blockchainInfo.getBlockNumber());
        copyright.setContractAddress(blockchainInfo.getContractAddress());
        copyright.setTransactionIndex(blockchainInfo.getTransactionIndex());
        copyright.setGasUsed(blockchainInfo.getGasUsed());
        copyright.setGasPrice(blockchainInfo.getGasPrice());

        copyright.setCreatedTime(LocalDateTime.now());
        copyright.setUpdatedTime(LocalDateTime.now());

        copyrightMapper.insert(copyright);

        log.info("版权注册成功: contractId={}, owner={}, txHash={}, blockNumber={}",
            contractId, ownerAddress, blockchainInfo.getTransactionHash(), blockchainInfo.getBlockNumber());

        return copyright;
    }

    /**
     * 根据ID查询版权
     */
    public CopyrightVO getById(Long id) {
        Copyright copyright = copyrightMapper.selectById(id);
        return convertToVO(copyright);
    }

    /**
     * 根据合约ID查询版权
     */
    public CopyrightVO getByContractId(String contractId) {
        Copyright copyright = copyrightMapper.selectByContractId(contractId);
        return convertToVO(copyright);
    }

    /**
     * 根据所有者查询版权列表
     */
    public List<CopyrightVO> listByOwner(String ownerAddress) {
        List<Copyright> copyrights = copyrightMapper.selectByOwner(ownerAddress);
        return copyrights.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    /**
     * 分页查询所有版权
     */
    public Page<CopyrightVO> page(int pageNum, int pageSize) {
        Page<Copyright> page = new Page<>(pageNum, pageSize);
        Page<Copyright> result = copyrightMapper.selectPage(page, null);

        Page<CopyrightVO> voPage = new Page<>(pageNum, pageSize);
        voPage.setRecords(result.getRecords().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList()));
        voPage.setTotal(result.getTotal());

        return voPage;
    }

    /**
     * 根据关键词搜索版权（标题、作者、描述）
     */
    public List<CopyrightVO> searchByKeyword(String keyword) {
        List<Copyright> copyrights = copyrightMapper.selectByKeyword(keyword);
        return copyrights.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    /**
     * 根据作者查询版权
     */
    public List<CopyrightVO> listByAuthor(String author) {
        List<Copyright> copyrights = copyrightMapper.selectByAuthor(author);
        return copyrights.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    /**
     * 转换为VO（包含完整区块链信息）
     */
    private CopyrightVO convertToVO(Copyright copyright) {
        if (copyright == null) {
            return null;
        }
        CopyrightVO vo = new CopyrightVO();
        BeanUtils.copyProperties(copyright, vo);

        // 计算交易手续费 (Gas Used * Gas Price)
        if (copyright.getGasUsed() != null && copyright.getGasPrice() != null) {
            try {
                // Gas Price 是十六进制字符串，转换为 Wei
                long gasUsed = copyright.getGasUsed();
                long gasPrice = Long.parseLong(copyright.getGasPrice().replace("0x", ""), 16);
                long feeWei = gasUsed * gasPrice;
                vo.setTransactionFee(feeWei + " Wei");
            } catch (Exception e) {
                log.warn("计算交易手续费失败: {}", e.getMessage());
            }
        }

        // 设置文件存储类型
        if (copyright.getFileHash() != null) {
            if (copyright.getFileHash().startsWith("Qm") || copyright.getFileHash().startsWith("ba")) {
                vo.setStorageType("ipfs");
                vo.setIpfsGatewayUrl("https://ipfs.io/ipfs/" + copyright.getFileHash());
            } else if (copyright.getFileHash().startsWith("local://")) {
                vo.setStorageType("local");
                vo.setFileDownloadUrl("/api/files/download/" + extractFileName(copyright.getFileHash()));
            }
        }

        // 生成浏览器链接（假设使用 FISCO BCOS 浏览器或区块链浏览器）
        // 实际使用时需要根据你的区块链浏览器URL调整
        String blockExplorerUrl = "https://browser.fisco-bcos.org";  // 替换为实际的浏览器地址
        if (copyright.getTransactionHash() != null) {
            vo.setTransactionBrowserUrl(blockExplorerUrl + "/transaction?hash=" + copyright.getTransactionHash());
        }
        if (copyright.getOwnerAddress() != null) {
            vo.setAddressBrowserUrl(blockExplorerUrl + "/address?address=" + copyright.getOwnerAddress());
        }
        if (copyright.getBlockNumber() != null) {
            vo.setBlockBrowserUrl(blockExplorerUrl + "/block?number=" + copyright.getBlockNumber());
        }

        return vo;
    }

    /**
     * 从本地文件哈希中提取文件名
     */
    private String extractFileName(String localFileHash) {
        if (localFileHash == null || !localFileHash.startsWith("local://")) {
            return null;
        }
        String withoutPrefix = localFileHash.substring("local://".length());
        int queryIndex = withoutPrefix.indexOf("?");
        return queryIndex != -1 ? withoutPrefix.substring(0, queryIndex) : withoutPrefix;
    }
}
