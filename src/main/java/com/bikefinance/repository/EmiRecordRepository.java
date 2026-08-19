package com.bikefinance.repository;

import com.bikefinance.EmiRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmiRecordRepository extends JpaRepository<EmiRecord, Integer> {

    List<EmiRecord> findByFinanceAccountFinanceIdOrderByEmiNumberAsc(Integer financeId);

    List<EmiRecord> findByCustomerCustomerId(Integer customerId);

    List<EmiRecord> findByBikeBikeId(Integer bikeId);

    List<EmiRecord> findByStatus(String status);

    @Query("SELECT e FROM EmiRecord e WHERE e.status = 'Pending' AND e.dueDate < :today")
    List<EmiRecord> findOverdueEmis(@Param("today") LocalDate today);

    @Query("SELECT e FROM EmiRecord e WHERE e.dueDate BETWEEN :startDate AND :endDate ORDER BY e.dueDate ASC")
    List<EmiRecord> findEmisDueBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    Long countByStatus(String status);

    @Query("SELECT COUNT(e) FROM EmiRecord e WHERE e.status = 'Pending' AND e.dueDate < :today")
    Long countOverdueEmis(@Param("today") LocalDate today);
}
