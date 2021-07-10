package com.github.popovdmitry.licenseservice.service;

import com.github.popovdmitry.licenseservice.dto.*;
import com.github.popovdmitry.licenseservice.exception.LicenseExpiredException;
import com.github.popovdmitry.licenseservice.model.License;
import com.github.popovdmitry.licenseservice.model.Product;
import com.github.popovdmitry.licenseservice.repository.LicenseRepository;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class LicenseService {

    private final LicenseRepository licenseRepository;
    private final ProductService productService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${license.duration-millis}")
    private long licenseDuration;

    @Value("${license.days-to-expire}")
    private long daysToExpire;

    public LicenseService(LicenseRepository licenseRepository, ProductService productService,
                          @Qualifier("defaultKafkaTemplate") KafkaTemplate<String, Object> kafkaTemplate) {
        this.licenseRepository = licenseRepository;
        this.productService = productService;
        this.kafkaTemplate = kafkaTemplate;
    }

    public ReturningLicenseDTO newLicense(NewLicenseDTO newLicenseDTO) throws NotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Optional<Product> product = productService.getProduct(newLicenseDTO.getProductName());
        if (product.isPresent()) {
            License license = new License();
            license.setStartDate(new Date(new java.util.Date().getTime()));
            license.setEndDate(new Date(license.getStartDate().getTime() + licenseDuration));
            license.setUserId(newLicenseDTO.getUserId());
            license.setProduct(product.get());
            license.setKeyPair(KeyPairGenerator.getInstance("RSA").generateKeyPair());

            licenseRepository.save(license);
            return ReturningLicenseDTO.fromLicense(license);
        }
        throw new NotFoundException(String.format("Product \"%s is not found \"", newLicenseDTO.getProductName()));
    }

    public ReturningLicenseDTO findLicense(Long licenseId, LicenseDTO licenseDTO) throws NotFoundException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Optional<License> license = licenseRepository.findById(licenseId);
        if (license.isPresent() && license.get().getUserId().equals(licenseDTO.getUserId())) {
            return ReturningLicenseDTO.fromLicense(license.get());
        }
        throw new NotFoundException("License is not found");
    }

    public List<Long> findAllIds(LicenseDTO licenseDTO) {
        return licenseRepository
                .findAllByUserId(
                        licenseDTO
                                .getUserId()
                ).stream()
                .map(License::getId)
                .toList();
    }

    public void checkLicense(ReturningLicenseDTO verifiableLicense) throws NotFoundException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, LicenseExpiredException {
        License license = licenseRepository.findById(verifiableLicense.getId()).orElseThrow(
                () -> new NotFoundException("License is not found"));
        if (verifiableLicense.getStartDate().equals(license.getStartDate().toString()) &&
                verifiableLicense.getEndDate().equals(license.getEndDate().toString()) &&
                verifiableLicense.getUserId().equals(license.getUserId()) &&
                verifiableLicense.getProductName().equals(license.getProduct().getName())) {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, license.getKeyPair().getPrivate());

            byte[] bytes = cipher.doFinal(verifiableLicense.getPublicKey().toByteArray());
            ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);
            byteBuffer.put(bytes);
            byteBuffer.flip();

            if (license.getUserId().equals(byteBuffer.getLong())) {
                if (license.getEndDate().after(new java.util.Date())) {
                    return;
                }
                throw new LicenseExpiredException("License was expired");
            }
        }
        throw new NotFoundException("License is not found");
    }


    @Scheduled(cron = "0 27 22 * * ?")
    private void sendLicenseInfo() {
        log.info("sendLicenseInfo");
        List<License> licenses = licenseRepository.findAllByEndDateBefore(
                new Date(
                        new java.util.Date().getTime() + daysToExpire * 24 * 60 * 60 * 1000
                ));

        licenses.forEach(license -> kafkaTemplate.send(
                "licensesInfoTopic", license.getUserId().toString(), new KafkaLicenseInfoDTO(
                        license.getProduct().getName(), license.getEndDate()
                )));

    }

    public List<ReturningLicenseFilterDTO> findAllByFilter(LicenseFilterDTO licenseFilterDTO) {
        if (licenseFilterDTO.getLicenseStartDate() == null) {
            if (licenseFilterDTO.getLicenseEndDate() == null) {
                if (licenseFilterDTO.getLicenseProductId() == null) {
                    if (licenseFilterDTO.getUserId() == null) {
                        return new ArrayList<>();
                    }
                    else {
                        return toReturningLicenseFilterDTOList(
                                licenseRepository.findAllByUserId(licenseFilterDTO.getUserId()));
                    }
                }
                else {
                    Optional<Product> product = productService.getProduct(licenseFilterDTO.getLicenseProductId());
                    if (licenseFilterDTO.getUserId() == null) {
                        if (product.isPresent()) {
                            return toReturningLicenseFilterDTOList(
                                    licenseRepository.findAllByProduct(product.get()));
                        }
                        return new ArrayList<>();
                    }
                    else {
                        if (product.isPresent()) {
                            return toReturningLicenseFilterDTOList(
                                    licenseRepository.findAllByUserIdAndProduct(
                                            licenseFilterDTO.getUserId(),
                                            product.get()));
                        }
                        return new ArrayList<>();
                    }
                }
            }
            else {
                if (licenseFilterDTO.getLicenseProductId() == null) {
                    if (licenseFilterDTO.getUserId() == null) {
                        return toReturningLicenseFilterDTOList(
                                licenseRepository.findAllByEndDate(licenseFilterDTO.getLicenseEndDate()));
                    }
                    else {
                        return toReturningLicenseFilterDTOList(
                                licenseRepository.findAllByUserIdAndEndDate(
                                        licenseFilterDTO.getUserId(),
                                        licenseFilterDTO.getLicenseEndDate()));
                    }
                }
                else {
                    Optional<Product> product = productService.getProduct(licenseFilterDTO.getLicenseProductId());
                    if (licenseFilterDTO.getUserId() == null) {
                        if (product.isPresent()) {
                            return toReturningLicenseFilterDTOList(
                                    licenseRepository.findAllByEndDateAndProduct(
                                            licenseFilterDTO.getLicenseEndDate(),
                                            product.get()));
                        }
                        return new ArrayList<>();
                    }
                    else {
                        if (product.isPresent()) {
                            return toReturningLicenseFilterDTOList(
                                    licenseRepository.findAllByUserIdAndEndDateAndProduct(
                                            licenseFilterDTO.getUserId(),
                                            licenseFilterDTO.getLicenseEndDate(),
                                            product.get()));
                        }
                        return new ArrayList<>();
                    }
                }
            }
        }
        else {
            if (licenseFilterDTO.getLicenseEndDate() == null) {
                if (licenseFilterDTO.getLicenseProductId() == null) {
                    if (licenseFilterDTO.getUserId() == null) {
                        return toReturningLicenseFilterDTOList(
                                licenseRepository.findAllByStartDate(licenseFilterDTO.getLicenseStartDate()));
                    }
                    else {
                        return toReturningLicenseFilterDTOList(
                                licenseRepository.findAllByUserIdAndStartDate(
                                        licenseFilterDTO.getUserId(),
                                        licenseFilterDTO.getLicenseStartDate()));
                    }
                }
                else {
                    Optional<Product> product = productService.getProduct(licenseFilterDTO.getLicenseProductId());
                    if (licenseFilterDTO.getUserId() == null) {
                        if (product.isPresent()) {
                            return toReturningLicenseFilterDTOList(
                                    licenseRepository.findAllByStartDateAndProduct(
                                            licenseFilterDTO.getLicenseStartDate(),
                                            product.get()));
                        }
                        return new ArrayList<>();
                    }
                    else {
                        if (product.isPresent()) {
                            return toReturningLicenseFilterDTOList(
                                    licenseRepository.findAllByUserIdAndStartDateAndProduct(
                                            licenseFilterDTO.getUserId(),
                                            licenseFilterDTO.getLicenseStartDate(),
                                            product.get()));
                        }
                        return new ArrayList<>();
                    }
                }
            }
            else {
                if (licenseFilterDTO.getLicenseProductId() == null) {
                    if (licenseFilterDTO.getUserId() == null) {
                        return toReturningLicenseFilterDTOList(
                                licenseRepository.findAllByStartDateAndEndDate(
                                        licenseFilterDTO.getLicenseStartDate(),
                                        licenseFilterDTO.getLicenseEndDate()));
                    }
                    else {
                        return toReturningLicenseFilterDTOList(
                                licenseRepository.findAllByUserIdAndStartDateAndEndDate(
                                        licenseFilterDTO.getUserId(),
                                        licenseFilterDTO.getLicenseStartDate(),
                                        licenseFilterDTO.getLicenseEndDate()));
                    }
                }
                else {
                    Optional<Product> product = productService.getProduct(licenseFilterDTO.getLicenseProductId());
                    if (licenseFilterDTO.getUserId() == null) {
                        if (product.isPresent()) {
                            return toReturningLicenseFilterDTOList(
                                    licenseRepository.findAllByStartDateAndEndDateAndProduct(
                                            licenseFilterDTO.getLicenseStartDate(),
                                            licenseFilterDTO.getLicenseEndDate(),
                                            product.get()));
                        }
                        return new ArrayList<>();
                    }
                    else {
                        if (product.isPresent()) {
                            return toReturningLicenseFilterDTOList(
                                    licenseRepository.findAllByUserIdAndStartDateAndEndDateAndProduct(
                                            licenseFilterDTO.getUserId(),
                                            licenseFilterDTO.getLicenseStartDate(),
                                            licenseFilterDTO.getLicenseEndDate(),
                                            product.get()));
                        }
                        return new ArrayList<>();
                    }
                }
            }
        }
    }

    private List<ReturningLicenseFilterDTO> toReturningLicenseFilterDTOList(List<License> licenseList) {
        return licenseList.stream().map(license -> new ReturningLicenseFilterDTO(
                license.getStartDate().toString(),
                license.getEndDate().toString(),
                license.getUserId(),
                license.getProduct().getId()
        )).toList();
    }
}
