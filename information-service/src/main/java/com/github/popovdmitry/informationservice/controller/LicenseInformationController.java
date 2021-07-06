package com.github.popovdmitry.informationservice.controller;

import com.github.popovdmitry.informationservice.dto.LicenseFilterDTO;
import com.github.popovdmitry.informationservice.service.InformationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.concurrent.ExecutionException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/license")
public class LicenseInformationController {

    private final InformationService informationService;

    @PostMapping("/list")
    public ResponseEntity<?> getLicenseList(@RequestBody LicenseFilterDTO licenseFilterDTO) {
        try {
            return ResponseEntity.ok(informationService.getLicenseInfo(licenseFilterDTO, false));
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/count")
    public ResponseEntity<?> getLicenseCount(@RequestBody LicenseFilterDTO licenseFilterDTO) {
        try {
            return ResponseEntity.ok(informationService.getLicenseInfo(licenseFilterDTO, true));
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
