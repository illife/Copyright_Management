package com.example.copyright.controller;

import com.example.copyright.service.BlockchainAdminService;
import com.example.copyright.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员控制器
 * 用于权限管理和初始化
 */
@Api(tags = "管理员权限管理")
@Slf4j
@RestController
@RequestMapping("/api/admin")
@ConditionalOnProperty(name = "fisco.useWebase", havingValue = "false", matchIfMissing = false)
public class AdminController {

    @Autowired(required = false)
    private BlockchainAdminService blockchainAdminService;

    /**
     * 初始化管理员权限
     * 给管理员账户授予 USER_REGISTRAR_ROLE
     */
    @ApiOperation("初始化管理员权限")
    @PostMapping("/init-permissions")
    public ResultVO<String> initializePermissions() {
        try {
            blockchainAdminService.initializeAdminPermissions();
            return ResultVO.success("管理员权限初始化成功");
        } catch (Exception e) {
            log.error("Failed to initialize permissions: {}", e.getMessage(), e);
            return ResultVO.error("权限初始化失败: " + e.getMessage());
        }
    }

    /**
     * 授予角色
     */
    @ApiOperation("授予角色")
    @PostMapping("/grant-role")
    public ResultVO<String> grantRole(@RequestParam String role, @RequestParam String account) {
        try {
            String txHash = blockchainAdminService.grantRole(role, account);
            return ResultVO.success("角色授予成功", txHash);
        } catch (Exception e) {
            log.error("Failed to grant role: {}", e.getMessage(), e);
            return ResultVO.error("角色授予失败: " + e.getMessage());
        }
    }

    /**
     * 检查角色
     */
    @ApiOperation("检查角色")
    @GetMapping("/has-role")
    public ResultVO<Boolean> hasRole(@RequestParam String role, @RequestParam String account) {
        try {
            boolean hasRole = blockchainAdminService.hasRole(role, account);
            return ResultVO.success(hasRole);
        } catch (Exception e) {
            log.error("Failed to check role: {}", e.getMessage(), e);
            return ResultVO.error("角色检查失败: " + e.getMessage());
        }
    }

    /**
     * 检查管理员权限状态
     */
    @ApiOperation("检查管理员权限状态")
    @GetMapping("/check-permissions")
    public ResultVO<String> checkPermissions() {
        try {
            blockchainAdminService.checkPermissions();
            return ResultVO.success("权限检查完成，请查看日志");
        } catch (Exception e) {
            log.error("Failed to check permissions: {}", e.getMessage(), e);
            return ResultVO.error("权限检查失败: " + e.getMessage());
        }
    }
}
