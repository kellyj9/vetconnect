package org.launchcode.VetConnect.models.data;

import org.launchcode.VetConnect.models.Claim;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {

    List<Claim> findByUserIdAndStatus(Long user_id, String status);
    Page<Claim> findAllByUserId(Long userId, Pageable pageable);
    Page<Claim> findAllByStatus(String status, Pageable pageable);
    Page<Claim> findAllByUserIdAndStatus(Long userId, String status, Pageable pageable);
    Page<Claim> findAll(Pageable pageable);
    Claim findByClinicIdAndStatus(Long clinicId, String status);
    Claim findByClinicId(Long clinicId);
}
