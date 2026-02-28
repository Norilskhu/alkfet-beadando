package zp.gde.hu.alkfetdbsvc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import zp.gde.hu.alkfetdbsvc.entity.RootCertificate;

@Repository
public interface RootCertificateRepository extends MongoRepository<RootCertificate, String> {
}
