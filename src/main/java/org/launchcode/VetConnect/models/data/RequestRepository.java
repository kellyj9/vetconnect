package org.launchcode.VetConnect.models.data;

import org.launchcode.VetConnect.models.Request;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByUserIdAndStatus(Long user_id, String status);
    List<Request> findByUserId(Long user_id);
    Page<Request> findAllByStatus(String status, Pageable pageable);

}
