package zp.gde.hu.alkfetdbsvc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import zp.gde.hu.alkfetdbsvc.entity.UserCertificate;
import zp.gde.hu.alkfetdbsvc.repository.UserCertificateRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserCertificateService {

    private final UserCertificateRepository repository;

    public Page<UserCertificate> findAll(int page, int size) {
        return repository.findAll(PageRequest.of(page, size));
    }

    public Page<UserCertificate> findByRootCertificateId(String rootCertificateId, int page, int size) {
        return repository.findByRootCertificateId(rootCertificateId, PageRequest.of(page, size));
    }

    public Optional<UserCertificate> findById(String id) {
        return repository.findById(id);
    }

    public UserCertificate save(UserCertificate userCertificate) {
        return repository.save(userCertificate);
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }

    public boolean existsById(String id) {
        return repository.existsById(id);
    }
}

