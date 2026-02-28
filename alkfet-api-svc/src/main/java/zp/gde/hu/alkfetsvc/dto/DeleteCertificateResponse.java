package zp.gde.hu.alkfetsvc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO tanúsítvány törlési válaszhoz (root és user tanúsítványokhoz egyaránt).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteCertificateResponse {

    /** A törölt tanúsítvány azonosítója */
    private String id;

    /** Visszajelzés üzenet */
    private String message;

    /** Sikeres volt-e a törlés */
    private boolean success;
}

