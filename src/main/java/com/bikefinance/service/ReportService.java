package com.bikefinance.service;

import com.bikefinance.*;
import com.bikefinance.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final BikeRepository bikeRepository;
    private final CustomerRepository customerRepository;
    private final FinanceAccountRepository financeAccountRepository;
    private final EmiRecordRepository emiRecordRepository;
    private final PaymentRepository paymentRepository;
    private final ChallanRepository challanRepository;

    public ReportService(BikeRepository bikeRepository,
                         CustomerRepository customerRepository,
                         FinanceAccountRepository financeAccountRepository,
                         EmiRecordRepository emiRecordRepository,
                         PaymentRepository paymentRepository,
                         ChallanRepository challanRepository) {
        this.bikeRepository = bikeRepository;
        this.customerRepository = customerRepository;
        this.financeAccountRepository = financeAccountRepository;
        this.emiRecordRepository = emiRecordRepository;
        this.paymentRepository = paymentRepository;
        this.challanRepository = challanRepository;
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalBikes = bikeRepository.count();
        long availableBikes = bikeRepository.findByStatus("Available").size();
        long financedBikes = bikeRepository.findByStatus("Financed").size();
        long totalCustomers = customerRepository.count();
        long activeFinance = financeAccountRepository.findByStatus("Active").size();

        long pendingEmis = emiRecordRepository.countByStatus("Pending");
        long overdueEmis = emiRecordRepository.countOverdueEmis(LocalDate.now());

        Double collected = paymentRepository.sumTotalCollected();
        double totalCollected = collected != null ? collected : 0.0;

        Double outstanding = financeAccountRepository.sumTotalOutstanding();
        double totalOutstanding = outstanding != null ? outstanding : 0.0;

        long totalChallans = challanRepository.countTotalChallans();

        stats.put("totalBikes", totalBikes);
        stats.put("availableBikes", availableBikes);
        stats.put("financedBikes", financedBikes);
        stats.put("totalCustomers", totalCustomers);
        stats.put("activeFinance", activeFinance);
        stats.put("pendingEmis", pendingEmis);
        stats.put("overdueEmis", overdueEmis);
        stats.put("totalCollected", totalCollected);
        stats.put("totalOutstanding", totalOutstanding);
        stats.put("totalChallans", totalChallans);

        // Recent tables
        stats.put("recentPayments", paymentRepository.findTop10ByOrderByPaymentDateDescPaymentIdDesc());
        stats.put("upcomingEmis", emiRecordRepository.findEmisDueBetween(LocalDate.now(), LocalDate.now().plusDays(30)));
        stats.put("overdueEmiList", emiRecordRepository.findOverdueEmis(LocalDate.now()));
        stats.put("recentFinance", financeAccountRepository.findAll());

        return stats;
    }
}
