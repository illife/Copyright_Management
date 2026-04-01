package com.example.copyright.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * IPFS配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ipfs")
public class IpfsConfig {

    /**
     * IPFS网关地址
     */
    private String gateway = "https://ipfs.io/ipfs/";

    /**
     * IPFS API地址
     */
    private String apiUrl = "http://127.0.0.1:5001/api/v0";

    /**
     * 连接超时时间（毫秒）
     */
    private Integer timeout = 30000;
}
