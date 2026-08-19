package com.bikefinance;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "challans")
public class Challan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challan_id")
    private Integer challanId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bike_id", nullable = false)
    private Bike bike;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "registration_number")
    private String registrationNumber;

    @Column(name = "challan_number", nullable = false)
    private String challanNumber;

    @Column(name = "challan_date")
    private LocalDate challanDate;

    @Column(name = "amount")
    private Double amount = 0.0;

    @Column(name = "challan_amount")
    private Double challanAmount = 0.0;

    @Column(name = "reason")
    private String reason;

    @Column(name = "location")
    private String location;

    @Column(name = "status")
    private String status = "Pending"; // Pending, Paid, Disputed, Cancelled

    @Column(name = "payment_status")
    private String paymentStatus;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "notes")
    private String notes;

    @PrePersist
    @PreUpdate
    protected void syncFields() {
        if (challanAmount == null || challanAmount == 0.0) {
            challanAmount = amount;
        }
        if (amount == null || amount == 0.0) {
            amount = challanAmount;
        }
        if (paymentStatus == null || paymentStatus.isEmpty()) {
            paymentStatus = status;
        }
    }

    public Challan() {
    }

    public Integer getChallanId() {
        return challanId;
    }

    public void setChallanId(Integer challanId) {
        this.challanId = challanId;
    }

    public Bike getBike() {
        return bike;
    }

    public void setBike(Bike bike) {
        this.bike = bike;
        if (bike != null && (this.registrationNumber == null || this.registrationNumber.isEmpty())) {
            this.registrationNumber = bike.getRegistrationNumber();
        }
        if (bike != null && bike.getCustomer() != null && this.customer == null) {
            this.customer = bike.getCustomer();
        }
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getChallanNumber() {
        return challanNumber;
    }

    public void setChallanNumber(String challanNumber) {
        this.challanNumber = challanNumber;
    }

    public LocalDate getChallanDate() {
        return challanDate;
    }

    public void setChallanDate(LocalDate challanDate) {
        this.challanDate = challanDate;
    }

    public Double getAmount() {
        return amount != null ? amount : (challanAmount != null ? challanAmount : 0.0);
    }

    public void setAmount(Double amount) {
        this.amount = amount;
        this.challanAmount = amount;
    }

    public Double getChallanAmount() {
        return challanAmount != null ? challanAmount : (amount != null ? amount : 0.0);
    }

    public void setChallanAmount(Double challanAmount) {
        this.challanAmount = challanAmount;
        this.amount = challanAmount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status != null ? status : "Pending";
    }

    public void setStatus(String status) {
        this.status = status;
        this.paymentStatus = status;
    }

    public String getPaymentStatus() {
        return paymentStatus != null ? paymentStatus : status;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
