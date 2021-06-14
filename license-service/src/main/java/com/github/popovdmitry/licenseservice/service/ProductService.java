package com.github.popovdmitry.licenseservice.service;

import com.github.popovdmitry.licenseservice.model.Product;
import com.github.popovdmitry.licenseservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public Optional<Product> getProduct(String name) {
        return productRepository.findByName(name);
    }
}
