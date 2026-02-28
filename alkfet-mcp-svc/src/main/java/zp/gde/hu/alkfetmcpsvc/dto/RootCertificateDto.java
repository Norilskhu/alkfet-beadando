package zp.gde.hu.alkfetmcpsvc.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RootCertificateDto {
    private String id;
    private String commonName;
    private String organization;
    private String organizationalUnit;
    private String country;
    private String state;
    private String locality;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private String serialNumber;
    private LocalDateTime createdAt;
}

