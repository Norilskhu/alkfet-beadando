package zp.gde.hu.alkfetsvc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zp.gde.hu.alkfetsvc.dto.DeleteCertificateResponse;
import zp.gde.hu.alkfetsvc.dto.PageResponse;
import zp.gde.hu.alkfetsvc.dto.SignUserCertificateRequest;
import zp.gde.hu.alkfetsvc.dto.UserCertificateResponse;
import zp.gde.hu.alkfetsvc.service.UserCertificateService;

/**
 * User Certificate műveletek (Sign CSR, Delete, List).
 * Az ábra alapján: "User-Cert operations" blokk.
 */
@RestController
@RequestMapping("/api/v1/user-certificates")
@RequiredArgsConstructor
public class UserCertificateController {

    private final UserCertificateService userCertificateService;

    /**
     * CSR aláírása gyökér tanúsítvánnyal és tárolása egyszerre.
     * Use case: "Sign User Certificate (Upload CSR)" + "Store User Certificate".
     */
    @PostMapping
    public ResponseEntity<UserCertificateResponse> signAndStoreUserCertificate(
            @RequestBody SignUserCertificateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userCertificateService.signAndStore(request));
    }

    /**
     * Felhasználói tanúsítványok lapozható listázása.
     *
     * @param page              oldalszám (0-alapú, alapértelmezett: 0)
     * @param size              oldalméret (alapértelmezett: 10)
     * @param rootCertificateId opcionális szűrés root cert ID alapján
     */
    @GetMapping
    public ResponseEntity<PageResponse<UserCertificateResponse>> listUserCertificates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String rootCertificateId) {
        return ResponseEntity.ok(userCertificateService.findAll(page, size, rootCertificateId));
    }

    /**
     * Felhasználói tanúsítvány lekérdezése ID alapján.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserCertificateResponse> getUserCertificateById(@PathVariable String id) {
        return ResponseEntity.ok(userCertificateService.findById(id));
    }

    /**
     * Felhasználói tanúsítvány törlése.
     * Use case: "Delete User Certificate" [piros ellipszis].
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteCertificateResponse> deleteUserCertificate(@PathVariable String id) {
        return ResponseEntity.ok(userCertificateService.delete(id));
    }
}
