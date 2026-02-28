package zp.gde.hu.alkfetsvc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO gyökér tanúsítvány létrehozásához (self-signed).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRootCertificateRequest {

    /** Tanúsítvány közönséges neve (CN) */
    private String commonName;

    /** Szervezet neve (O) */
    private String organization;

    /** Szervezeti egység (OU) */
    private String organizationalUnit;

    /** Ország kód (C), pl. "HU" */
    private String country;

    /** Állam / Megye (ST) */
    private String state;

    /** Helység (L) */
    private String locality;

    /** Érvényesség hossza napokban */
    private int validityDays;
}

