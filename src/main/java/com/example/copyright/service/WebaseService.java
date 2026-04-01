package com.example.copyright.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.copyright.config.ContractConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WeBASE-Front服务类
 * 通过HTTP API调用FISCO BCOS，不需要证书
 * 仅在WeBASE模式下加载
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "fisco.useWebase", havingValue = "true", matchIfMissing = false)
public class WebaseService {

    @Value("${fisco.url}")
    private String webaseUrl;

    @Value("${fisco.groupId}")
    private Integer groupId;

    @Value("${fisco.contractAddress.CopyrightRegistry_CONTRACT}")
    private String copyrightContractAddress;

    @Value("${fisco.contractAddress.RBAC_CONTRACT}")
    private String rbacContractAddress;

    @Value("${fisco.account.ownerAddress}")
    private String ownerAddress;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 调用WeBASE-Front API
     */
    private JSONObject callWebaseApi(String contractAddress, String contractName, String functionName, List<Object> params) {
        try {
            // URL 固定为 handle
            String url = webaseUrl;

            // 构建请求体（符合WeBASE-Front真实格式）
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("groupId", String.valueOf(groupId));
            requestBody.put("user", ownerAddress);
            requestBody.put("contractName", contractName);
            requestBody.put("contractAddress", contractAddress);
            requestBody.put("contractPath", "CopyrightManagement");
            requestBody.put("funcName", functionName);
            requestBody.put("cnsName", "");
            requestBody.put("funcId", 0);

            // 根据 functionName 构建不同的 inputs
            List<Map<String, Object>> inputs = new ArrayList<>();
            switch (functionName) {
                case "registerUser":
                    // RBAC.registerUser(address user, string username)
                    Map<String, Object> userParam = new HashMap<>();
                    userParam.put("internalType", "address");
                    userParam.put("name", "user");
                    userParam.put("type", "address");
                    userParam.put("value", params.get(0));
                    inputs.add(userParam);

                    Map<String, Object> usernameParam = new HashMap<>();
                    usernameParam.put("internalType", "string");
                    usernameParam.put("name", "username");
                    usernameParam.put("type", "string");
                    usernameParam.put("value", params.get(1));
                    inputs.add(usernameParam);
                    break;

                case "registerCopyright":
                    // CopyrightRegistry.registerCopyright(address owner, string title, string author, string fileHash)
                    Map<String, Object> ownerParam = new HashMap<>();
                    ownerParam.put("internalType", "address");
                    ownerParam.put("name", "owner");
                    ownerParam.put("type", "address");
                    ownerParam.put("value", params.get(0));
                    inputs.add(ownerParam);

                    Map<String, Object> titleParam = new HashMap<>();
                    titleParam.put("internalType", "string");
                    titleParam.put("name", "title");
                    titleParam.put("type", "string");
                    titleParam.put("value", params.get(1));
                    inputs.add(titleParam);

                    Map<String, Object> authorParam = new HashMap<>();
                    authorParam.put("internalType", "string");
                    authorParam.put("name", "author");
                    authorParam.put("type", "string");
                    authorParam.put("value", params.get(2));
                    inputs.add(authorParam);

                    Map<String, Object> fileHashParam = new HashMap<>();
                    fileHashParam.put("internalType", "string");
                    fileHashParam.put("name", "fileHash");
                    fileHashParam.put("type", "string");
                    fileHashParam.put("value", params.get(3));
                    inputs.add(fileHashParam);
                    break;

                // 可以继续添加其他函数的参数映射
                default:
                    // 默认处理：简化版本，直接传递值
                    for (Object param : params) {
                        Map<String, Object> defaultParam = new HashMap<>();
                        defaultParam.put("value", param);
                        inputs.add(defaultParam);
                    }
                    break;
            }

            requestBody.put("inputs", inputs);
            requestBody.put("outputs", new ArrayList<>());
            requestBody.put("stateMutability", "nonpayable");
            requestBody.put("type", "function");

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            log.info("Calling WeBASE API: {}, contract: {}, function: {}", url, contractName, functionName);
            log.debug("Request body: {}", requestBody);

            // 发送 POST 请求
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject result = JSON.parseObject(response.getBody());
                log.info("WeBASE API response: {}", result);
                return result;
            } else {
                log.error("WeBASE API error: {}", response.getStatusCode());
                throw new RuntimeException("WeBASE API call failed: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Failed to call WeBASE API: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to call WeBASE API: " + e.getMessage(), e);
        }
    }

    /**
     * 注册用户
     */
    public String registerUser(String userAddress, String username) {
        List<Object> params = new ArrayList<>();
        params.add(userAddress);
        params.add(username);

        JSONObject result = callWebaseApi(rbacContractAddress, "RBAC", "registerUser", params);

        if (result.getInteger("code") == 0) {
            return result.getString("transactionHash");
        } else {
            throw new RuntimeException("Register user failed: " + result.getString("message"));
        }
    }

    /**
     * 注册版权
     */
    public String registerCopyright(String owner, String title, String author, String fileHash) {
        List<Object> params = new ArrayList<>();
        params.add(owner);
        params.add(title);
        params.add(author);
        params.add(fileHash);

        JSONObject result = callWebaseApi(copyrightContractAddress, "CopyrightRegistry", "registerCopyright", params);

        if (result.getInteger("code") == 0) {
            Map<String, Object> data = result.getJSONObject("data");
            return String.valueOf(data.get("copyrightId"));
        } else {
            throw new RuntimeException("Register copyright failed: " + result.getString("message"));
        }
    }

    /**
     * 申请授权
     */
    public String applyForLicense(BigInteger copyrightId, String licensee, BigInteger startTime, BigInteger endTime, boolean isExclusive) {
        List<Object> params = new ArrayList<>();
        params.add(copyrightId.toString());
        params.add(licensee);
        params.add(startTime.toString());
        params.add(endTime.toString());
        params.add(isExclusive);

        JSONObject result = callWebaseApi(copyrightContractAddress, "CopyrightRegistry", "applyForLicense", params);

        if (result.getInteger("code") == 0) {
            Map<String, Object> data = result.getJSONObject("data");
            return String.valueOf(data.get("licenseId"));
        } else {
            throw new RuntimeException("Apply for license failed: " + result.getString("message"));
        }
    }

    /**
     * 批准授权
     */
    public String approveLicense(BigInteger licenseId) {
        List<Object> params = new ArrayList<>();
        params.add(licenseId.toString());

        JSONObject result = callWebaseApi(copyrightContractAddress, "CopyrightRegistry", "approveLicense", params);

        if (result.getInteger("code") == 0) {
            return result.getString("transactionHash");
        } else {
            throw new RuntimeException("Approve license failed: " + result.getString("message"));
        }
    }

    /**
     * 拒绝授权
     */
    public String rejectLicense(BigInteger licenseId) {
        List<Object> params = new ArrayList<>();
        params.add(licenseId.toString());

        JSONObject result = callWebaseApi(copyrightContractAddress, "CopyrightRegistry", "rejectLicense", params);

        if (result.getInteger("code") == 0) {
            return result.getString("transactionHash");
        } else {
            throw new RuntimeException("Reject license failed: " + result.getString("message"));
        }
    }

    /**
     * 激活授权
     */
    public String activateLicense(BigInteger licenseId) {
        List<Object> params = new ArrayList<>();
        params.add(licenseId.toString());

        JSONObject result = callWebaseApi(copyrightContractAddress, "CopyrightRegistry", "activateLicense", params);

        if (result.getInteger("code") == 0) {
            return result.getString("transactionHash");
        } else {
            throw new RuntimeException("Activate license failed: " + result.getString("message"));
        }
    }

    /**
     * 撤销授权
     */
    public String revokeLicense(BigInteger licenseId) {
        List<Object> params = new ArrayList<>();
        params.add(licenseId.toString());

        JSONObject result = callWebaseApi(copyrightContractAddress, "CopyrightRegistry", "revokeLicense", params);

        if (result.getInteger("code") == 0) {
            return result.getString("transactionHash");
        } else {
            throw new RuntimeException("Revoke license failed: " + result.getString("message"));
        }
    }

    /**
     * 记录版税支付
     */
    public String recordRoyaltyPayment(BigInteger copyrightId, String payer, String payee, BigInteger amount, String currency) {
        List<Object> params = new ArrayList<>();
        params.add(copyrightId.toString());
        params.add(payer);
        params.add(payee);
        params.add(amount.toString());
        params.add(currency);

        JSONObject result = callWebaseApi(copyrightContractAddress, "CopyrightRegistry", "recordRoyaltyPayment", params);

        if (result.getInteger("code") == 0) {
            return result.getString("transactionHash");
        } else {
            throw new RuntimeException("Record royalty payment failed: " + result.getString("message"));
        }
    }

    /**
     * 转让版权
     */
    public String transferCopyright(BigInteger copyrightId, String newOwner) {
        List<Object> params = new ArrayList<>();
        params.add(copyrightId.toString());
        params.add(newOwner);

        JSONObject result = callWebaseApi(copyrightContractAddress, "CopyrightRegistry", "transferCopyright", params);

        if (result.getInteger("code") == 0) {
            return result.getString("transactionHash");
        } else {
            throw new RuntimeException("Transfer copyright failed: " + result.getString("message"));
        }
    }

    /**
     * 查询版权
     */
    public Map<String, Object> getCopyright(BigInteger copyrightId) {
        List<Object> params = new ArrayList<>();
        params.add(copyrightId.toString());

        JSONObject result = callWebaseApi(copyrightContractAddress, "CopyrightRegistry", "getCopyright", params);

        if (result.getInteger("code") == 0) {
            return result.getJSONObject("data");
        } else {
            throw new RuntimeException("Get copyright failed: " + result.getString("message"));
        }
    }

    /**
     * 查询授权
     */
    public Map<String, Object> getLicense(BigInteger licenseId) {
        List<Object> params = new ArrayList<>();
        params.add(licenseId.toString());

        JSONObject result = callWebaseApi(copyrightContractAddress, "CopyrightRegistry", "getLicense", params);

        if (result.getInteger("code") == 0) {
            return result.getJSONObject("data");
        } else {
            throw new RuntimeException("Get license failed: " + result.getString("message"));
        }
    }
}
