package com.example.copyright.controller;

import com.example.copyright.dto.RoyaltyPaymentDTO;
import com.example.copyright.entity.RoyaltyPayment;
import com.example.copyright.service.RoyaltyService;
import com.example.copyright.vo.ResultVO;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 版税控制器
 */
@Api(tags = "版税管理")
@Slf4j
@RestController
@RequestMapping("/api/royalties")
public class RoyaltyController {

    @Autowired
    private RoyaltyService royaltyService;

    /**
     * 记录版税支付
     */
    @ApiOperation("记录版税支付")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dto", value = "版税支付信息", required = true, dataType = "RoyaltyPaymentDTO")
    })
    @PostMapping("/record")
    public ResultVO<RoyaltyPayment> record(@Validated @RequestBody RoyaltyPaymentDTO dto) {
        try {
            RoyaltyPayment payment = royaltyService.recordPayment(dto);
            return ResultVO.success("版税记录成功", payment);
        } catch (Exception e) {
            log.error("Failed to record royalty payment: {}", e.getMessage(), e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 根据版权ID查询版税记录
     */
    @ApiOperation("根据版权ID查询版税记录")
    @ApiImplicitParam(name = "copyrightId", value = "版权ID", required = true, dataType = "Long", paramType = "path")
    @GetMapping("/copyright/{copyrightId}")
    public ResultVO<List<RoyaltyPayment>> listByCopyrightId(@PathVariable Long copyrightId) {
        try {
            List<RoyaltyPayment> list = royaltyService.listByCopyrightId(copyrightId);
            return ResultVO.success(list);
        } catch (Exception e) {
            log.error("Failed to list royalty payments: {}", e.getMessage(), e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 根据支付方查询版税记录
     */
    @ApiOperation("根据支付方查询版税记录")
    @ApiImplicitParam(name = "payerAddress", value = "支付方地址", required = true, dataType = "String", paramType = "path")
    @GetMapping("/payer/{payerAddress}")
    public ResultVO<List<RoyaltyPayment>> listByPayer(@PathVariable String payerAddress) {
        try {
            List<RoyaltyPayment> list = royaltyService.listByPayer(payerAddress);
            return ResultVO.success(list);
        } catch (Exception e) {
            log.error("Failed to list royalty payments: {}", e.getMessage(), e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 根据收款方查询版税记录
     */
    @ApiOperation("根据收款方查询版税记录")
    @ApiImplicitParam(name = "payeeAddress", value = "收款方地址", required = true, dataType = "String", paramType = "path")
    @GetMapping("/payee/{payeeAddress}")
    public ResultVO<List<RoyaltyPayment>> listByPayee(@PathVariable String payeeAddress) {
        try {
            List<RoyaltyPayment> list = royaltyService.listByPayee(payeeAddress);
            return ResultVO.success(list);
        } catch (Exception e) {
            log.error("Failed to list royalty payments: {}", e.getMessage(), e);
            return ResultVO.error(e.getMessage());
        }
    }
}
