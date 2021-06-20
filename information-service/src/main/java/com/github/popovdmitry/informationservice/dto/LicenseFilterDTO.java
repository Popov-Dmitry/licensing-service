package com.github.popovdmitry.informationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LicenseFilterDTO {

    private Date licenseStartDate;
    private Date licenseEndDate;
    private Long licenseProductId;

}
