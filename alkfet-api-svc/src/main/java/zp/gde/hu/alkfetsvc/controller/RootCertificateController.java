package zp.gde.hu.alkfetsvc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zp.gde.hu.alkfetsvc.dto.CreateRootCertificateRequest;
import zp.gde.hu.alkfetsvc.dto.DeleteCertificateResponse;
import zp.gde.hu.alkfetsvc.dto.PageResponse;
import zp.gde.hu.alkfetsvc.dto.RootCertificateResponse;
import zp.gde.hu.alkfetsvc.service.RootCertificateService;

/**
 * Root Certificate műveletek (Create, Delete, List).
 * Az ábra alapján: "Root-Cert operations" blokk.
 */
@RestController
@RequestMapping("/api/v1/root-certificates")
@RequiredArgsConstructor
public class RootCertificateController {

    private final RootCertificateService rootCertificateService;

    /**
     * Új gyökér (self-signed) tanúsítvány létrehozása.
     * Use case: Create root certificate (self-signed).
     */
    @PostMapping
    public ResponseEntity<RootCertificateResponse> createRootCertificate(
            @RequestBody CreateRootCertificateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rootCertificateService.create(request));
    }

    /**
     * Gyökér tanúsítványok lapozható listázása.
     *
     * @param page oldalszám (0-alapú, alapértelmezett: 0)
     * @param size oldalméret (alapértelmezett: 10)
     */
    @GetMapping
    public ResponseEntity<PageResponse<RootCertificateResponse>> listRootCertificates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(rootCertificateService.findAll(page, size));
    }

    /**
     * Gyökér tanúsítvány lekérdezése ID alapján.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RootCertificateResponse> getRootCertificateById(@PathVariable String id) {
        return ResponseEntity.ok(rootCertificateService.findById(id));
    }

    /**
     * Gyökér tanúsítvány törlése.
     * Use case: "Delete root certificate" [piros ellipszis].
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteCertificateResponse> deleteRootCertificate(@PathVariable String id) {
        return ResponseEntity.ok(rootCertificateService.delete(id));
    }
}
