package com.example.copyright.service;

import com.example.copyright.config.ContractConfig;
import com.example.copyright.utils.IOUtil;
import com.example.copyright.utils.SDKUtil;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.crypto.keystore.KeyTool;
import org.fisco.bcos.sdk.crypto.hash.Keccak256;
import org.fisco.bcos.sdk.transaction.manager.AssembleTransactionProcessor;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * 区块链管理员服务
 * 用于初始化权限和管理角色
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "fisco.useWebase", havingValue = "false", matchIfMissing = false)
public class BlockchainAdminService {

    @Autowired(required = false)
    private SDKUtil sdkUtil;

    @Autowired
    private ContractConfig contractConfig;

    @Value("${fisco.account.adminAddress:0xb82ea6626cb8cd46081b2dc6ece702864490f366}")
    private String adminAddress;

    private static final String COPYRIGHT_CONTRACT = "CopyrightRegistry";
    private static final String RBAC_CONTRACT = "RBAC";

    private static String copyrightAbi = null;
    private static String copyrightBin = null;
    private static String rbacAbi = null;
    private static String rbacBin = null;

    // 角色常量（从合约中读取，存储为字节数组）
    private byte[] USER_REGISTRAR_ROLE_BYTES = null;
    private byte[] OWNER_ROLE_BYTES = null;

    // 为了兼容性，同时存储 base64 字符串（用于日志显示）
    private String USER_REGISTRAR_ROLE = null;
    private String OWNER_ROLE = null;

    @PostConstruct
    public void init() {
        try {
            if (copyrightAbi == null) {
                copyrightAbi = IOUtil.readResourceAsString("abi/CopyrightRegistry.abi");
                copyrightBin = IOUtil.readResourceAsString("bin/ecc/CopyrightRegistry.bin");
                rbacAbi = IOUtil.readResourceAsString("abi/RBAC.abi");
                rbacBin = IOUtil.readResourceAsString("bin/ecc/RBAC.bin");
                log.info("BlockchainAdminService: Contract files loaded");

                // 从合约中读取角色常量
                try {
                    loadRoleConstants();
                } catch (Exception e) {
                    log.error("Failed to load role constants from contract: {}", e.getMessage(), e);
                    throw new RuntimeException("Failed to initialize blockchain admin service", e);
                }
            }
        } catch (Exception e) {
            log.error("Failed to load contract files: {}", e.getMessage(), e);
        }
    }

    /**
     * 计算角色常量（使用keccak256哈希，与Solidity合约一致）
     * 直接在Java中计算，避免从合约读取时的编码问题
     */
    private void loadRoleConstants() {
        try {
            log.info("Calculating role constants using keccak256 hash");

            // 计算与Solidity合约相同的keccak256哈希值
            // Solidity: bytes32 public constant USER_REGISTRAR_ROLE = keccak256("USER_REGISTRAR_ROLE");
            Keccak256 keccak256 = new Keccak256();

            // 计算 USER_REGISTRAR_ROLE
            byte[] userRegistrarHash = keccak256.hash("USER_REGISTRAR_ROLE".getBytes());
            USER_REGISTRAR_ROLE_BYTES = userRegistrarHash;
            USER_REGISTRAR_ROLE = "base64://" + Base64.getEncoder().encodeToString(userRegistrarHash);

            log.info("✓ USER_REGISTRAR_ROLE calculated:");
            log.info("  Input: \"USER_REGISTRAR_ROLE\"");
            log.info("  Bytes length: {}", userRegistrarHash.length);
            log.info("  Hex: {}", bytesToHex(userRegistrarHash));
            log.info("  Base64: {}", USER_REGISTRAR_ROLE);

            // 计算 OWNER_ROLE
            byte[] ownerHash = keccak256.hash("OWNER_ROLE".getBytes());
            OWNER_ROLE_BYTES = ownerHash;
            OWNER_ROLE = "base64://" + Base64.getEncoder().encodeToString(ownerHash);

            log.info("✓ OWNER_ROLE calculated:");
            log.info("  Input: \"OWNER_ROLE\"");
            log.info("  Bytes length: {}", ownerHash.length);
            log.info("  Hex: {}", bytesToHex(ownerHash));
            log.info("  Base64: {}", OWNER_ROLE);

            log.info("Role constants loaded successfully");

        } catch (Exception e) {
            log.error("Failed to calculate role constants: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to calculate role constants", e);
        }
    }

    /**
     * 从对象中提取字节数组
     * 添加健壮的错误处理和字符清理
     */
    private byte[] extractBytes(Object obj) {
        if (obj == null) {
            return null;
        }

        log.info("Extracting bytes from: type={}, toString={}", obj.getClass().getName(), obj);

        // 1. 如果已经是 byte[]，直接返回
        if (obj instanceof byte[]) {
            log.info("Object is byte[], returning directly");
            return (byte[]) obj;
        }

        // 2. 如果是 String，尝试从 base64 解码
        if (obj instanceof String) {
            String str = (String) obj;
            log.info("Object is String: length={}, chars={}", str.length(), str.toCharArray());

            // 清理字符串：移除所有非base64字符（只保留A-Za-z0-9+/=）
            String cleanStr = str.replaceAll("[^A-Za-z0-9+/=]", "");
            log.info("Cleaned string: length={}", cleanStr.length());

            try {
                if (str.startsWith("base64://")) {
                    String base64Part = str.substring("base64://".length());
                    log.info("Extracting base64 part: length={}", base64Part.length());
                    byte[] result = Base64.getDecoder().decode(base64Part);
                    log.info("Successfully decoded, result length: {}", result.length);
                    return result;
                } else {
                    // 可能是纯 base64 字符串
                    log.info("Decoding as pure base64 string");
                    byte[] result = Base64.getDecoder().decode(str);
                    log.info("Successfully decoded, result length: {}", result.length);
                    return result;
                }
            } catch (IllegalArgumentException e) {
                log.error("Base64 decode failed: {}, string was: '{}', cleaned: '{}'",
                    e.getMessage(), str, cleanStr);

                // 尝试使用清理后的字符串
                try {
                    byte[] result = Base64.getDecoder().decode(cleanStr);
                    log.info("Successfully decoded with cleaned string, result length: {}", result.length);
                    return result;
                } catch (IllegalArgumentException e2) {
                    log.error("Cleaned string also failed to decode: {}", e2.getMessage());
                    throw new RuntimeException("Failed to decode base64 string: " + str, e);
                }
            }
        }

        // 3. 如果是 List，递归处理第一个元素
        if (obj instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) obj;
            log.info("Object is List with {} elements", list.size());
            if (!list.isEmpty()) {
                log.info("Extracting first element from List");
                return extractBytes(list.get(0));
            }
        }

        // 4. 如果是数组，处理第一个元素
        if (obj.getClass().isArray()) {
            Object[] array = (Object[]) obj;
            log.info("Object is Array with length {}", array.length);
            if (array.length > 0) {
                log.info("Extracting first element from Array");
                return extractBytes(array[0]);
            }
        }

        log.error("Cannot extract bytes from type: {}", obj.getClass().getName());
        throw new RuntimeException("Cannot extract bytes from type: " + obj.getClass().getName());
    }

    /**
     * 从对象中提取 base64 字符串
     * 处理各种可能的返回格式
     */
    private String extractBase64String(Object obj) {
        if (obj == null) {
            return null;
        }

        log.info("Extracting base64 from: type={}, toString={}", obj.getClass().getName(), obj);

        // 1. 如果是 String，检查是否是 base64 格式
        if (obj instanceof String) {
            String str = (String) obj;
            if (str.startsWith("base64://")) {
                return str;
            } else {
                // 可能是纯 base64 字符串，添加前缀
                log.warn("String does not start with base64://, adding prefix");
                return "base64://" + str;
            }
        }

        // 2. 如果是 byte[]，转换为 base64
        if (obj instanceof byte[]) {
            byte[] bytes = (byte[]) obj;
            return "base64://" + Base64.getEncoder().encodeToString(bytes);
        }

        // 3. 如果是 List，递归处理第一个元素
        if (obj instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) obj;
            if (!list.isEmpty()) {
                log.info("Object is a List, extracting first element");
                return extractBase64String(list.get(0));
            }
        }

        // 4. 如果是数组，处理第一个元素
        if (obj.getClass().isArray()) {
            Object[] array = (Object[]) obj;
            if (array.length > 0) {
                log.info("Object is an Array, extracting first element");
                return extractBase64String(array[0]);
            }
        }

        log.error("Cannot extract base64 from type: {}", obj.getClass().getName());
        return null;
    }

    /**
     * 将字节数组转换为十六进制字符串
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    private AssembleTransactionProcessor getCopyrightProcessor() throws Exception {
        if (copyrightAbi == null || copyrightBin == null) {
            throw new RuntimeException("CopyrightRegistry ABI or BIN not loaded");
        }
        return sdkUtil.sdk(COPYRIGHT_CONTRACT, copyrightAbi, copyrightBin);
    }

    private AssembleTransactionProcessor getRbacProcessor() throws Exception {
        if (rbacAbi == null || rbacBin == null) {
            throw new RuntimeException("RBAC ABI or BIN not loaded");
        }
        return sdkUtil.sdk(RBAC_CONTRACT, rbacAbi, rbacBin);
    }

    /**
     * 授予角色
     * 使用 CopyrightRegistry 合约调用（它继承自 RBAC）
     * bytes32 参数传递字节数组
     */
    public String grantRole(String role, String account) {
        try {
            AssembleTransactionProcessor processor = getCopyrightProcessor();

            List<Object> params = new ArrayList<>();
            // 如果是 base64 字符串，转换为字节数组
            byte[] roleBytes;
            if (role.startsWith("base64://")) {
                roleBytes = base64ToBytes(role);
            } else {
                // 假设已经存储为 base64 格式的字符串引用
                roleBytes = USER_REGISTRAR_ROLE_BYTES != null && role.contains("USER_REGISTRAR") ?
                    USER_REGISTRAR_ROLE_BYTES : OWNER_ROLE_BYTES;
            }

            params.add(roleBytes);
            params.add(account);

            log.info("Calling CopyrightRegistry.grantRole:");
            log.info("  role bytes length: {}", roleBytes.length);
            log.info("  account: {}", account);

            TransactionResponse transactionResponse = processor.sendTransactionAndGetResponseByContractLoader(
                COPYRIGHT_CONTRACT,
                contractConfig.getCopyrightAddress(),
                "grantRole",
                params
            );

            String message = transactionResponse.getReceiptMessages();
            if ("Success".equals(message)) {
                String txHash = transactionResponse.getTransactionReceipt().getTransactionHash();
                log.info("✓ Role granted successfully: role={}, account={}, txHash={}", role, account, txHash);
                return txHash;
            } else {
                log.error("✗ Grant role failed, message: {}", message);
                throw new RuntimeException("Grant role failed, message: " + message);
            }

        } catch (Exception e) {
            log.error("Failed to grant role: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to grant role: " + e.getMessage(), e);
        }
    }

    /**
     * 初始化管理员权限
     * 给当前账户授予 USER_REGISTRAR_ROLE，使其可以注册用户
     */
    public void initializeAdminPermissions() {
        try {
            log.info("=== 开始检查链上权限 ===");
            log.info("管理员地址: {}", adminAddress);

            if (USER_REGISTRAR_ROLE_BYTES == null) {
                throw new RuntimeException("USER_REGISTRAR_ROLE not loaded. Cannot initialize permissions.");
            }

            if (OWNER_ROLE_BYTES == null) {
                throw new RuntimeException("OWNER_ROLE not loaded. Cannot initialize permissions.");
            }

            // 1. 检查当前账户是否已有 USER_REGISTRAR_ROLE
            boolean hasUserRegistrarRole = hasRoleBytes(USER_REGISTRAR_ROLE_BYTES, adminAddress);
            log.info("USER_REGISTRAR_ROLE: {}", hasUserRegistrarRole ? "✓ 已拥有" : "✗ 未拥有");

            if (hasUserRegistrarRole) {
                log.info("✓ 管理员账户已拥有 USER_REGISTRAR_ROLE，无需重复授予");
                return;
            }

            // 2. 检查当前账户是否拥有 OWNER_ROLE
            boolean hasOwnerRole = hasRoleBytes(OWNER_ROLE_BYTES, adminAddress);
            log.info("OWNER_ROLE: {}", hasOwnerRole ? "✓ 已拥有" : "✗ 未拥有");

            if (!hasOwnerRole) {
                log.error("✗ 管理员账户没有 OWNER_ROLE，无法授予 USER_REGISTRAR_ROLE");
                log.error("解决方案：");
                log.error("1. 确保当前账户 ({}) 是合约部署者", adminAddress);
                log.error("2. 或在 WeBASE-Front 中调用 CopyrightRegistry 合约的 grantRole");
                log.error("3. 参数: role=OWNER_ROLE, account={}", adminAddress);
                throw new RuntimeException("管理员账户缺少必要的链上权限 (OWNER_ROLE)");
            }

            // 3. 授予 USER_REGISTRAR_ROLE（使用 CopyrightRegistry 合约）
            log.info("正在授予 USER_REGISTRAR_ROLE 给当前账户...");
            String txHash = grantRole(USER_REGISTRAR_ROLE, adminAddress);
            log.info("✓ 权限授予交易已发送! txHash={}", txHash);

            // 4. 验证权限是否已授予
            Thread.sleep(2000); // 等待交易上链
            boolean nowHasRole = hasRoleBytes(USER_REGISTRAR_ROLE_BYTES, adminAddress);
            if (nowHasRole) {
                log.info("✓ 权限验证成功，管理员账户现已拥有 USER_REGISTRAR_ROLE");
            } else {
                log.warn("⚠ 权限授予交易已发送，但链上尚未生效，可能需要等待更多确认");
            }

        } catch (Exception e) {
            log.error("Failed to initialize admin permissions: {}", e.getMessage(), e);
            throw new RuntimeException("权限初始化失败: " + e.getMessage(), e);
        }
    }

    /**
     * 检查当前账户的完整权限状态
     */
    public void checkPermissions() {
        log.info("=== 链上权限检查报告 ===");
        log.info("管理员地址: {}", adminAddress);
        log.info("合约地址: {}", contractConfig.getCopyrightAddress());

        try {
            // 使用字节数组直接检查，避免 base64 编码问题
            boolean hasOwnerRole = hasRoleBytes(OWNER_ROLE_BYTES, adminAddress);
            boolean hasUserRegistrarRole = hasRoleBytes(USER_REGISTRAR_ROLE_BYTES, adminAddress);

            log.info("OWNER_ROLE: {}", hasOwnerRole ? "✓ 拥有" : "✗ 未拥有");
            log.info("USER_REGISTRAR_ROLE: {}", hasUserRegistrarRole ? "✓ 拥有" : "✗ 未拥有");

            if (hasUserRegistrarRole) {
                log.info("✓ 管理员账户具有注册用户权限，可以正常使用");
            } else {
                log.warn("⚠ 管理员账户缺少 USER_REGISTRAR_ROLE，无法注册用户");
                log.warn("请调用 POST /api/admin/init-permissions 进行初始化");
            }

        } catch (Exception e) {
            log.error("权限检查失败: {}", e.getMessage(), e);
        }

        log.info("========================");
    }

    /**
     * 检查账户是否有指定角色（使用字节数组，避免 base64 编码问题）
     * 使用 CopyrightRegistry 合约调用（它继承自 RBAC）
     * bytes32 参数直接传递字节数组
     */
    private boolean hasRoleBytes(byte[] roleBytes, String account) {
        try {
            AssembleTransactionProcessor processor = getCopyrightProcessor();

            List<Object> params = new ArrayList<>();
            params.add(roleBytes);  // 直接传递字节数组，不需要转换
            params.add(account);

            log.debug("Calling CopyrightRegistry.hasRole: role bytes (length: {}), account={}",
                    roleBytes.length, account);

            TransactionResponse transactionResponse = processor.sendTransactionAndGetResponseByContractLoader(
                COPYRIGHT_CONTRACT,
                contractConfig.getCopyrightAddress(),
                "hasRole",
                params
            );

            List<Object> returnObject = transactionResponse.getReturnObject();
            if (returnObject != null && !returnObject.isEmpty()) {
                Boolean hasRoleResult = (Boolean) returnObject.get(0);
                log.debug("Account {} has role: {}", account, hasRoleResult);
                return hasRoleResult;
            }
            return false;

        } catch (Exception e) {
            log.error("Failed to check role: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 检查账户是否有指定角色
     * 使用 CopyrightRegistry 合约调用（它继承自 RBAC）
     * bytes32 参数需要传递字节数组
     */
    public boolean hasRole(String role, String account) {
        try {
            AssembleTransactionProcessor processor = getCopyrightProcessor();

            List<Object> params = new ArrayList<>();
            // 将 base64 字符串转换为字节数组
            byte[] roleBytes = base64ToBytes(role);
            params.add(roleBytes);
            params.add(account);

            log.debug("Calling CopyrightRegistry.hasRole: role={} -> bytes (length: {}), account={}",
                    role, roleBytes.length, account);

            TransactionResponse transactionResponse = processor.sendTransactionAndGetResponseByContractLoader(
                COPYRIGHT_CONTRACT,
                contractConfig.getCopyrightAddress(),
                "hasRole",
                params
            );

            List<Object> returnObject = transactionResponse.getReturnObject();
            if (returnObject != null && !returnObject.isEmpty()) {
                Boolean hasRoleResult = (Boolean) returnObject.get(0);
                log.debug("Account {} has role {}: {}", account, role, hasRoleResult);
                return hasRoleResult;
            }
            return false;

        } catch (Exception e) {
            log.error("Failed to check role: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 将 base64 字符串转换为字节数组
     */
    private byte[] base64ToBytes(String base64) {
        // 移除 base64:// 前缀
        String cleanBase64 = base64.startsWith("base64://") ? base64.substring("base64://".length()) : base64;
        return Base64.getDecoder().decode(cleanBase64);
    }
}
