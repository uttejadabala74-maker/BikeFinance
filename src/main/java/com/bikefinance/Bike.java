package com.bikefinance;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "bikes")
public class Bike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bike_id")
    private Integer bikeId;

    @Column(name = "company")
    private String company;

    @Column(name = "model")
    private String model;

    @Column(name = "variant")
    private String variant;

    @Column(name = "colour")
    private String colour;

    @Column(name = "manufacturing_year")
    private String manufacturingYear;

    @Column(name = "chassis_number")
    private String chassisNumber;

    @Column(name = "engine_number")
    private String engineNumber;

    @Column(name = "registration_number")
    private String registrationNumber;

    @Column(name = "price")
    private Double price = 0.0;

    @Column(name = "bike_actual_price")
    private Double bikeActualPrice = 0.0;

    @Column(name = "down_payment")
    private Double downPayment = 0.0;

    @Column(name = "finance_principal")
    private Double financePrincipal = 0.0;

    @Column(name = "selling_date")
    private LocalDate sellingDate;

    @Column(name = "status")
    private String status = "Available"; // Available, Sold, Financed, Completed

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public Bike() {
    }

    public Integer getBikeId() {
        return bikeId;
    }

    public void setBikeId(Integer bikeId) {
        this.bikeId = bikeId;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getManufacturingYear() {
        return manufacturingYear;
    }

    public void setManufacturingYear(String manufacturingYear) {
        this.manufacturingYear = manufacturingYear;
    }

    public String getChassisNumber() {
        return chassisNumber;
    }

    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }

    public String getEngineNumber() {
        return engineNumber;
    }

    public void setEngineNumber(String engineNumber) {
        this.engineNumber = engineNumber;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public Double getPrice() {
        return (bikeActualPrice != null && bikeActualPrice > 0) ? bikeActualPrice : (price != null ? price : 0.0);
    }

    public void setPrice(Double price) {
        this.price = price;
        if (this.bikeActualPrice == null || this.bikeActualPrice == 0.0) {
            this.bikeActualPrice = price;
        }
    }

    public Double getBikeActualPrice() {
        return (bikeActualPrice != null && bikeActualPrice > 0) ? bikeActualPrice : (price != null ? price : 0.0);
    }

    public void setBikeActualPrice(Double bikeActualPrice) {
        this.bikeActualPrice = bikeActualPrice;
        this.price = bikeActualPrice;
    }

    public Double getDownPayment() {
        return downPayment != null ? downPayment : 0.0;
    }

    public void setDownPayment(Double downPayment) {
        this.downPayment = downPayment;
    }

    public Double getFinancePrincipal() {
        return financePrincipal != null ? financePrincipal : 0.0;
    }

    public void setFinancePrincipal(Double financePrincipal) {
        this.financePrincipal = financePrincipal;
    }

    public LocalDate getSellingDate() {
        return sellingDate;
    }

    public void setSellingDate(LocalDate sellingDate) {
        this.sellingDate = sellingDate;
    }

    public String getStatus() {
        return status != null ? status : "Available";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}