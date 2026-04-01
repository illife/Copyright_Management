package com.example.copyright.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.copyright.dto.LicenseApplyDTO;
import com.example.copyright.entity.License;
import com.example.copyright.service.LicenseService;
import com.example.copyright.utils.JwtUtil;
import com.example.copyright.vo.LicenseVO;
import com.example.copyright.vo.ResultVO;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 授权控制器
 */
@Api(tags = "授权管理")
@Slf4j
@RestController
@RequestMapping("/api/licenses")
public class LicenseController {

    @Autowired
    private LicenseService licenseService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 申请授权
     */
    @ApiOperation("申请授权")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dto", value = "授权申请信息", required = true, dataType = "LicenseApplyDTO")
    })
    @PostMapping("/apply")
    public ResultVO<License> apply(@Validated @RequestBody LicenseApplyDTO dto) {
        try {
            License license = licenseService.apply(dto);
            return ResultVO.success("授权申请成功", license);
        } catch (Exception e) {
            log.error("Failed to apply for license: {}", e.getMessage(), e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 批准授权
     */
    @ApiOperation("批准授权")
    @PutMapping("/{id}/approve")
    public ResultVO<Void> approve(@PathVariable Long id) {
        try {
            licenseService.approve(id);
            return ResultVO.success();
        } catch (Exception e) {
            log.error("Failed to approve license: {}", e.getMessage(), e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 批准授权（请求体版本）- 备用接口
     */
    @ApiOperation("批准授权（请求体版）")
    @PutMapping("/approve")
    public ResultVO<Void> approveWithBody(@RequestBody Map<String, Object> request) {
        try {
            Long id = Long.valueOf(request.get("licenseId").toString());
            licenseService.approve(id);
            return ResultVO.success();
        } catch (Exception e) {
            log.error("Failed to approve license: {}", e.getMessage(), e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 拒绝授权
     */
    @ApiOperation("拒绝授权")
    @PutMapping("/{id}/reject")
    public ResultVO<Void> reject(@PathVariable Long id) {
        try {
            licenseService.reject(id);
            return ResultVO.success();
        } catch (Exception e) {
            log.error("Failed to reject license: {}", e.getMessage(), e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 拒绝授权（请求体版本）- 备用接口
     */
    @ApiOperation("拒绝授权（请求体版）")
    @PutMapping("/reject")
    public ResultVO<Void> rejectWithBody(@RequestBody Map<String, Object> request) {
        try {
            Long id = Long.valueOf(request.get("licenseId").toString());
            licenseService.reject(id);
            return ResultVO.success();
        } catch (Exception e) {
            log.error("Failed to reject license: {}", e.getMessage(), e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 激活授权
     */
    @ApiOperation("激活授权")
    @ApiImplicitParam(name = "id", value = "授权ID", required = true, dataType = "Long", paramType = "path")
    @PutMapping("/{id}/activate")
    public ResultVO<Void> activate(@PathVariable Long id, @RequestHeader("Authorization") String authorization) {
        try {
            // 从JWT令牌中提取用户地址
            String userAddress = jwtUtil.getAddressFromToken(authorization);
            if (userAddress == null) {
                return ResultVO.error("无法获取用户信息");
            }

            licenseService.activate(id, userAddress);
            return ResultVO.success();
        } catch (Exception e) {
            log.error("Failed to activate license: {}", e.getMessage(), e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 撤销授权
     */
    @ApiOperation("撤销授权")
    @ApiImplicitParam(name = "id", value = "授权ID", required = true, dataType = "Long", paramType = "path")
    @PutMapping("/{id}/revoke")
    public ResultVO<Void> revoke(@PathVariable Long id) {
        try {
            licenseService.revoke(id);
            return ResultVO.success();
        } catch (Exception e) {
            log.error("Failed to revoke license: {}", e.getMessage(), e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 根据ID查询授权
     */
    @ApiOperation("根据ID查询授权")
    @ApiImplicitParam(name = "id", value = "授权ID", required = true, dataType = "Long", paramType = "path")
    @GetMapping("/{id}")
    public ResultVO<LicenseVO> getById(@PathVariable Long id) {
        try {
            LicenseVO vo = licenseService.getById(id);
            if (vo == null) {
                return ResultVO.error("授权不存在");
            }
            return ResultVO.success(vo);
        } catch (Exception e) {
            log.error("Failed to get license: {}", e.getMessage(), e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 根据版权ID查询授权列表
     */
    @ApiOperation("根据版权ID查询授权列表")
    @ApiImplicitParam(name = "copyrightId", value = "版权ID", required = true, dataType = "Long", paramType = "path")
    @GetMapping("/copyright/{copyrightId}")
    public ResultVO<List<LicenseVO>> listByCopyrightId(@PathVariable Long copyrightId) {
        try {
            List<LicenseVO> list = licenseService.listByCopyrightId(copyrightId);
            return ResultVO.success(list);
        } catch (Exception e) {
            log.error("Failed to list licenses: {}", e.getMessage(), e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 根据被授权方查询授权列表
     */
    @ApiOperation("根据被授权方查询授权列表")
    @ApiImplicitParam(name = "licenseeAddress", value = "被授权方地址", required = true, dataType = "String", paramType = "path")
    @GetMapping("/licensee/{licenseeAddress}")
    public ResultVO<List<LicenseVO>> listByLicensee(@PathVariable String licenseeAddress) {
        try {
            List<LicenseVO> list = licenseService.listByLicensee(licenseeAddress);
            return ResultVO.success(list);
        } catch (Exception e) {
            log.error("Failed to list licenses: {}", e.getMessage(), e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 查询待审批的授权
     */
    @ApiOperation("查询待审批的授权")
    @GetMapping("/pending")
    public ResultVO<List<LicenseVO>> listPending() {
        try {
            List<LicenseVO> list = licenseService.listPending();
            return ResultVO.success(list);
        } catch (Exception e) {
            log.error("Failed to list pending licenses: {}", e.getMessage(), e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 分页查询所有授权
     */
    @ApiOperation("分页查询所有授权")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", dataType = "int", paramType = "query")
    })
    @GetMapping("/page")
    public ResultVO<Page<LicenseVO>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            Page<LicenseVO> page = licenseService.page(pageNum, pageSize);
            return ResultVO.success(page);
        } catch (Exception e) {
            log.error("Failed to page licenses: {}", e.getMessage(), e);
            return ResultVO.error(e.getMessage());
        }
    }
}
