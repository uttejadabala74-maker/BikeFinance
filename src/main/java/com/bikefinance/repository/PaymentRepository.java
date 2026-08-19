package com.bikefinance.repository;

import com.bikefinance.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    List<Payment> findByFinanceAccountFinanceIdOrderByPaymentDateDesc(Integer financeId);

    List<Payment> findByCustomerCustomerId(Integer customerId);

    List<Payment> findByBikeBikeId(Integer bikeId);

    List<Payment> findTop10ByOrderByPaymentDateDescPaymentIdDesc();

    @Query("SELECT SUM(p.amountPaid) FROM Payment p WHERE p.financeAccount.financeId = :financeId")
    Double sumAmountPaidByFinanceId(@Param("financeId") Integer financeId);

    @Query("SELECT SUM(p.amountPaid) FROM Payment p")
    Double sumTotalCollected();

    @Query("SELECT SUM(p.amountPaid) FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate")
    Double sumCollectedBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
