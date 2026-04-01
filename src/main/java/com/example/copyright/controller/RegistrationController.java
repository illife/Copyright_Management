package com.example.copyright.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.copyright.dto.RegistrationRequestDTO;
import com.example.copyright.dto.ReviewRequestDTO;
import com.example.copyright.entity.UserRegistration;
import com.example.copyright.service.UserRegistrationService;
import com.example.copyright.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户注册申请控制器
 */
@Api(tags = "用户注册申请管理")
@Slf4j
@RestController
@RequestMapping("/api/registration")
public class RegistrationController {

    @Autowired
    private UserRegistrationService userRegistrationService;

    /**
     * 提交注册申请
     */
    @ApiOperation("提交注册申请")
    @PostMapping("/submit")
    public ResultVO<UserRegistration> submitRegistration(@RequestBody RegistrationRequestDTO request) {
        try {
            UserRegistration registration = new UserRegistration();
            BeanUtils.copyProperties(request, registration);

            UserRegistration result = userRegistrationService.submitRegistration(registration);
            return ResultVO.success("注册申请提交成功，请等待管理员审核", result);
        } catch (Exception e) {
            log.error("提交注册申请失败: {}", e.getMessage(), e);
            return ResultVO.error("提交失败: " + e.getMessage());
        }
    }

    /**
     * 审批注册申请
     */
    @ApiOperation("审批注册申请")
    @PostMapping("/review")
    public ResultVO<UserRegistration> reviewRegistration(@RequestBody ReviewRequestDTO request) {
        try {
            // TODO: 从 JWT 中获取审核人信息
            String reviewer = "admin";

            UserRegistration result;
            if (request.getApproved()) {
                result = userRegistrationService.approveRegistration(
                        request.getId(), reviewer, request.getComment());
            } else {
                result = userRegistrationService.rejectRegistration(
                        request.getId(), reviewer, request.getComment());
            }

            String message = request.getApproved() ? "审批通过" : "已拒绝";
            return ResultVO.success(message, result);
        } catch (Exception e) {
            log.error("审批失败: {}", e.getMessage(), e);
            return ResultVO.error("审批失败: " + e.getMessage());
        }
    }

    /**
     * 查询申请详情
     */
    @ApiOperation("查询申请详情")
    @GetMapping("/{id}")
    public ResultVO<UserRegistration> getRegistration(@PathVariable Long id) {
        try {
            UserRegistration registration = userRegistrationService.getRegistrationById(id);
            if (registration == null) {
                return ResultVO.error("申请不存在");
            }
            return ResultVO.success(registration);
        } catch (Exception e) {
            log.error("查询申请详情失败: {}", e.getMessage(), e);
            return ResultVO.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 查询我的申请状态
     */
    @ApiOperation("查询我的申请状态")
    @GetMapping("/my-status")
    public ResultVO<UserRegistration> getMyStatus(@RequestParam String userAddress) {
        try {
            UserRegistration registration = userRegistrationService.getRegistrationByUserAddress(userAddress);
            return ResultVO.success(registration);
        } catch (Exception e) {
            log.error("查询申请状态失败: {}", e.getMessage(), e);
            return ResultVO.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询注册申请列表
     */
    @ApiOperation("分页查询注册申请列表")
    @GetMapping("/list")
    public ResultVO<IPage<UserRegistration>> getRegistrationList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") int page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") int size,
            @ApiParam("状态筛选") @RequestParam(required = false) String status) {
        try {
            IPage<UserRegistration> result = userRegistrationService.getRegistrationList(page, size, status);
            return ResultVO.success(result);
        } catch (Exception e) {
            log.error("查询申请列表失败: {}", e.getMessage(), e);
            return ResultVO.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取待审核申请数量
     */
    @ApiOperation("获取待审核申请数量")
    @GetMapping("/pending-count")
    public ResultVO<Long> getPendingCount() {
        try {
            long count = userRegistrationService.getPendingCount();
            return ResultVO.success(count);
        } catch (Exception e) {
            log.error("获取待审核数量失败: {}", e.getMessage(), e);
            return ResultVO.error("查询失败: " + e.getMessage());
        }
    }
}
