package org.launchcode.VetConnect.models.data;

import org.launchcode.VetConnect.models.Request;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepository extends CrudRepository<Request, Long> {
}
