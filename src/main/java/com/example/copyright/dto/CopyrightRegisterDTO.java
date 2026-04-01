package com.example.copyright.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 版权注册DTO
 * 新合约版本：删除owner字段，owner从JWT token获取
 */
@Data
public class CopyrightRegisterDTO {

    // ❌ 删除owner字段 - 新合约版本从JWT token获取owner
    // @NotBlank(message = "所有者地址不能为空")
    // @Pattern(regexp = "^0x[a-fA-F0-9]{40}$", message = "无效的区块链地址格式")
    // private String owner;

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200个字符")
    private String title;

    @NotBlank(message = "作者不能为空")
    @Size(max = 100, message = "作者长度不能超过100个字符")
    private String author;

    @NotBlank(message = "文件哈希不能为空")
    @Size(min = 46, message = "无效的IPFS哈希格式")
    private String fileHash;

    @Size(max = 1000, message = "描述长度不能超过1000个字符")
    private String description;
}
