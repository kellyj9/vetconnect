package org.launchcode.VetConnect.models.data;

import org.launchcode.VetConnect.models.Request;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends CrudRepository<Request, Long> {

    List<Request> findByUserIdAndStatus(Long user_id, String status);
    List<Request> findByUserId(Long user_id);

}
