package com.example.copyright.service;

import com.example.copyright.config.ContractConfig;
import com.example.copyright.utils.IOUtil;
import com.example.copyright.utils.SDKUtil;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.transaction.manager.AssembleTransactionProcessor;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * FISCO BCOS合约服务
 * 所有操作都通过 CopyrightRegistry 合约
 * 完全模仿 backend 真实项目实现
 */
@Slf4j
@Service
public class FiscoContractService {

    @Autowired(required = false)
    private SDKUtil sdkUtil;

    @Autowired
    private ContractConfig contractConfig;

    // 合约名称和地址
    private static final String CONTRACT_NAME = "CopyrightRegistry";
    private static String contractAbi = null;
    private static String contractBin = null;

    @PostConstruct
    public void init() {
        try {
            log.info("Loading CopyrightRegistry contract files...");

            // 读取 CopyrightRegistry 合约 ABI 和 BIN
            if (contractAbi == null) {
                contractAbi = IOUtil.readResourceAsString("abi/CopyrightRegistry.abi");
                contractBin = IOUtil.readResourceAsString("bin/ecc/CopyrightRegistry.bin");

                log.info("Contract ABI loaded: {}", contractAbi != null && contractAbi.length() > 0 ? "YES" : "NO");
                log.info("Contract BIN loaded: {}", contractBin != null && contractBin.length() > 0 ? "YES" : "NO");

                if (contractAbi == null || contractAbi.isEmpty()) {
                    log.error("Failed to load contract ABI!");
                }
                if (contractBin == null || contractBin.isEmpty()) {
                    log.error("Failed to load contract BIN!");
                }
            }

            log.info("CopyrightRegistry contract files loaded successfully");
        } catch (Exception e) {
            log.error("Failed to load contract ABI/BIN files: {}", e.getMessage(), e);
        }
    }

    /**
     * 获取合约处理器
     */
    private AssembleTransactionProcessor getProcessor() throws Exception {
        if (contractAbi == null || contractBin == null) {
            throw new RuntimeException("Contract ABI or BIN not loaded");
        }
        return sdkUtil.sdk(CONTRACT_NAME, contractAbi, contractBin);
    }

    /**
     * 注册用户到区块链
     * 新合约版本：只需要userAddress参数，不需要username
     * 通过 CopyrightRegistry 合约调用 registerUser(address user)
     *
     * @param userAddress 用户地址
     * @return 交易哈希
     */
    public String registerUser(String userAddress) {
        try {
            AssembleTransactionProcessor processor = getProcessor();

            List<Object> params = new ArrayList<>();
            params.add(userAddress);

            // 调用合约
            TransactionResponse transactionResponse = processor.sendTransactionAndGetResponseByContractLoader(
                CONTRACT_NAME,
                contractConfig.getCopyrightAddress(),
                "registerUser",
                params
            );

            // 检查结果
            String message = transactionResponse.getReceiptMessages();
            if ("Success".equals(message)) {
                String txHash = transactionResponse.getTransactionReceipt().getTransactionHash();
                log.info("User registered on blockchain, user: {}, txHash: {}", userAddress, txHash);
                return txHash;
            } else {
                throw new RuntimeException("Register user failed, message: " + message);
            }

        } catch (Exception e) {
            log.error("Failed to register user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to register user: " + e.getMessage(), e);
        }
    }

    /**
     * 注册版权到区块链（返回完整区块链信息）
     * 新合约版本：只需要title, author, fileHash参数，owner使用msg.sender
     * 通过 CopyrightRegistry 合约调用 registerCopyright(string title, string author, string fileHash)
     *
     * @param title 版权标题
     * @param author 作者
     * @param fileHash 文件哈希（IPFS CID或本地哈希）
     * @return 包含版权ID和完整交易信息的Map
     */
    public java.util.Map<String, Object> registerCopyrightWithBlockchainInfo(String title, String author, String fileHash) {
        try {
            AssembleTransactionProcessor processor = getProcessor();

            List<Object> params = new ArrayList<>();
            params.add(title);
            params.add(author);
            params.add(fileHash);

            log.info("调用智能合约注册版权: title={}, author={}", title, author);

            TransactionResponse transactionResponse = processor.sendTransactionAndGetResponseByContractLoader(
                CONTRACT_NAME,
                contractConfig.getCopyrightAddress(),
                "registerCopyright",
                params
            );

            String message = transactionResponse.getReceiptMessages();
            if (!"Success".equals(message)) {
                throw new RuntimeException("Register copyright failed, message: " + message);
            }

            // 提取版权ID
            List<Object> returnObject = transactionResponse.getReturnObject();
            String copyrightId = returnObject != null && !returnObject.isEmpty()
                ? returnObject.get(0).toString()
                : String.valueOf(System.currentTimeMillis());

            // 提取完整的交易回执信息
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("copyrightId", copyrightId);
            result.put("blockchainInfo", extractBlockchainInfo(transactionResponse));

            log.info("版权注册成功: copyrightId={}, txHash={}",
                copyrightId,
                transactionResponse.getTransactionReceipt().getTransactionHash());

            return result;

        } catch (Exception e) {
            log.error("Failed to register copyright: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to register copyright: " + e.getMessage(), e);
        }
    }

    /**
     * 从交易回执中提取完整的区块链信息
     */
    private com.example.copyright.vo.BlockchainInfo extractBlockchainInfo(TransactionResponse response) {
        try {
            // FISCO BCOS SDK的receipt方法返回String，需要转换为Long
            Long blockNumber = null;
            Long gasUsed = null;
            Integer transactionIndex = null;

            try {
                String blockNumberStr = response.getTransactionReceipt().getBlockNumber();
                if (blockNumberStr != null && !blockNumberStr.isEmpty()) {
                    blockNumber = Long.parseLong(blockNumberStr);
                }
            } catch (Exception e) {
                log.warn("Failed to parse blockNumber: {}", e.getMessage());
            }

            try {
                String gasUsedStr = response.getTransactionReceipt().getGasUsed();
                if (gasUsedStr != null && !gasUsedStr.isEmpty()) {
                    gasUsed = Long.parseLong(gasUsedStr);
                }
            } catch (Exception e) {
                log.warn("Failed to parse gasUsed: {}", e.getMessage());
            }

            try {
                String transactionIndexStr = response.getTransactionReceipt().getTransactionIndex();
                if (transactionIndexStr != null && !transactionIndexStr.isEmpty()) {
                    transactionIndex = Integer.parseInt(transactionIndexStr);
                }
            } catch (Exception e) {
                log.warn("Failed to parse transactionIndex: {}", e.getMessage());
            }

            return com.example.copyright.vo.BlockchainInfo.builder()
                .transactionHash(response.getTransactionReceipt().getTransactionHash())
                .blockHash(response.getTransactionReceipt().getBlockHash())
                .blockNumber(blockNumber)
                .contractAddress(contractConfig.getCopyrightAddress())
                .transactionIndex(transactionIndex)
                .gasUsed(gasUsed)
                .gasPrice(null) // Gas price info not available in transaction receipt
                .status("Success")
                .from(response.getTransactionReceipt().getFrom())
                .to(response.getTransactionReceipt().getTo())
                .input(response.getTransactionReceipt().getInput())
                .methodName("registerCopyright(string,string,string)")
                .blockTimestamp(System.currentTimeMillis() / 1000)
                .build();

        } catch (Exception e) {
            log.warn("Failed to extract blockchain info: {}", e.getMessage());
            // 返回基础信息，包含所有字段（即使是null）
            return com.example.copyright.vo.BlockchainInfo.builder()
                .transactionHash(response.getTransactionReceipt().getTransactionHash())
                .blockHash(response.getTransactionReceipt().getBlockHash())
                .blockNumber(null)
                .contractAddress(contractConfig.getCopyrightAddress())
                .transactionIndex(null)
                .gasUsed(null)
                .gasPrice(null)
                .status("Success")
                .from(response.getTransactionReceipt().getFrom())
                .to(response.getTransactionReceipt().getTo())
                .methodName("registerCopyright")
                .blockTimestamp(System.currentTimeMillis() / 1000)
                .build();
        }
    }

    /**
     * 注册版权到区块链（简化版，仅返回版权ID）
     * 保持向后兼容
     */
    public String registerCopyright(String title, String author, String fileHash) {
        try {
            AssembleTransactionProcessor processor = getProcessor();

            List<Object> params = new ArrayList<>();
            params.add(title);
            params.add(author);
            params.add(fileHash);

            TransactionResponse transactionResponse = processor.sendTransactionAndGetResponseByContractLoader(
                CONTRACT_NAME,
                contractConfig.getCopyrightAddress(),
                "registerCopyright",
                params
            );

            String message = transactionResponse.getReceiptMessages();
            if ("Success".equals(message)) {
                // 从返回值中获取版权ID
                List<Object> returnObject = transactionResponse.getReturnObject();
                String copyrightId = returnObject != null && !returnObject.isEmpty()
                    ? returnObject.get(0).toString()
                    : String.valueOf(System.currentTimeMillis());

                log.info("Copyright registered on blockchain, copyrightId: {}, title: {}", copyrightId, title);
                return copyrightId;
            } else {
                throw new RuntimeException("Register copyright failed, message: " + message);
            }

        } catch (Exception e) {
            log.error("Failed to register copyright: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to register copyright: " + e.getMessage(), e);
        }
    }

    /**
     * 申请授权
     */
    public String applyForLicense(BigInteger copyrightId, String licensee, BigInteger startTime, BigInteger endTime, boolean isExclusive) {
        try {
            AssembleTransactionProcessor processor = getProcessor();

            List<Object> params = new ArrayList<>();
            params.add(copyrightId);
            params.add(licensee);
            params.add(startTime);
            params.add(endTime);
            params.add(isExclusive);

            TransactionResponse transactionResponse = processor.sendTransactionAndGetResponseByContractLoader(
                CONTRACT_NAME,
                contractConfig.getCopyrightAddress(),
                "applyForLicense",
                params
            );

            String message = transactionResponse.getReceiptMessages();
            if ("Success".equals(message)) {
                List<Object> returnObject = transactionResponse.getReturnObject();
                String licenseId = returnObject != null && !returnObject.isEmpty()
                    ? returnObject.get(0).toString()
                    : String.valueOf(System.currentTimeMillis());

                log.info("License applied on blockchain, licenseId: {}", licenseId);
                return licenseId;
            } else {
                throw new RuntimeException("Apply for license failed, message: " + message);
            }

        } catch (Exception e) {
            log.error("Failed to apply for license: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to apply for license: " + e.getMessage(), e);
        }
    }

    /**
     * 批准授权
     */
    public String approveLicense(BigInteger licenseId) {
        try {
            AssembleTransactionProcessor processor = getProcessor();

            List<Object> params = new ArrayList<>();
            params.add(licenseId);

            TransactionResponse transactionResponse = processor.sendTransactionAndGetResponseByContractLoader(
                CONTRACT_NAME,
                contractConfig.getCopyrightAddress(),
                "approveLicense",
                params
            );

            String message = transactionResponse.getReceiptMessages();
            if ("Success".equals(message)) {
                String txHash = transactionResponse.getTransactionReceipt().getTransactionHash();
                log.info("License approved on blockchain, txHash: {}", txHash);
                return txHash;
            } else {
                throw new RuntimeException("Approve license failed, message: " + message);
            }

        } catch (Exception e) {
            log.error("Failed to approve license: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to approve license: " + e.getMessage(), e);
        }
    }

    /**
     * 拒绝授权
     */
    public String rejectLicense(BigInteger licenseId) {
        try {
            AssembleTransactionProcessor processor = getProcessor();

            List<Object> params = new ArrayList<>();
            params.add(licenseId);

            TransactionResponse transactionResponse = processor.sendTransactionAndGetResponseByContractLoader(
                CONTRACT_NAME,
                contractConfig.getCopyrightAddress(),
                "rejectLicense",
                params
            );

            String message = transactionResponse.getReceiptMessages();
            if ("Success".equals(message)) {
                String txHash = transactionResponse.getTransactionReceipt().getTransactionHash();
                log.info("License rejected on blockchain, txHash: {}", txHash);
                return txHash;
            } else {
                throw new RuntimeException("Reject license failed, message: " + message);
            }

        } catch (Exception e) {
            log.error("Failed to reject license: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to reject license: " + e.getMessage(), e);
        }
    }

    /**
     * 激活授权
     */
    public String activateLicense(BigInteger licenseId) {
        try {
            AssembleTransactionProcessor processor = getProcessor();

            List<Object> params = new ArrayList<>();
            params.add(licenseId);

            TransactionResponse transactionResponse = processor.sendTransactionAndGetResponseByContractLoader(
                CONTRACT_NAME,
                contractConfig.getCopyrightAddress(),
                "activateLicense",
                params
            );

            String message = transactionResponse.getReceiptMessages();
            if ("Success".equals(message)) {
                String txHash = transactionResponse.getTransactionReceipt().getTransactionHash();
                log.info("License activated on blockchain, txHash: {}", txHash);
                return txHash;
            } else {
                throw new RuntimeException("Activate license failed, message: " + message);
            }

        } catch (Exception e) {
            log.error("Failed to activate license: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to activate license: " + e.getMessage(), e);
        }
    }

    /**
     * 撤销授权
     */
    public String revokeLicense(BigInteger licenseId) {
        try {
            AssembleTransactionProcessor processor = getProcessor();

            List<Object> params = new ArrayList<>();
            params.add(licenseId);

            TransactionResponse transactionResponse = processor.sendTransactionAndGetResponseByContractLoader(
                CONTRACT_NAME,
                contractConfig.getCopyrightAddress(),
                "revokeLicense",
                params
            );

            String message = transactionResponse.getReceiptMessages();
            if ("Success".equals(message)) {
                String txHash = transactionResponse.getTransactionReceipt().getTransactionHash();
                log.info("License revoked on blockchain, txHash: {}", txHash);
                return txHash;
            } else {
                throw new RuntimeException("Revoke license failed, message: " + message);
            }

        } catch (Exception e) {
            log.error("Failed to revoke license: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to revoke license: " + e.getMessage(), e);
        }
    }

    /**
     * 记录版税支付
     */
    public String recordRoyaltyPayment(BigInteger copyrightId, String payer, String payee, BigInteger amount, String currency) {
        try {
            AssembleTransactionProcessor processor = getProcessor();

            List<Object> params = new ArrayList<>();
            params.add(copyrightId);
            params.add(payer);
            params.add(payee);
            params.add(amount);
            params.add(currency);

            TransactionResponse transactionResponse = processor.sendTransactionAndGetResponseByContractLoader(
                CONTRACT_NAME,
                contractConfig.getCopyrightAddress(),
                "recordRoyaltyPayment",
                params
            );

            String message = transactionResponse.getReceiptMessages();
            if ("Success".equals(message)) {
                String txHash = transactionResponse.getTransactionReceipt().getTransactionHash();
                log.info("Royalty payment recorded on blockchain, txHash: {}", txHash);
                return txHash;
            } else {
                throw new RuntimeException("Record royalty payment failed, message: " + message);
            }

        } catch (Exception e) {
            log.error("Failed to record royalty payment: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to record royalty payment: " + e.getMessage(), e);
        }
    }

    /**
     * 转让版权
     */
    public String transferCopyright(BigInteger copyrightId, String newOwner) {
        try {
            AssembleTransactionProcessor processor = getProcessor();

            List<Object> params = new ArrayList<>();
            params.add(copyrightId);
            params.add(newOwner);

            TransactionResponse transactionResponse = processor.sendTransactionAndGetResponseByContractLoader(
                CONTRACT_NAME,
                contractConfig.getCopyrightAddress(),
                "transferCopyright",
                params
            );

            String message = transactionResponse.getReceiptMessages();
            if ("Success".equals(message)) {
                String txHash = transactionResponse.getTransactionReceipt().getTransactionHash();
                log.info("Copyright transferred on blockchain, txHash: {}", txHash);
                return txHash;
            } else {
                throw new RuntimeException("Transfer copyright failed, message: " + message);
            }

        } catch (Exception e) {
            log.error("Failed to transfer copyright: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to transfer copyright: " + e.getMessage(), e);
        }
    }

    /**
     * 从区块链查询版权信息
     */
    public List<Object> getCopyrightFromChain(BigInteger copyrightId) {
        try {
            AssembleTransactionProcessor processor = getProcessor();

            List<Object> params = new ArrayList<>();
            params.add(copyrightId);

            TransactionResponse transactionResponse = processor.sendTransactionAndGetResponseByContractLoader(
                CONTRACT_NAME,
                contractConfig.getCopyrightAddress(),
                "getCopyright",
                params
            );

            return transactionResponse.getReturnObject();

        } catch (Exception e) {
            log.error("Failed to get copyright from chain: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get copyright from chain: " + e.getMessage(), e);
        }
    }

    /**
     * 从区块链查询授权信息
     */
    public List<Object> getLicenseFromChain(BigInteger licenseId) {
        try {
            AssembleTransactionProcessor processor = getProcessor();

            List<Object> params = new ArrayList<>();
            params.add(licenseId);

            TransactionResponse transactionResponse = processor.sendTransactionAndGetResponseByContractLoader(
                CONTRACT_NAME,
                contractConfig.getCopyrightAddress(),
                "getLicense",
                params
            );

            return transactionResponse.getReturnObject();

        } catch (Exception e) {
            log.error("Failed to get license from chain: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get license from chain: " + e.getMessage(), e);
        }
    }

    /**
     * 设置版权审批者
     * 新增方法：允许版权拥有者指定特定人员来审批该版权的授权申请
     *
     * @param copyrightId 版权ID
     * @param approver 审批者地址
     * @return 交易哈希
     */
    public String setCopyrightApprover(BigInteger copyrightId, String approver) {
        try {
            AssembleTransactionProcessor processor = getProcessor();

            List<Object> params = new ArrayList<>();
            params.add(copyrightId);
            params.add(approver);

            TransactionResponse transactionResponse = processor.sendTransactionAndGetResponseByContractLoader(
                CONTRACT_NAME,
                contractConfig.getCopyrightAddress(),
                "setCopyrightApprover",
                params
            );

            String message = transactionResponse.getReceiptMessages();
            if ("Success".equals(message)) {
                String txHash = transactionResponse.getTransactionReceipt().getTransactionHash();
                log.info("Copyright approver set on blockchain, copyrightId: {}, approver: {}, txHash: {}", copyrightId, approver, txHash);
                return txHash;
            } else {
                throw new RuntimeException("Set copyright approver failed, message: " + message);
            }

        } catch (Exception e) {
            log.error("Failed to set copyright approver: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to set copyright approver: " + e.getMessage(), e);
        }
    }

    /**
     * 检查用户是否拥有有效的授权
     * 新增方法：查询指定用户是否对指定版权拥有有效授权
     *
     * @param copyrightId 版权ID
     * @param user 用户地址
     * @return 是否拥有有效授权
     */
    public boolean hasValidLicense(BigInteger copyrightId, String user) {
        try {
            AssembleTransactionProcessor processor = getProcessor();

            List<Object> params = new ArrayList<>();
            params.add(copyrightId);
            params.add(user);

            TransactionResponse transactionResponse = processor.sendTransactionAndGetResponseByContractLoader(
                CONTRACT_NAME,
                contractConfig.getCopyrightAddress(),
                "hasValidLicense",
                params
            );

            List<Object> returnObject = transactionResponse.getReturnObject();
            boolean hasValid = returnObject != null && !returnObject.isEmpty()
                ? Boolean.parseBoolean(returnObject.get(0).toString())
                : false;

            log.info("Checked valid license, copyrightId: {}, user: {}, hasValid: {}", copyrightId, user, hasValid);
            return hasValid;

        } catch (Exception e) {
            log.error("Failed to check valid license: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to check valid license: " + e.getMessage(), e);
        }
    }

    /**
     * 检查用户是否已注册
     * 新增方法：查询指定用户是否已在区块链上注册
     *
     * @param userAddress 用户地址
     * @return 是否已注册
     */
    public boolean isRegisteredUser(String userAddress) {
        try {
            AssembleTransactionProcessor processor = getProcessor();

            List<Object> params = new ArrayList<>();
            params.add(userAddress);

            TransactionResponse transactionResponse = processor.sendTransactionAndGetResponseByContractLoader(
                CONTRACT_NAME,
                contractConfig.getCopyrightAddress(),
                "isRegisteredUser",
                params
            );

            List<Object> returnObject = transactionResponse.getReturnObject();
            boolean isRegistered = returnObject != null && !returnObject.isEmpty()
                ? Boolean.parseBoolean(returnObject.get(0).toString())
                : false;

            log.debug("Checked user registration, user: {}, isRegistered: {}", userAddress, isRegistered);
            return isRegistered;

        } catch (Exception e) {
            log.error("Failed to check user registration: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to check user registration: " + e.getMessage(), e);
        }
    }

    /**
     * 检查地址是否拥有特定角色
     * 新增方法：查询指定地址是否拥有指定角色
     *
     * @param role keccak256哈希的角色标识
     * @param account 要检查的地址
     * @return 是否拥有该角色
     */
    public boolean hasRole(String role, String account) {
        try {
            AssembleTransactionProcessor processor = getProcessor();

            List<Object> params = new ArrayList<>();
            params.add(role);
            params.add(account);

            TransactionResponse transactionResponse = processor.sendTransactionAndGetResponseByContractLoader(
                CONTRACT_NAME,
                contractConfig.getCopyrightAddress(),
                "hasRole",
                params
            );

            List<Object> returnObject = transactionResponse.getReturnObject();
            boolean hasRole = returnObject != null && !returnObject.isEmpty()
                ? Boolean.parseBoolean(returnObject.get(0).toString())
                : false;

            log.debug("Checked role, role: {}, account: {}, hasRole: {}", role, account, hasRole);
            return hasRole;

        } catch (Exception e) {
            log.error("Failed to check role: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to check role: " + e.getMessage(), e);
        }
    }

    /**
     * 授予角色
     * 新增方法：系统管理员授予指定地址特定角色
     *
     * @param role keccak256哈希的角色标识
     * @param account 接收角色的地址
     * @return 交易哈希
     */
    public String grantRole(String role, String account) {
        try {
            AssembleTransactionProcessor processor = getProcessor();

            List<Object> params = new ArrayList<>();
            params.add(role);
            params.add(account);

            TransactionResponse transactionResponse = processor.sendTransactionAndGetResponseByContractLoader(
                CONTRACT_NAME,
                contractConfig.getCopyrightAddress(),
                "grantRole",
                params
            );

            String message = transactionResponse.getReceiptMessages();
            if ("Success".equals(message)) {
                String txHash = transactionResponse.getTransactionReceipt().getTransactionHash();
                log.info("Role granted on blockchain, role: {}, account: {}, txHash: {}", role, account, txHash);
                return txHash;
            } else {
                throw new RuntimeException("Grant role failed, message: " + message);
            }

        } catch (Exception e) {
            log.error("Failed to grant role: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to grant role: " + e.getMessage(), e);
        }
    }

    /**
     * 撤销角色
     * 新增方法：系统管理员撤销指定地址的特定角色
     *
     * @param role keccak256哈希的角色标识
     * @param account 要撤销角色的地址
     * @return 交易哈希
     */
    public String revokeRole(String role, String account) {
        try {
            AssembleTransactionProcessor processor = getProcessor();

            List<Object> params = new ArrayList<>();
            params.add(role);
            params.add(account);

            TransactionResponse transactionResponse = processor.sendTransactionAndGetResponseByContractLoader(
                CONTRACT_NAME,
                contractConfig.getCopyrightAddress(),
                "revokeRole",
                params
            );

            String message = transactionResponse.getReceiptMessages();
            if ("Success".equals(message)) {
                String txHash = transactionResponse.getTransactionReceipt().getTransactionHash();
                log.info("Role revoked on blockchain, role: {}, account: {}, txHash: {}", role, account, txHash);
                return txHash;
            } else {
                throw new RuntimeException("Revoke role failed, message: " + message);
            }

        } catch (Exception e) {
            log.error("Failed to revoke role: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to revoke role: " + e.getMessage(), e);
        }
    }
}
