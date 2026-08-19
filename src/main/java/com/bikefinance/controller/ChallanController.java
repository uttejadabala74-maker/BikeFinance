package com.bikefinance.controller;

import com.bikefinance.*;
import com.bikefinance.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/challans")
public class ChallanController {

    private final ChallanRepository challanRepository;
    private final BikeRepository bikeRepository;
    private final CustomerRepository customerRepository;

    public ChallanController(ChallanRepository challanRepository,
                             BikeRepository bikeRepository,
                             CustomerRepository customerRepository) {
        this.challanRepository = challanRepository;
        this.bikeRepository = bikeRepository;
        this.customerRepository = customerRepository;
    }

    @GetMapping
    public String listChallans(Model model) {
        model.addAttribute("challans", challanRepository.findAll());
        model.addAttribute("activeTab", "challans");
        return "challans";
    }

    @GetMapping("/add")
    public String showAddChallanForm(@RequestParam(value = "bikeId", required = false) Integer bikeId, Model model) {
        Challan challan = new Challan();
        challan.setChallanDate(LocalDate.now());

        if (bikeId != null && bikeId > 0) {
            Bike bike = bikeRepository.findById(bikeId).orElse(null);
            if (bike != null) {
                challan.setBike(bike);
                challan.setRegistrationNumber(bike.getRegistrationNumber());
                challan.setCustomer(bike.getCustomer());
            }
        }

        model.addAttribute("challan", challan);
        model.addAttribute("bikes", bikeRepository.findAll());
        model.addAttribute("customers", customerRepository.findAll());
        model.addAttribute("activeTab", "challans");
        return "add-challan";
    }

    @PostMapping("/save")
    public String saveChallan(@ModelAttribute Challan challan,
                             @RequestParam("bikeId") Integer bikeId,
                             RedirectAttributes redirectAttributes) {
        try {
            Bike bike = bikeRepository.findById(bikeId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Bike selected"));

            challan.setBike(bike);
            if (bike.getCustomer() != null) {
                challan.setCustomer(bike.getCustomer());
            }
            if (challan.getRegistrationNumber() == null || challan.getRegistrationNumber().isEmpty()) {
                challan.setRegistrationNumber(bike.getRegistrationNumber());
            }

            challanRepository.save(challan);
            redirectAttributes.addFlashAttribute("successMessage", "Challan recorded successfully!");
            return "redirect:/challans";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error recording challan: " + e.getMessage());
            return "redirect:/challans/add";
        }
    }

    @PostMapping("/update-status/{id}")
    public String updateChallanStatus(@PathVariable("id") Integer id,
                                      @RequestParam("status") String status,
                                      RedirectAttributes redirectAttributes) {
        try {
            Challan challan = challanRepository.findById(id).orElseThrow();
            challan.setStatus(status);
            if ("Paid".equalsIgnoreCase(status)) {
                challan.setPaymentDate(LocalDate.now());
            }
            challanRepository.save(challan);
            redirectAttributes.addFlashAttribute("successMessage", "Challan status updated!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating status: " + e.getMessage());
        }
        return "redirect:/challans";
    }
}
