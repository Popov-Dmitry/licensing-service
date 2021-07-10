package com.github.popovdmitry.licenseservice.repository;

import com.github.popovdmitry.licenseservice.model.License;
import com.github.popovdmitry.licenseservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface LicenseRepository extends JpaRepository<License, Long> {
    List<License> findAllByUserId(Long userId);
    List<License> findAllByEndDateBefore(Date date);

    List<License> findAllByStartDate(Date date);
    List<License> findAllByEndDate(Date date);
    List<License> findAllByProduct(Product product);

    List<License> findAllByUserIdAndStartDate(Long userId, Date startDate);
    List<License> findAllByUserIdAndEndDate(Long userId, Date endDate);
    List<License> findAllByUserIdAndProduct(Long userId, Product product);

    List<License> findAllByStartDateAndEndDate(Date startDate, Date endDate);
    List<License> findAllByStartDateAndProduct(Date startDate, Product product);

    List<License> findAllByEndDateAndProduct(Date endDate, Product product);

    List<License> findAllByUserIdAndStartDateAndEndDate(Long userId, Date startDate, Date endDate);
    List<License> findAllByUserIdAndStartDateAndProduct(Long userId, Date startDate, Product product);
    List<License> findAllByUserIdAndEndDateAndProduct(Long userId, Date endDate, Product product);

    List<License> findAllByStartDateAndEndDateAndProduct(Date startDate, Date endDate, Product product);

    List<License> findAllByUserIdAndStartDateAndEndDateAndProduct(Long userId, Date startDate, Date endDate, Product product);

}