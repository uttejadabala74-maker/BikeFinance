package com.bikefinance.repository;

import com.bikefinance.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    
    List<Customer> findByNameContainingIgnoreCaseOrPhoneContaining(String name, String phone);

    @Query("SELECT c FROM Customer c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(c.phone) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(c.alternatePhone) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(c.guardianName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Customer> searchCustomers(@Param("query") String query);
}