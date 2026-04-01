package com.example.copyright.config;

import lombok.Data;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.config.model.ConfigProperty;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FISCO BCOS配置类
 * 仅在直连节点模式（需要证书）时加载
 * 参考真实项目实现
 */
@Data
@Configuration
public class FiscoBcosConfig {

    @Value("${fisco.nodeList}")
    private String nodeLists;

    @Value("${fisco.groupId}")
    private Integer groupId;

    @Value("${fisco.certPath:conf}")
    private String certPath;

    @Value("${fisco.account.accountFilePath:account/}")
    private String accountFilePath;

    /**
     * 创建配置属性
     */
    @Bean(name = "configProperty")
    public ConfigProperty defaultConfigProperty() throws ConfigException {
        ConfigProperty property = new ConfigProperty();

        // 配置cryptoMaterial（证书）
        Map<String, Object> cryptoMaterialMap = new HashMap<>();
        String[] possibilities = certPath.split(",");
        for (int i = 0; i < possibilities.length; i++) {
            try {
                cryptoMaterialMap.put("certPath", possibilities[i]);
                property.setCryptoMaterial(cryptoMaterialMap);

                // 配置network（网络节点）
                Map<String, Object> networkMap = new HashMap<>();
                String[] split = nodeLists.split(",");
                List<String> nodeList = Arrays.asList(split);
                networkMap.put("peers", nodeList);
                property.setNetwork(networkMap);

                // 配置threadPool（线程池）
                Map<String, Object> threadPoolMap = new HashMap<>();
                threadPoolMap.put("channelProcessorThreadSize", "16");
                threadPoolMap.put("receiptProcessorThreadSize", "16");
                threadPoolMap.put("maxBlockingQueueSize", "102400");
                property.setThreadPool(threadPoolMap);

                return property;
            } catch (Exception ex) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        throw new ConfigException("Failed to connect to any peer");
    }

    /**
     * 创建配置选项
     */
    @Bean(name = "configOption")
    public ConfigOption defaultConfigOption(ConfigProperty configProperty) throws ConfigException {
        return new ConfigOption(configProperty);
    }

    /**
     * 创建BCOS SDK实例
     */
    @Bean(name = "bcosSDK")
    public BcosSDK bcosSDK(ConfigOption configOption) {
        return new BcosSDK(configOption);
    }

    /**
     * 创建客户端
     */
    @Bean(name = "client")
    public Client getClient(BcosSDK bcosSDK) {
        // 为群组初始化client
        Client client = bcosSDK.getClient(groupId);
        return client;
    }

    /**
     * 创建密钥对（从客户端获取）
     */
    @Bean
    public CryptoKeyPair getCryptoKeyPair(Client client) {
        // 从客户端获取密钥对
        return client.getCryptoSuite().getCryptoKeyPair();
    }
}
