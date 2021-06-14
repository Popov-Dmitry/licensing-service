package com.github.popovdmitry.licenseservice.exception;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class LicenseExpiredException extends Exception {
    private String message;
}
