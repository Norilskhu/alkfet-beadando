package zp.gde.hu.alkfetdbsvc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * X.509 felhasználói tanúsítvány entitás.
 * A tanúsítványt a RootCertificate írja alá CSR feltöltés alapján.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_certificates")
public class UserCertificate {

    @Id
    private String id;

    /** Az aláíró gyökér tanúsítvány azonosítója */
    private String rootCertificateId;

    /** Tanúsítvány közönséges neve (CN) */
    private String commonName;

    /** Kibocsátó szervezet neve (O) */
    private String organization;

    /** Kibocsátó szervezeti egység (OU) */
    private String organizationalUnit;

    /** Ország (C) */
    private String country;

    /** Állam / Megye (ST) */
    private String state;

    /** Helység (L) */
    private String locality;

    /** Érvényesség kezdete */
    private LocalDateTime validFrom;

    /** Érvényesség vége */
    private LocalDateTime validTo;

    /** PEM formátumú tanúsítvány (aláírt) */
    private String certificatePem;

    /** Az eredeti CSR (Certificate Signing Request) PEM formátumban */
    private String csrPem;

    /** Sorozatszám */
    private String serialNumber;

    /** Tanúsítvány státusza */
    private CertificateStatus status;

    /** Tárolás időpontja */
    private LocalDateTime storedAt;

    /** Létrehozás időpontja */
    private LocalDateTime createdAt;

    public enum CertificateStatus {
        ACTIVE,
        REVOKED,
        EXPIRED
    }
}

