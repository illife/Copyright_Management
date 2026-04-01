package com.example.copyright;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.security.Security;

/**
 * 版权管理平台启动类
 */
@SpringBootApplication
@MapperScan("com.example.copyright.mapper")
@EnableAsync
public class CopyrightManagementApplication {

    public static void main(String[] args) {
        // 注册 Bouncy Castle 安全提供者，支持 secp256k1 曲线
        Security.addProvider(new BouncyCastleProvider());

        SpringApplication.run(CopyrightManagementApplication.class, args);
        System.out.println("\n" +
            "            =======================================================\n" +
            "               ____  _____  _    _    _    ____  _____   __\n" +
            "              / __ \\|  __ \\| |  | |  | |  |  _ \\|  __ \\ / _|\n" +
            "             | |  | | |__) | |  | |  | |  | |_) | |  | | |\n" +
            "             | |  | |  ___/| |  | |  | |  |  _ <| |  | | |\n" +
            "             | |__| | |    | |__| |__| |  | |_) | |__| | |\n" +
            "              \\____/|_|    |____|____/   |____/|_____/|_|\n" +
            "\n" +
            "                       Copyright Management Platform\n" +
            "                         Powered by FISCO BCOS\n" +
            "            =======================================================\n");
    }
}
