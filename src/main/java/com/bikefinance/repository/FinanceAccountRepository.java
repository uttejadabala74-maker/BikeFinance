package com.bikefinance.repository;

import com.bikefinance.FinanceAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinanceAccountRepository extends JpaRepository<FinanceAccount, Integer> {

    List<FinanceAccount> findByCustomerCustomerId(Integer customerId);

    Optional<FinanceAccount> findByBikeBikeId(Integer bikeId);

    List<FinanceAccount> findByStatus(String status);

    @Query("SELECT f FROM FinanceAccount f WHERE LOWER(f.loanAccountNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(f.financeCompany) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(f.customer.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(f.bike.registrationNumber) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<FinanceAccount> searchFinanceAccounts(@Param("query") String query);

    @Query("SELECT SUM(f.totalPaid) FROM FinanceAccount f")
    Double sumTotalPaid();

    @Query("SELECT SUM(f.totalOutstanding) FROM FinanceAccount f")
    Double sumTotalOutstanding();

    @Query("SELECT SUM(f.financePrincipal) FROM FinanceAccount f")
    Double sumFinancePrincipal();

    @Query("SELECT SUM(f.totalRepayment) FROM FinanceAccount f")
    Double sumTotalRepayment();
}
