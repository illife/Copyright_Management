package com.example.copyright.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 合约配置类
 */
@Data
@Configuration
public class ContractConfig {

    @Value("${fisco.contractAddress.RBAC_CONTRACT}")
    private String rbacAddress;

    @Value("${fisco.contractAddress.CopyrightRegistry_CONTRACT}")
    private String copyrightAddress;
}
