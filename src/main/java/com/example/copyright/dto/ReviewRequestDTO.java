package com.example.copyright.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 审批请求 DTO
 */
@Data
@ApiModel("审批请求")
public class ReviewRequestDTO {

    @ApiModelProperty(value = "申请ID", required = true)
    @NotNull(message = "申请ID不能为空")
    private Long id;

    @ApiModelProperty(value = "审批意见", required = true)
    @NotBlank(message = "审批意见不能为空")
    private String comment;

    @ApiModelProperty(value = "是否通过", required = true)
    @NotNull(message = "审批结果不能为空")
    private Boolean approved;
}
