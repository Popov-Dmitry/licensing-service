package com.github.popovdmitry.licenseservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KafkaLicenseInfoDTO {

    private String productName;
    private Date endDate;
}
