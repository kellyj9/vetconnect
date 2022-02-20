package org.launchcode.VetConnect.models.data;

import org.launchcode.VetConnect.models.Claim;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimRepository extends CrudRepository<Claim, Long> {

    List<Claim> findByUserIdAndApproved(Long user_id, Boolean approved);
}
