package in.intranet.springbootmongodb.repository;

import in.intranet.springbootmongodb.model.ScheduleModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends MongoRepository<ScheduleModel, String> {
    List<ScheduleModel> findByCreatedBy(String email);
}