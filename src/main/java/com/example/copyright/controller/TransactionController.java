package com.example.copyright.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.copyright.dto.TransactionRecordQueryDTO;
import com.example.copyright.service.TransactionService;
import com.example.copyright.vo.TransactionRecordVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 交易记录控制器
 * 提供交易记录查询API
 */
@Api(tags = "交易记录")
@Slf4j
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    /**
     * 获取交易统计信息
     */
    @ApiOperation("获取交易统计信息")
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getTransactionStats() {
        try {
            Map<String, Object> stats = transactionService.getTransactionStats();

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取成功");
            response.put("data", stats);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get transaction stats: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", "获取交易统计失败: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * 获取交易记录列表（分页）
     */
    @ApiOperation("获取交易记录列表")
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getTransactionList(
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "12") Integer pageSize,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String fromAddress,
            @RequestParam(required = false) String toAddress,
            @RequestParam(required = false) Long blockNumber,
            @RequestParam(required = false) String transactionHash
    ) {
        try {
            // 构建查询DTO
            TransactionRecordQueryDTO dto = new TransactionRecordQueryDTO();
            dto.setPageNum(pageNum);
            dto.setPageSize(pageSize);
            dto.setType(type);
            dto.setStatus(status);
            dto.setFromAddress(fromAddress);
            dto.setToAddress(toAddress);
            dto.setBlockNumber(blockNumber);
            dto.setTransactionHash(transactionHash);

            // 查询交易记录
            Page<TransactionRecordVO> page = transactionService.getTransactionList(dto);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取成功");

            Map<String, Object> data = new HashMap<>();
            data.put("list", page.getRecords());
            data.put("total", page.getTotal());
            data.put("pageNum", page.getCurrent());
            data.put("pageSize", page.getSize());
            data.put("pages", page.getPages());

            response.put("data", data);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get transaction list: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", "获取交易列表失败: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * 导出交易记录
     */
    @ApiOperation("导出交易记录")
    @GetMapping("/export")
    public ResponseEntity<Map<String, Object>> exportTransactions(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status
    ) {
        try {
            // TODO: 实现导出功能（Excel/CSV）
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "导出功能开发中");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to export transactions: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", "导出失败: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}
