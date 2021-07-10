package com.github.popovdmitry.licenseservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturningLicenseFilterDTO {

    private String licenseStartDate;
    private String licenseEndDate;
    private Long userId;
    private Long licenseProductId;

}
