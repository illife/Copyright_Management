package com.example.copyright.service;

import com.example.copyright.dto.RoyaltyPaymentDTO;
import com.example.copyright.entity.RoyaltyPayment;
import com.example.copyright.mapper.RoyaltyPaymentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 版税服务
 */
@Slf4j
@Service
public class RoyaltyService {

    @Autowired
    private RoyaltyPaymentMapper royaltyPaymentMapper;

    @Autowired
    private FiscoContractService fiscoContractService;

    /**
     * 记录版税支付
     */
    @Transactional(rollbackFor = Exception.class)
    public RoyaltyPayment recordPayment(RoyaltyPaymentDTO dto) {
        // 调用区块链合约记录版税
        try {
            String txHash = fiscoContractService.recordRoyaltyPayment(
                new BigInteger(dto.getCopyrightId()),
                dto.getPayer(),
                dto.getPayee(),
                dto.getAmount().toBigInteger(),
                dto.getCurrency()
            );
            log.info("Royalty payment recorded on blockchain: txHash={}", txHash);

            // 保存版税记录到数据库
            RoyaltyPayment payment = new RoyaltyPayment();
            payment.setCopyrightId(Long.valueOf(dto.getCopyrightId()));
            payment.setPayerAddress(dto.getPayer());
            payment.setPayeeAddress(dto.getPayee());
            payment.setAmount(dto.getAmount());
            payment.setCurrency(dto.getCurrency());
            payment.setTransactionHash(txHash);
            payment.setBlockTimestamp(System.currentTimeMillis());
            payment.setCreatedTime(LocalDateTime.now());

            royaltyPaymentMapper.insert(payment);

            return payment;

        } catch (Exception e) {
            log.error("Failed to record royalty payment: {}", e.getMessage(), e);
            throw new RuntimeException("记录版税失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据版权ID查询版税记录
     */
    public List<RoyaltyPayment> listByCopyrightId(Long copyrightId) {
        return royaltyPaymentMapper.selectByCopyrightId(copyrightId);
    }

    /**
     * 根据支付方查询版税记录
     */
    public List<RoyaltyPayment> listByPayer(String payerAddress) {
        return royaltyPaymentMapper.selectByPayer(payerAddress);
    }

    /**
     * 根据收款方查询版税记录
     */
    public List<RoyaltyPayment> listByPayee(String payeeAddress) {
        return royaltyPaymentMapper.selectByPayee(payeeAddress);
    }
}
