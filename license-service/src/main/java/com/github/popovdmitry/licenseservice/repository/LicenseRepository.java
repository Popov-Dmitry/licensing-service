package com.github.popovdmitry.licenseservice.repository;

import com.github.popovdmitry.licenseservice.model.License;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface LicenseRepository extends JpaRepository<License, Long> {
    List<License> findAllByUserId(Long userId);
    List<License> findAllByEndDateAfter(Date date);
}
