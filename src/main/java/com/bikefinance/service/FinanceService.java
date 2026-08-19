package com.bikefinance.service;

import com.bikefinance.*;
import com.bikefinance.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class FinanceService {

    private final FinanceAccountRepository financeAccountRepository;
    private final EmiRecordRepository emiRecordRepository;
    private final PaymentRepository paymentRepository;
    private final BikeRepository bikeRepository;
    private final CustomerRepository customerRepository;

    public FinanceService(FinanceAccountRepository financeAccountRepository,
                          EmiRecordRepository emiRecordRepository,
                          PaymentRepository paymentRepository,
                          BikeRepository bikeRepository,
                          CustomerRepository customerRepository) {
        this.financeAccountRepository = financeAccountRepository;
        this.emiRecordRepository = emiRecordRepository;
        this.paymentRepository = paymentRepository;
        this.bikeRepository = bikeRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * Dynamically calculates financial breakdown object for live previews or validation.
     */
    public CalculationResult calculateFinance(Double bikeActualPrice, Double downPayment,
                                             Double agreementCharges, Double otherCharges,
                                             Double interestRate, Integer tenure, Integer numberOfEmis) {
        double actualPrice = bikeActualPrice != null ? bikeActualPrice : 0.0;
        double advance = downPayment != null ? downPayment : 0.0;
        double agreement = agreementCharges != null ? agreementCharges : 0.0;
        double other = otherCharges != null ? otherCharges : 0.0;
        double rate = interestRate != null ? interestRate : 0.0;
        int t = (tenure != null && tenure > 0) ? tenure : 1;
        int numEmis = (numberOfEmis != null && numberOfEmis > 0) ? numberOfEmis : t;

        double balanceAmount = Math.max(0.0, actualPrice - advance);
        double principal = balanceAmount + agreement + other;
        
        // Flat Interest formula: Principal * (Rate / 100) * Tenure
        double totalInterest = principal * (rate / 100.0) * t;
        double totalRepayment = principal + totalInterest;
        
        // Base EMI rounded to nearest integer or 2 decimal places
        double baseEmi = numEmis > 0 ? Math.round(totalRepayment / numEmis) : 0.0;

        return new CalculationResult(actualPrice, advance, balanceAmount, agreement, other,
                principal, rate, t, numEmis, baseEmi, totalInterest, totalRepayment);
    }

    /**
     * Saves or updates a Finance Account, updates Bike status to 'Financed', and generates EMI schedule.
     */
    @Transactional
    public FinanceAccount createFinanceAccount(FinanceAccount account) {
        // 1. Fetch persistent Bike and Customer
        Bike bike = bikeRepository.findById(account.getBike().getBikeId())
                .orElseThrow(() -> new IllegalArgumentException("Bike not found"));
        Customer customer = customerRepository.findById(account.getCustomer().getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        account.setBike(bike);
        account.setCustomer(customer);

        // 2. Perform server-side calculation
        CalculationResult calc = calculateFinance(
                account.getBikeActualPrice(),
                account.getDownPayment(),
                account.getAgreementCharges(),
                account.getOtherCharges(),
                account.getInterestRate(),
                account.getTenure(),
                account.getNumberOfEmis()
        );

        account.setBalanceAmount(calc.balanceAmount());
        account.setFinancePrincipal(calc.principal());
        account.setTotalInterest(calc.totalInterest());
        account.setTotalRepayment(calc.totalRepayment());
        account.setEmiAmount(calc.baseEmi());
        account.setTotalPaid(0.0);
        account.setTotalOutstanding(calc.totalRepayment());
        account.setStatus("Active");

        if (account.getFinanceStartDate() == null) {
            account.setFinanceStartDate(LocalDate.now());
        }
        if (account.getFirstEmiDate() == null) {
            account.setFirstEmiDate(account.getFinanceStartDate().plusMonths(1));
        }

        // 3. Save Finance Account
        FinanceAccount savedAccount = financeAccountRepository.save(account);

        // 4. Update Bike Status & link customer
        bike.setStatus("Financed");
        bike.setCustomer(customer);
        bike.setBikeActualPrice(account.getBikeActualPrice());
        bike.setDownPayment(account.getDownPayment());
        bike.setFinancePrincipal(calc.principal());
        if (bike.getSellingDate() == null) {
            bike.setSellingDate(account.getFinanceStartDate());
        }
        bikeRepository.save(bike);

        // 5. Generate exact EMI Schedule records
        generateEmiSchedule(savedAccount);

        return savedAccount;
    }

    /**
     * Generates exact EMI records absorbing rounding difference on the final EMI.
     */
    @Transactional
    public void generateEmiSchedule(FinanceAccount account) {
        // Delete any existing unpaid EMI records if regenerating
        List<EmiRecord> existing = emiRecordRepository.findByFinanceAccountFinanceIdOrderByEmiNumberAsc(account.getFinanceId());
        if (!existing.isEmpty()) {
            emiRecordRepository.deleteAll(existing);
        }

        int numEmis = account.getNumberOfEmis();
        double totalRepayment = account.getTotalRepayment();
        double standardEmi = Math.round(totalRepayment / numEmis);

        double runningSum = 0.0;
        LocalDate firstEmiDate = account.getFirstEmiDate();

        List<EmiRecord> schedule = new ArrayList<>();
        for (int i = 1; i <= numEmis; i++) {
            EmiRecord emi = new EmiRecord();
            emi.setFinanceAccount(account);
            emi.setCustomer(account.getCustomer());
            emi.setBike(account.getBike());
            emi.setEmiNumber(i);

            // Date logic: first EMI uses firstEmiDate, subsequent months increment by (i-1)
            LocalDate dueDate = firstEmiDate.plusMonths(i - 1);
            emi.setDueDate(dueDate);

            double currentEmiAmount;
            if (i < numEmis) {
                currentEmiAmount = standardEmi;
                runningSum += currentEmiAmount;
            } else {
                // Final EMI absorbs rounding difference so sum equals totalRepayment exactly
                currentEmiAmount = Math.max(0.0, totalRepayment - runningSum);
            }

            emi.setEmiAmount(currentEmiAmount);
            emi.setPaidAmount(0.0);
            emi.setRemainingAmount(currentEmiAmount);
            
            // Check overdue status initially if date is in past
            if (dueDate.isBefore(LocalDate.now())) {
                emi.setStatus("Overdue");
            } else {
                emi.setStatus("Pending");
            }

            schedule.add(emi);
        }

        emiRecordRepository.saveAll(schedule);
    }

    /**
     * Processes a new payment, allocates it to EMI schedule, and updates outstanding totals.
     */
    @Transactional
    public Payment recordPayment(Payment payment) {
        FinanceAccount account = financeAccountRepository.findById(payment.getFinanceAccount().getFinanceId())
                .orElseThrow(() -> new IllegalArgumentException("Finance Account not found"));

        payment.setFinanceAccount(account);
        payment.setCustomer(account.getCustomer());
        payment.setBike(account.getBike());

        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDate.now());
        }

        // Save Payment record
        Payment savedPayment = paymentRepository.save(payment);

        // Allocate payment across EMI schedule
        double unallocated = payment.getAmountPaid();
        List<EmiRecord> schedule = emiRecordRepository.findByFinanceAccountFinanceIdOrderByEmiNumberAsc(account.getFinanceId());

        for (EmiRecord emi : schedule) {
            if (unallocated <= 0) break;
            
            if ("Paid".equals(emi.getStatus()) || "Waived".equals(emi.getStatus())) {
                continue;
            }

            double remainingEmi = emi.getEmiAmount() - emi.getPaidAmount();
            if (remainingEmi <= 0) continue;

            if (unallocated >= remainingEmi) {
                emi.setPaidAmount(emi.getEmiAmount());
                emi.setRemainingAmount(0.0);
                emi.setStatus("Paid");
                emi.setPaymentDate(payment.getPaymentDate());
                unallocated -= remainingEmi;
            } else {
                emi.setPaidAmount(emi.getPaidAmount() + unallocated);
                emi.setRemainingAmount(emi.getEmiAmount() - emi.getPaidAmount());
                emi.setStatus("Partially Paid");
                emi.setPaymentDate(payment.getPaymentDate());
                unallocated = 0.0;
            }
            emiRecordRepository.save(emi);
        }

        // Recalculate Finance Account totals
        recalculateAccountTotals(account.getFinanceId());

        return savedPayment;
    }

    /**
     * Recalculates Total Paid and Total Outstanding strictly from actual payment records.
     */
    @Transactional
    public void recalculateAccountTotals(Integer financeId) {
        FinanceAccount account = financeAccountRepository.findById(financeId)
                .orElseThrow(() -> new IllegalArgumentException("Finance Account not found"));

        Double totalPaid = paymentRepository.sumAmountPaidByFinanceId(financeId);
        if (totalPaid == null) totalPaid = 0.0;

        account.setTotalPaid(totalPaid);
        double outstanding = Math.max(0.0, account.getTotalRepayment() - totalPaid);
        account.setTotalOutstanding(outstanding);

        if (outstanding <= 0.0 && account.getTotalRepayment() > 0.0) {
            account.setStatus("Completed");
            Bike bike = account.getBike();
            if (bike != null) {
                bike.setStatus("Completed");
                bikeRepository.save(bike);
            }
        } else if ("Completed".equals(account.getStatus()) && outstanding > 0.0) {
            account.setStatus("Active");
            Bike bike = account.getBike();
            if (bike != null) {
                bike.setStatus("Financed");
                bikeRepository.save(bike);
            }
        }

        financeAccountRepository.save(account);

        // Update overdue statuses for pending EMIs
        updateOverdueStatuses(financeId);
    }

    /**
     * Updates EMI status to Overdue if due_date < today and not fully paid.
     */
    @Transactional
    public void updateOverdueStatuses(Integer financeId) {
        List<EmiRecord> schedule = emiRecordRepository.findByFinanceAccountFinanceIdOrderByEmiNumberAsc(financeId);
        LocalDate today = LocalDate.now();

        for (EmiRecord emi : schedule) {
            if (("Pending".equals(emi.getStatus()) || "Partially Paid".equals(emi.getStatus()))
                    && emi.getDueDate() != null && emi.getDueDate().isBefore(today)) {
                emi.setStatus("Overdue");
                emiRecordRepository.save(emi);
            }
        }
    }

    public record CalculationResult(
            double bikeActualPrice,
            double downPayment,
            double balanceAmount,
            double agreementCharges,
            double otherCharges,
            double principal,
            double interestRate,
            int tenure,
            int numberOfEmis,
            double baseEmi,
            double totalInterest,
            double totalRepayment
    ) {}
}
