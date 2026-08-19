package com.bikefinance;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer paymentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "finance_id", nullable = false)
    private FinanceAccount financeAccount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bike_id", nullable = false)
    private Bike bike;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "emi_id")
    private EmiRecord emiRecord;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @Column(name = "amount")
    private Double amount = 0.0;

    @Column(name = "amount_paid")
    private Double amountPaid = 0.0;

    @Column(name = "payment_mode")
    private String paymentMode = "Cash"; // Cash, UPI, Bank Transfer, Cheque, Other

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "transaction_number")
    private String transactionNumber;

    @Column(name = "collected_by")
    private String collectedBy;

    @Column(name = "notes")
    private String notes;

    @PrePersist
    @PreUpdate
    protected void syncFields() {
        if (amountPaid == null || amountPaid == 0.0) {
            if (amount != null && amount > 0.0) {
                amountPaid = amount;
            }
        }
        if (amount == null || amount == 0.0) {
            amount = amountPaid;
        }
        if (paymentMethod == null || paymentMethod.isEmpty()) {
            paymentMethod = paymentMode;
        }
    }

    public Payment() {
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public FinanceAccount getFinanceAccount() {
        return financeAccount;
    }

    public void setFinanceAccount(FinanceAccount financeAccount) {
        this.financeAccount = financeAccount;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Bike getBike() {
        return bike;
    }

    public void setBike(Bike bike) {
        this.bike = bike;
    }

    public EmiRecord getEmiRecord() {
        return emiRecord;
    }

    public void setEmiRecord(EmiRecord emiRecord) {
        this.emiRecord = emiRecord;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Double getAmount() {
        return amount != null ? amount : (amountPaid != null ? amountPaid : 0.0);
    }

    public void setAmount(Double amount) {
        this.amount = amount;
        this.amountPaid = amount;
    }

    public Double getAmountPaid() {
        return amountPaid != null && amountPaid > 0.0 ? amountPaid : (amount != null ? amount : 0.0);
    }

    public void setAmountPaid(Double amountPaid) {
        this.amountPaid = amountPaid;
        this.amount = amountPaid;
    }

    public String getPaymentMode() {
        return paymentMode != null ? paymentMode : "Cash";
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
        this.paymentMethod = paymentMode;
    }

    public String getPaymentMethod() {
        return paymentMethod != null ? paymentMethod : paymentMode;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(String transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public String getCollectedBy() {
        return collectedBy;
    }

    public void setCollectedBy(String collectedBy) {
        this.collectedBy = collectedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
