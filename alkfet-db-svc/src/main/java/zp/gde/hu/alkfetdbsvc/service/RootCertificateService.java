package zp.gde.hu.alkfetdbsvc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import zp.gde.hu.alkfetdbsvc.entity.RootCertificate;
import zp.gde.hu.alkfetdbsvc.repository.RootCertificateRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RootCertificateService {

    private final RootCertificateRepository repository;

    public Page<RootCertificate> findAll(int page, int size) {
        return repository.findAll(PageRequest.of(page, size));
    }

    public Optional<RootCertificate> findById(String id) {
        return repository.findById(id);
    }

    public RootCertificate save(RootCertificate rootCertificate) {
        return repository.save(rootCertificate);
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }

    public boolean existsById(String id) {
        return repository.existsById(id);
    }
}

