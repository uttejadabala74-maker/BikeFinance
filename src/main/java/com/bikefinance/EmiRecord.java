package com.bikefinance;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "emis")
public class EmiRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emi_id")
    private Integer emiId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "finance_id", nullable = false)
    private FinanceAccount financeAccount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bike_id", nullable = false)
    private Bike bike;

    @Column(name = "emi_number", nullable = false)
    private Integer emiNumber;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "emi_amount", nullable = false)
    private Double emiAmount = 0.0;

    @Column(name = "paid_amount")
    private Double paidAmount = 0.0;

    @Column(name = "remaining_amount")
    private Double remainingAmount = 0.0;

    @Column(name = "status")
    private String status = "Pending"; // Pending, Paid, Partially Paid, Overdue, Waived, Cancelled

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "late_fee")
    private Double lateFee = 0.0;

    @Column(name = "notes")
    private String notes;

    public EmiRecord() {
    }

    public Integer getEmiId() {
        return emiId;
    }

    public void setEmiId(Integer emiId) {
        this.emiId = emiId;
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

    public Integer getEmiNumber() {
        return emiNumber;
    }

    public void setEmiNumber(Integer emiNumber) {
        this.emiNumber = emiNumber;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Double getEmiAmount() {
        return emiAmount != null ? emiAmount : 0.0;
    }

    public void setEmiAmount(Double emiAmount) {
        this.emiAmount = emiAmount;
    }

    public Double getPaidAmount() {
        return paidAmount != null ? paidAmount : 0.0;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Double getRemainingAmount() {
        return remainingAmount != null ? remainingAmount : (getEmiAmount() - getPaidAmount());
    }

    public void setRemainingAmount(Double remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public String getStatus() {
        return status != null ? status : "Pending";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Double getLateFee() {
        return lateFee != null ? lateFee : 0.0;
    }

    public void setLateFee(Double lateFee) {
        this.lateFee = lateFee;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
