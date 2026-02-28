package zp.gde.hu.alkfetsvc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO felhasználói tanúsítvány aláírásához (CSR feltöltés alapján).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUserCertificateRequest {

    /** Az aláíró gyökér tanúsítvány azonosítója */
    private String rootCertificateId;

    /** PEM formátumú Certificate Signing Request (CSR) */
    private String csrPem;

    /** Érvényesség hossza napokban */
    private int validityDays;
}

