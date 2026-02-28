package zp.gde.hu.alkfetdbsvc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zp.gde.hu.alkfetdbsvc.entity.UserCertificate;
import zp.gde.hu.alkfetdbsvc.service.UserCertificateService;

@RestController
@RequestMapping("/internal/user-certificates")
@RequiredArgsConstructor
public class UserCertificateController {

    private final UserCertificateService service;

    @GetMapping
    public Page<UserCertificate> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String rootCertificateId) {
        if (rootCertificateId != null) {
            return service.findByRootCertificateId(rootCertificateId, page, size);
        }
        return service.findAll(page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserCertificate> findById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public UserCertificate save(@RequestBody UserCertificate userCertificate) {
        return service.save(userCertificate);
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

