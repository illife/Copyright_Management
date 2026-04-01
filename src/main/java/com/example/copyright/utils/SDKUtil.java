package com.example.copyright.utils;

import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.transaction.manager.AssembleTransactionProcessor;
import org.fisco.bcos.sdk.transaction.manager.TransactionProcessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * FISCO BCOS SDK工具类
 * 参考真实项目实现
 */
@Slf4j
@Component
public class SDKUtil {

    @Value("${fisco.account.accountFilePaths:account/}")
    private String accountFilePaths;

    @Autowired
    private Client client;

    /**
     * 创建交易处理器
     * 完全模仿 backend 真实项目实现
     */
    public AssembleTransactionProcessor sdk(String contractName, String abi, String bin) throws Exception {
        String[] possibilities = accountFilePaths.split(",");
        for (int i = 0; i < possibilities.length; i++) {
            try {
                String filePath = possibilities[i] + "key.txt";
                String s = readFileByPath(filePath);
                if (s == null) {
                    continue;
                }

                CryptoSuite cryptoSuite = new CryptoSuite(0);
                CryptoKeyPair keyPair = cryptoSuite.getKeyPairFactory().createKeyPair(s);

                log.info("===============");
                log.info("Loaded key from: {}", filePath);
                log.info("Key address: {}", keyPair.getAddress());

                return TransactionProcessorFactory.createAssembleTransactionProcessor(
                    client, keyPair, contractName, abi, bin
                );
            } catch (Exception ex) {
                log.error("Failed to load account: {}", ex.getMessage());
                Thread.sleep(1000);
            }
        }
        throw new IOException("Failed to load account from any configured path");
    }

    private static String readFileByPath(String fullFilePath) throws IOException {
        return IOUtil.readResourceAsString(fullFilePath);
    }
}
