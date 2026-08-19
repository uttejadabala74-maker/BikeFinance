package com.bikefinance.repository;

import com.bikefinance.Bike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BikeRepository extends JpaRepository<Bike, Integer> {
    
    List<Bike> findByStatus(String status);
    
    List<Bike> findByCustomerCustomerId(Integer customerId);

    @Query("SELECT b FROM Bike b WHERE LOWER(b.registrationNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(b.company) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(b.model) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(b.chassisNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(b.engineNumber) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Bike> searchBikes(@Param("query") String query);
}