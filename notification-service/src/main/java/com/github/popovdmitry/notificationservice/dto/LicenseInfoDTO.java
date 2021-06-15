package com.github.popovdmitry.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LicenseInfoDTO {

    private String productName;
    private Date endDate;
}
