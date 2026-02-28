package zp.gde.hu.alkfetdbsvc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * X.509 gyökér (self-signed) tanúsítvány entitás.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "root_certificates")
public class RootCertificate {

    @Id
    private String id;

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

    /** PEM formátumú tanúsítvány */
    private String certificatePem;

    /** PEM formátumú privát kulcs (titkosítva tárolva) */
    private String privateKeyPem;

    /** Sorozatszám */
    private String serialNumber;

    /** Létrehozás időpontja */
    private LocalDateTime createdAt;
}

