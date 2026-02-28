package zp.gde.hu.alkfetdbsvc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zp.gde.hu.alkfetdbsvc.entity.RootCertificate;
import zp.gde.hu.alkfetdbsvc.service.RootCertificateService;

@RestController
@RequestMapping("/internal/root-certificates")
@RequiredArgsConstructor
public class RootCertificateController {

    private final RootCertificateService service;

    @GetMapping
    public Page<RootCertificate> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return service.findAll(page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RootCertificate> findById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public RootCertificate save(@RequestBody RootCertificate rootCertificate) {
        return service.save(rootCertificate);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable String id) {
        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

