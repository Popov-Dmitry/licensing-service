package com.github.popovdmitry.licenseservice.controller;

import com.github.popovdmitry.licenseservice.dto.LicenseDTO;
import com.github.popovdmitry.licenseservice.dto.NewLicenseDTO;
import com.github.popovdmitry.licenseservice.dto.ReturningLicenseDTO;
import com.github.popovdmitry.licenseservice.exception.LicenseExpiredException;
import com.github.popovdmitry.licenseservice.service.LicenseService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LicenseController {

    private final LicenseService licenseService;

    @PostMapping("/new")
    public ResponseEntity<?> newLicense(@RequestBody NewLicenseDTO newLicenseDTO) {
        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(licenseService.newLicense(newLicenseDTO));
        } catch (NotFoundException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{licenseId}")
    public ResponseEntity<?> getLicense(@PathVariable Long licenseId,
                                        @RequestBody LicenseDTO licenseDTO) {
        try {
            return ResponseEntity.ok(licenseService.findLicense(licenseId, licenseDTO));
        } catch (NotFoundException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/list")
    public ResponseEntity<List<Long>> getAllIds(@RequestBody LicenseDTO licenseDTO) {
        return ResponseEntity.ok(licenseService.findAllIds(licenseDTO));
    }

    @PostMapping("/check")
    public ResponseEntity<?> getLicense(@RequestBody ReturningLicenseDTO verifiableLicense) {
        try {
            licenseService.checkLicense(verifiableLicense);
            return ResponseEntity.ok().build();
        } catch (NotFoundException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("LICENSE_NOT_EXIST");
        }  catch (LicenseExpiredException e) {
            return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body("LICENSE_EXPIRED");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
