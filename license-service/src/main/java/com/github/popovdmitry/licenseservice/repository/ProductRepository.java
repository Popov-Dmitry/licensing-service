package com.github.popovdmitry.licenseservice.repository;

import com.github.popovdmitry.licenseservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
