package zp.gde.hu.alkfetsvc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO felhasználói tanúsítvány válaszhoz.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCertificateResponse {

    private String id;
    private String rootCertificateId;
    private String commonName;
    private String organization;
    private String organizationalUnit;
    private String country;
    private String state;
    private String locality;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private String certificatePem;
    private String csrPem;
    private String serialNumber;
    private String status;
    private LocalDateTime storedAt;
    private LocalDateTime createdAt;
}

