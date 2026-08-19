package com.bikefinance.controller;

import com.bikefinance.EmiRecord;
import com.bikefinance.repository.EmiRecordRepository;
import com.bikefinance.service.FinanceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/emis")
public class EmiController {

    private final EmiRecordRepository emiRecordRepository;
    private final FinanceService financeService;

    public EmiController(EmiRecordRepository emiRecordRepository,
                         FinanceService financeService) {
        this.emiRecordRepository = emiRecordRepository;
        this.financeService = financeService;
    }

    @GetMapping
    public String listEmis(@RequestParam(value = "status", required = false) String status, Model model) {
        List<EmiRecord> emis;
        if ("overdue".equalsIgnoreCase(status)) {
            emis = emiRecordRepository.findOverdueEmis(LocalDate.now());
        } else if (status != null && !status.trim().isEmpty()) {
            emis = emiRecordRepository.findByStatus(status);
        } else {
            emis = emiRecordRepository.findAll();
        }

        model.addAttribute("emis", emis);
        model.addAttribute("currentStatus", status);
        model.addAttribute("activeTab", "emis");
        return "emis";
    }

    @PostMapping("/update-status/{id}")
    public String updateEmiStatus(@PathVariable("id") Integer id,
                                  @RequestParam("status") String status,
                                  RedirectAttributes redirectAttributes) {
        try {
            EmiRecord emi = emiRecordRepository.findById(id).orElseThrow();
            emi.setStatus(status);
            if ("Paid".equals(status)) {
                emi.setPaidAmount(emi.getEmiAmount());
                emi.setRemainingAmount(0.0);
                emi.setPaymentDate(LocalDate.now());
            } else if ("Pending".equals(status)) {
                emi.setPaidAmount(0.0);
                emi.setRemainingAmount(emi.getEmiAmount());
                emi.setPaymentDate(null);
            }
            emiRecordRepository.save(emi);

            if (emi.getFinanceAccount() != null) {
                financeService.recalculateAccountTotals(emi.getFinanceAccount().getFinanceId());
            }
            redirectAttributes.addFlashAttribute("successMessage", "EMI status updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating EMI status: " + e.getMessage());
        }
        return "redirect:/emis";
    }
}
