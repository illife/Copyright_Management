package com.example.copyright.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器 - 用于验证后端是否正常运行
 */
@Slf4j
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/hello")
    public Map<String, Object> hello() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Hello from Copyright Management Backend!");
        result.put("status", "success");
        result.put("timestamp", LocalDateTime.now().toString());
        result.put("server", "Spring Boot " + org.springframework.boot.SpringBootVersion.getVersion());
        return result;
    }

    @GetMapping("/blockchain-status")
    public Map<String, Object> blockchainStatus() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "Blockchain Explorer Service is loaded");
        result.put("timestamp", LocalDateTime.now().toString());

        // 检查服务是否可用
        try {
            Class<?> explorerServiceClass = Class.forName("com.example.copyright.service.BlockchainExplorerService");
            result.put("serviceClassLoaded", true);
            result.put("serviceClassName", explorerServiceClass.getName());
        } catch (ClassNotFoundException e) {
            result.put("serviceClassLoaded", false);
            result.put("error", "BlockchainExplorerService not found");
        }

        return result;
    }
}
