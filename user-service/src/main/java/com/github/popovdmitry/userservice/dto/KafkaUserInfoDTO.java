package com.github.popovdmitry.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KafkaUserInfoDTO {

    private String email;
    private String userName;
}
