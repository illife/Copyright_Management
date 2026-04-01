package com.example.copyright.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

/**
 * 版税记录DTO
 */
@Data
public class RoyaltyPaymentDTO {

    @NotBlank(message = "版权ID不能为空")
    private String copyrightId;

    @NotBlank(message = "支付方地址不能为空")
    @Pattern(regexp = "^0x[a-fA-F0-9]{40}$", message = "无效的区块链地址格式")
    private String payer;

    @NotBlank(message = "收款方地址不能为空")
    @Pattern(regexp = "^0x[a-fA-F0-9]{40}$", message = "无效的区块链地址格式")
    private String payee;

    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.00000001", message = "金额必须大于0")
    private BigDecimal amount;

    @NotBlank(message = "货币类型不能为空")
    private String currency;
}
