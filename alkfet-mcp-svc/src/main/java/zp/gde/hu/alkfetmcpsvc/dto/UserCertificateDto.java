package zp.gde.hu.alkfetmcpsvc.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserCertificateDto {
    private String id;
    private String rootCertificateId;
    private String commonName;
    private String organization;
    private String country;
    private String serialNumber;
    private String status;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private LocalDateTime createdAt;
}

