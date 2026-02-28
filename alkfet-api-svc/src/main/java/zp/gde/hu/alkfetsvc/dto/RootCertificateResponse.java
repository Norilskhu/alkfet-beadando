package zp.gde.hu.alkfetsvc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO gyökér tanúsítvány válaszhoz.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RootCertificateResponse {

    private String id;
    private String commonName;
    private String organization;
    private String organizationalUnit;
    private String country;
    private String state;
    private String locality;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private String certificatePem;
    private String serialNumber;
    private LocalDateTime createdAt;
}

