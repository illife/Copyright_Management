package com.example.copyright.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 区块链工具类
 */
@Slf4j
@Component
public class BlockchainUtil {

    /**
     * 计算SHA256哈希
     */
    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to calculate SHA256: {}", e.getMessage());
            throw new RuntimeException("SHA256 algorithm not found", e);
        }
    }

    /**
     * 字节数组转十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * 验证地址格式
     */
    public static boolean isValidAddress(String address) {
        if (address == null || address.length() != 42) {
            return false;
        }
        return address.startsWith("0x") && address.substring(2).matches("[0-9a-fA-F]{40}");
    }

    /**
     * 验证IPFS哈希格式
     */
    public static boolean isValidIpfsHash(String hash) {
        if (hash == null || hash.length() < 46) {
            return false;
        }
        // IPFS hash格式: Qm... (base58) 或 /ipfs/Qm... 或 ipfs://Qm...
        return hash.matches("(ipfs://|/ipfs/)?[A-Za-z0-9]{46,}");
    }

    /**
     * 转换时间戳（秒级）
     */
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * BigInteger转字符串
     */
    public static String bigIntegerToString(BigInteger value) {
        return value != null ? value.toString() : "0";
    }
}
