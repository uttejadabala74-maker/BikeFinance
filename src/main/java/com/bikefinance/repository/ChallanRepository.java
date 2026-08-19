package com.bikefinance.repository;

import com.bikefinance.Challan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallanRepository extends JpaRepository<Challan, Integer> {

    List<Challan> findByBikeBikeId(Integer bikeId);

    List<Challan> findByCustomerCustomerId(Integer customerId);

    List<Challan> findByStatus(String status);

    @Query("SELECT COUNT(c) FROM Challan c")
    Long countTotalChallans();

    @Query("SELECT SUM(c.amount) FROM Challan c WHERE c.status = 'Pending'")
    Double sumPendingChallans();
}
