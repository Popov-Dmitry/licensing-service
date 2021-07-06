package com.github.popovdmitry.informationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFilterDTO {

    private Long userId;
    private int userTypeId;
    private String userName;

}
