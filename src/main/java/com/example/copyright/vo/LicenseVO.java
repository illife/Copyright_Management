package com.example.copyright.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 授权信息VO
 */
@Data
public class LicenseVO {

    private Long id;
    private String contractId;
    private Long copyrightId;
    private String copyrightTitle;
    private String applicantAddress;
    private String licenseeAddress;
    private Long startTime;
    private Long endTime;
    private Boolean isExclusive;
    private String status;
    private String transactionHash;
    private LocalDateTime createdTime;
}
