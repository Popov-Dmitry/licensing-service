package com.github.popovdmitry.licenseservice.repository;

import com.github.popovdmitry.licenseservice.model.License;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LicenseRepository extends JpaRepository<License, Long> {
}
