package zp.gde.hu.alkfetdbsvc.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import zp.gde.hu.alkfetdbsvc.entity.UserCertificate;

@Repository
public interface UserCertificateRepository extends MongoRepository<UserCertificate, String> {

    Page<UserCertificate> findByRootCertificateId(String rootCertificateId, Pageable pageable);
}

