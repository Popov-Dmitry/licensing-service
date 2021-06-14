package com.github.popovdmitry.licenseservice.dto;

import com.github.popovdmitry.licenseservice.model.License;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturningLicenseDTO {

    private Long id;
    private String startDate;
    private String endDate;
    private Long userId;
    private String productName;
    private String publicKey;

    public static ReturningLicenseDTO fromLicense(License license) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, license.getKeyPair().getPublic());
        ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);
        byteBuffer.putLong(license.getUserId());
        byte[] bytes = cipher.doFinal(byteBuffer.array());
        String publicKey = new String(bytes);

        return new ReturningLicenseDTO(license.getId(),
                license.getStartDate().toString(),
                license.getEndDate().toString(),
                license.getUserId(),
                license.getProduct().getName(),
                publicKey);
    }

}
