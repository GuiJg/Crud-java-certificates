package in.intranet.springbootmongodb.repository;

import in.intranet.springbootmongodb.model.CertificateModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateRepository extends MongoRepository<CertificateModel, String> {
}
