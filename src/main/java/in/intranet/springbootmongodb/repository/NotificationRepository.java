package in.intranet.springbootmongodb.repository;

import in.intranet.springbootmongodb.model.NotificationModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepository extends MongoRepository<NotificationModel, String> {}
