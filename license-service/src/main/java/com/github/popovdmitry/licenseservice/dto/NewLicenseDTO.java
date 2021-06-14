package com.github.popovdmitry.licenseservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewLicenseDTO {

    private Long userId;
    private String productName;
}
