package com.bikefinance;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "finance_accounts")
public class FinanceAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "finance_id")
    private Integer financeId;

    @Column(name = "finance_company")
    private String financeCompany;

    @Column(name = "loan_account_number")
    private String loanAccountNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bike_id", nullable = false)
    private Bike bike;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "bike_actual_price")
    private Double bikeActualPrice = 0.0;

    @Column(name = "down_payment")
    private Double downPayment = 0.0;

    @Column(name = "balance_amount")
    private Double balanceAmount = 0.0;

    @Column(name = "agreement_charges")
    private Double agreementCharges = 0.0;

    @Column(name = "other_charges")
    private Double otherCharges = 0.0;

    @Column(name = "finance_principal")
    private Double financePrincipal = 0.0;

    @Column(name = "interest_rate")
    private Double interestRate = 0.0; // Percentage, e.g. 1.8

    @Column(name = "interest_type")
    private String interestType = "Flat";

    @Column(name = "tenure")
    private Integer tenure = 10; // in months

    @Column(name = "number_of_emis")
    private Integer numberOfEmis = 10;

    @Column(name = "emi_amount")
    private Double emiAmount = 0.0;

    @Column(name = "processing_fee")
    private Double processingFee = 0.0;

    @Column(name = "status")
    private String status = "Active"; // Active, Completed, Closed, Cancelled

    @Column(name = "finance_start_date")
    private LocalDate financeStartDate;

    @Column(name = "first_emi_date")
    private LocalDate firstEmiDate;

    @Column(name = "total_repayment")
    private Double totalRepayment = 0.0;

    @Column(name = "total_interest")
    private Double totalInterest = 0.0;

    @Column(name = "total_paid")
    private Double totalPaid = 0.0;

    @Column(name = "total_outstanding")
    private Double totalOutstanding = 0.0;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public FinanceAccount() {
    }

    public Integer getFinanceId() {
        return financeId;
    }

    public void setFinanceId(Integer financeId) {
        this.financeId = financeId;
    }

    public String getFinanceCompany() {
        return financeCompany;
    }

    public void setFinanceCompany(String financeCompany) {
        this.financeCompany = financeCompany;
    }

    public String getLoanAccountNumber() {
        return loanAccountNumber;
    }

    public void setLoanAccountNumber(String loanAccountNumber) {
        this.loanAccountNumber = loanAccountNumber;
    }

    public Bike getBike() {
        return bike;
    }

    public void setBike(Bike bike) {
        this.bike = bike;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Double getBikeActualPrice() {
        return bikeActualPrice != null ? bikeActualPrice : 0.0;
    }

    public void setBikeActualPrice(Double bikeActualPrice) {
        this.bikeActualPrice = bikeActualPrice;
    }

    public Double getDownPayment() {
        return downPayment != null ? downPayment : 0.0;
    }

    public void setDownPayment(Double downPayment) {
        this.downPayment = downPayment;
    }

    public Double getBalanceAmount() {
        return balanceAmount != null ? balanceAmount : 0.0;
    }

    public void setBalanceAmount(Double balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public Double getAgreementCharges() {
        return agreementCharges != null ? agreementCharges : 0.0;
    }

    public void setAgreementCharges(Double agreementCharges) {
        this.agreementCharges = agreementCharges;
    }

    public Double getOtherCharges() {
        return otherCharges != null ? otherCharges : 0.0;
    }

    public void setOtherCharges(Double otherCharges) {
        this.otherCharges = otherCharges;
    }

    public Double getFinancePrincipal() {
        return financePrincipal != null ? financePrincipal : 0.0;
    }

    public void setFinancePrincipal(Double financePrincipal) {
        this.financePrincipal = financePrincipal;
    }

    public Double getInterestRate() {
        return interestRate != null ? interestRate : 0.0;
    }

    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }

    public String getInterestType() {
        return interestType != null ? interestType : "Flat";
    }

    public void setInterestType(String interestType) {
        this.interestType = interestType;
    }

    public Integer getTenure() {
        return tenure != null ? tenure : 1;
    }

    public void setTenure(Integer tenure) {
        this.tenure = tenure;
    }

    public Integer getNumberOfEmis() {
        return numberOfEmis != null ? numberOfEmis : 1;
    }

    public void setNumberOfEmis(Integer numberOfEmis) {
        this.numberOfEmis = numberOfEmis;
    }

    public Double getEmiAmount() {
        return emiAmount != null ? emiAmount : 0.0;
    }

    public void setEmiAmount(Double emiAmount) {
        this.emiAmount = emiAmount;
    }

    public Double getProcessingFee() {
        return processingFee != null ? processingFee : 0.0;
    }

    public void setProcessingFee(Double processingFee) {
        this.processingFee = processingFee;
    }

    public String getStatus() {
        return status != null ? status : "Active";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getFinanceStartDate() {
        return financeStartDate;
    }

    public void setFinanceStartDate(LocalDate financeStartDate) {
        this.financeStartDate = financeStartDate;
    }

    public LocalDate getFirstEmiDate() {
        return firstEmiDate;
    }

    public void setFirstEmiDate(LocalDate firstEmiDate) {
        this.firstEmiDate = firstEmiDate;
    }

    public Double getTotalRepayment() {
        return totalRepayment != null ? totalRepayment : 0.0;
    }

    public void setTotalRepayment(Double totalRepayment) {
        this.totalRepayment = totalRepayment;
    }

    public Double getTotalInterest() {
        return totalInterest != null ? totalInterest : 0.0;
    }

    public void setTotalInterest(Double totalInterest) {
        this.totalInterest = totalInterest;
    }

    public Double getTotalPaid() {
        return totalPaid != null ? totalPaid : 0.0;
    }

    public void setTotalPaid(Double totalPaid) {
        this.totalPaid = totalPaid;
    }

    public Double getTotalOutstanding() {
        return totalOutstanding != null ? totalOutstanding : 0.0;
    }

    public void setTotalOutstanding(Double totalOutstanding) {
        this.totalOutstanding = totalOutstanding;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
