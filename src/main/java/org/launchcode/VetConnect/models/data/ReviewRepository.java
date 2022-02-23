package org.launchcode.VetConnect.models.data;

import org.launchcode.VetConnect.models.Review;
import org.springframework.data.repository.CrudRepository;

public interface ReviewRepository extends CrudRepository<Review, Long>{

    Review findByUserIdAndClinicId (Long user_id, Long clinic_id);
}
