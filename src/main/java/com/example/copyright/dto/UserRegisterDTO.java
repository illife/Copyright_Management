package com.example.copyright.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 用户注册DTO
 */
@Data
public class UserRegisterDTO {

    @NotBlank(message = "用户地址不能为空")
    @Pattern(regexp = "^0x[a-fA-F0-9]{40}$", message = "无效的区块链地址格式")
    private String address;

    @NotBlank(message = "用户名不能为空")
    private String username;

    private String email;

    private String phone;
}
