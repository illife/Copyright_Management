package com.example.copyright.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 授权申请DTO
 */
@Data
public class LicenseApplyDTO {

    @NotBlank(message = "版权ID不能为空")
    private String copyrightId;

    @NotBlank(message = "被授权方地址不能为空")
    @Pattern(regexp = "^0x[a-fA-F0-9]{40}$", message = "无效的区块链地址格式")
    private String licensee;

    // 别名，支持两种字段名
    public String getLicenseeAddress() {
        return licensee;
    }

    public void setLicenseeAddress(String licenseeAddress) {
        this.licensee = licenseeAddress;
    }

    // 开始时间（可选，默认为当前时间）
    private Long startTime;

    @NotNull(message = "结束时间不能为空")
    private Long endTime;

    @NotNull(message = "是否独占授权不能为空")
    private Boolean isExclusive;
}
