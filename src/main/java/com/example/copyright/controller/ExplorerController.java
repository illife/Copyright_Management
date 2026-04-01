package com.example.copyright.controller;

import com.example.copyright.service.BlockchainExplorerService;
import com.example.copyright.vo.BlockVO;
import com.example.copyright.vo.ChainStatsVO;
import com.example.copyright.vo.TransactionVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 区块链浏览器控制器 - 别名路径
 * 同时支持 /api/explorer 和 /api/blockchain 两个路径
 */
@Api(tags = "区块链浏览器")
@Slf4j
@RestController
@RequestMapping("/api/explorer")
public class ExplorerController {

    @Autowired(required = false)
    private BlockchainExplorerService explorerService;

    /**
     * 获取链统计信息
     */
    @ApiOperation("获取链统计信息")
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getChainStats() {
        try {
            log.info("收到链统计信息请求");
            if (explorerService == null) {
                log.error("BlockchainExplorerService 未初始化");
                Map<String, Object> error = new HashMap<>();
                error.put("code", 503);
                error.put("msg", "BlockchainExplorerService not initialized");
                return ResponseEntity.status(503).body(error);
            }
            ChainStatsVO stats = explorerService.getChainStats();
            log.info("成功获取链统计信息: {}", stats.getLatestBlockNumber());
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "success");
            response.put("data", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get chain stats: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("msg", "Failed to get chain stats: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * 根据区块号获取区块信息
     */
    @ApiOperation("根据区块号获取区块信息")
    @GetMapping("/block/{blockNumber}")
    public ResponseEntity<Map<String, Object>> getBlockByNumber(
            @PathVariable String blockNumber) {
        try {
            BigInteger bn = new BigInteger(blockNumber);
            BlockVO block = explorerService.getBlockByNumber(bn);
            if (block == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("code", 404);
                error.put("msg", "Block not found");
                return ResponseEntity.status(404).body(error);
            }
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "success");
            response.put("data", block);
            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 400);
            error.put("msg", "Invalid block number format");
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("Failed to get block: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("msg", "Failed to get block: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * 根据区块哈希获取区块信息
     */
    @ApiOperation("根据区块哈希获取区块信息")
    @GetMapping("/block/hash/{blockHash}")
    public ResponseEntity<BlockVO> getBlockByHash(
            @PathVariable String blockHash) {
        try {
            BlockVO block = explorerService.getBlockByHash(blockHash);
            if (block == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(block);
        } catch (Exception e) {
            log.error("Failed to get block by hash: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 获取最新区块列表
     */
    @ApiOperation("获取最新区块列表")
    @GetMapping("/blocks/latest")
    public ResponseEntity<Map<String, Object>> getLatestBlocks(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            log.info("收到获取最新区块列表请求，limit: {}", limit);
            if (explorerService == null) {
                log.error("BlockchainExplorerService 未初始化");
                Map<String, Object> error = new HashMap<>();
                error.put("code", 503);
                error.put("msg", "BlockchainExplorerService not initialized");
                return ResponseEntity.status(503).body(error);
            }
            if (limit > 100) {
                limit = 100;
            }
            List<BlockVO> blocks = explorerService.getLatestBlocks(limit);
            log.info("成功获取 {} 个区块", blocks.size());
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "success");
            response.put("data", blocks);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get latest blocks: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("msg", "Failed to get latest blocks: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * 根据交易哈希获取交易信息
     */
    @ApiOperation("根据交易哈希获取交易信息")
    @GetMapping("/transaction/{transactionHash}")
    public ResponseEntity<TransactionVO> getTransactionByHash(
            @PathVariable String transactionHash) {
        try {
            TransactionVO transaction = explorerService.getTransactionByHash(transactionHash);
            if (transaction == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            log.error("Failed to get transaction: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 根据区块号获取交易列表
     */
    @ApiOperation("根据区块号获取交易列表")
    @GetMapping("/block/{blockNumber}/transactions")
    public ResponseEntity<Map<String, Object>> getTransactionsByBlock(
            @PathVariable String blockNumber) {
        try {
            BigInteger bn = new BigInteger(blockNumber);
            List<TransactionVO> transactions = explorerService.getTransactionsByBlock(bn);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "success");
            response.put("data", transactions);
            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 400);
            error.put("msg", "Invalid block number format");
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("Failed to get transactions by block: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("msg", "Failed to get transactions: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * 获取最新交易列表
     */
    @ApiOperation("获取最新交易列表")
    @GetMapping("/transactions/latest")
    public ResponseEntity<List<TransactionVO>> getLatestTransactions(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            if (limit > 100) {
                limit = 100;
            }
            List<TransactionVO> transactions = explorerService.getLatestTransactions(limit);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            log.error("Failed to get latest transactions: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 根据地址查询交易
     */
    @ApiOperation("根据地址查询交易")
    @GetMapping("/address/{address}/transactions")
    public ResponseEntity<List<TransactionVO>> getTransactionsByAddress(
            @PathVariable String address,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            if (limit > 100) {
                limit = 100;
            }
            List<TransactionVO> transactions = explorerService.getTransactionsByAddress(address, limit);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            log.error("Failed to get transactions by address: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 健康检查
     */
    @ApiOperation("区块链浏览器健康检查")
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        try {
            ChainStatsVO stats = explorerService.getChainStats();
            if (stats != null && stats.getLatestBlockNumber() != null) {
                return ResponseEntity.ok(new HealthResponse(
                        true,
                        "区块链浏览器运行正常",
                        stats.getLatestBlockNumber().toString()
                ));
            } else {
                return ResponseEntity.status(503).body(new HealthResponse(
                        false,
                        "无法连接到区块链节点",
                        null
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(503).body(new HealthResponse(
                    false,
                    "区块链浏览器异常: " + e.getMessage(),
                    null
            ));
        }
    }

    private static class HealthResponse {
        private boolean healthy;
        private String message;
        private String blockNumber;

        public HealthResponse(boolean healthy, String message, String blockNumber) {
            this.healthy = healthy;
            this.message = message;
            this.blockNumber = blockNumber;
        }

        public boolean isHealthy() {
            return healthy;
        }

        public String getMessage() {
            return message;
        }

        public String getBlockNumber() {
            return blockNumber;
        }
    }

    private static class ErrorResponse {
        private String error;
        private String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }

        public String getError() {
            return error;
        }

        public String getMessage() {
            return message;
        }
    }
}
