package com.github.popovdmitry.licenseservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.security.KeyPair;
import java.sql.Date;

@Entity
@Table(name = "licenses")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class License {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "keys")
    private KeyPair keyPair;
}
