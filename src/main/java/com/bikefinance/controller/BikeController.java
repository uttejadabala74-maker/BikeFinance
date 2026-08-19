package com.bikefinance.controller;

import com.bikefinance.*;
import com.bikefinance.repository.*;
import com.bikefinance.service.FinanceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/bikes")
public class BikeController {

    private final BikeRepository bikeRepository;
    private final CustomerRepository customerRepository;
    private final FinanceAccountRepository financeAccountRepository;
    private final EmiRecordRepository emiRecordRepository;
    private final PaymentRepository paymentRepository;
    private final ChallanRepository challanRepository;
    private final FinanceService financeService;

    public BikeController(BikeRepository bikeRepository,
                          CustomerRepository customerRepository,
                          FinanceAccountRepository financeAccountRepository,
                          EmiRecordRepository emiRecordRepository,
                          PaymentRepository paymentRepository,
                          ChallanRepository challanRepository,
                          FinanceService financeService) {
        this.bikeRepository = bikeRepository;
        this.customerRepository = customerRepository;
        this.financeAccountRepository = financeAccountRepository;
        this.emiRecordRepository = emiRecordRepository;
        this.paymentRepository = paymentRepository;
        this.challanRepository = challanRepository;
        this.financeService = financeService;
    }

    @GetMapping
    public String listBikes(Model model) {
        model.addAttribute("bikes", bikeRepository.findAll());
        model.addAttribute("activeTab", "bikes");
        return "bikes";
    }

    @GetMapping("/add")
    public String showAddBikeForm(Model model) {
        model.addAttribute("bike", new Bike());
        model.addAttribute("customers", customerRepository.findAll());
        model.addAttribute("activeTab", "bikes");
        return "add-bike";
    }

    @PostMapping("/save")
    public String saveBike(@ModelAttribute Bike bike,
                           @RequestParam(value = "customerId", required = false) Integer customerId,
                           RedirectAttributes redirectAttributes) {
        try {
            if (customerId != null && customerId > 0) {
                Customer customer = customerRepository.findById(customerId).orElse(null);
                bike.setCustomer(customer);
            }
            if (bike.getStatus() == null || bike.getStatus().isEmpty()) {
                bike.setStatus(bike.getCustomer() != null ? "Financed" : "Available");
            }
            bikeRepository.save(bike);
            redirectAttributes.addFlashAttribute("successMessage", "Bike saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving bike: " + e.getMessage());
        }
        return "redirect:/bikes";
    }

    @GetMapping("/detail/{id}")
    public String viewBikeDetail(@PathVariable("id") Integer id, Model model) {
        Bike bike = bikeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Bike ID: " + id));

        // Retrieve associated Finance Account, EMIs, Payments, Challans
        FinanceAccount finance = financeAccountRepository.findByBikeBikeId(id).orElse(null);
        List<EmiRecord> emis = emiRecordRepository.findByBikeBikeId(id);
        List<Payment> payments = paymentRepository.findByBikeBikeId(id);
        List<Challan> challans = challanRepository.findByBikeBikeId(id);

        if (finance != null) {
            financeService.recalculateAccountTotals(finance.getFinanceId());
        }

        model.addAttribute("bike", bike);
        model.addAttribute("finance", finance);
        model.addAttribute("emis", emis);
        model.addAttribute("payments", payments);
        model.addAttribute("challans", challans);
        model.addAttribute("activeTab", "bikes");

        return "bike-detail";
    }

    @GetMapping("/edit/{id}")
    public String showEditBikeForm(@PathVariable("id") Integer id, Model model) {
        Bike bike = bikeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Bike ID: " + id));
        model.addAttribute("bike", bike);
        model.addAttribute("customers", customerRepository.findAll());
        model.addAttribute("activeTab", "bikes");
        return "edit-bike";
    }

    @PostMapping("/update")
    public String updateBike(@ModelAttribute Bike bike,
                             @RequestParam(value = "customerId", required = false) Integer customerId,
                             RedirectAttributes redirectAttributes) {
        try {
            Bike existing = bikeRepository.findById(bike.getBikeId()).orElseThrow();
            existing.setCompany(bike.getCompany());
            existing.setModel(bike.getModel());
            existing.setVariant(bike.getVariant());
            existing.setColour(bike.getColour());
            existing.setManufacturingYear(bike.getManufacturingYear());
            existing.setChassisNumber(bike.getChassisNumber());
            existing.setEngineNumber(bike.getEngineNumber());
            existing.setRegistrationNumber(bike.getRegistrationNumber());
            existing.setBikeActualPrice(bike.getBikeActualPrice());
            existing.setStatus(bike.getStatus());

            if (customerId != null && customerId > 0) {
                Customer customer = customerRepository.findById(customerId).orElse(null);
                existing.setCustomer(customer);
            } else {
                existing.setCustomer(null);
            }

            bikeRepository.save(existing);
            redirectAttributes.addFlashAttribute("successMessage", "Bike updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating bike: " + e.getMessage());
        }
        return "redirect:/bikes";
    }

    @PostMapping("/delete/{id}")
    public String deleteBike(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            bikeRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Bike deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete bike with associated records.");
        }
        return "redirect:/bikes";
    }
}