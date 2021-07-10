package com.github.popovdmitry.informationservice.controller;

import com.github.popovdmitry.informationservice.dto.UserFilterDTO;
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
@RequestMapping("/user")
public class UserInformationController {

    private final InformationService informationService;

    @PostMapping("/list")
    public ResponseEntity<?> getLicenseList(@RequestBody UserFilterDTO userFilterDTO) {
        try {
            return ResponseEntity.ok(informationService.getUserInfo(userFilterDTO, false));
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/count")
    public ResponseEntity<?> getLicenseCount(@RequestBody UserFilterDTO userFilterDTO) {
        try {
            return ResponseEntity.ok(informationService.getUserInfo(userFilterDTO, true));
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
