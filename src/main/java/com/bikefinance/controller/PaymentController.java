package com.bikefinance.controller;

import com.bikefinance.*;
import com.bikefinance.repository.*;
import com.bikefinance.service.FinanceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final FinanceAccountRepository financeAccountRepository;
    private final EmiRecordRepository emiRecordRepository;
    private final FinanceService financeService;

    public PaymentController(PaymentRepository paymentRepository,
                             FinanceAccountRepository financeAccountRepository,
                             EmiRecordRepository emiRecordRepository,
                             FinanceService financeService) {
        this.paymentRepository = paymentRepository;
        this.financeAccountRepository = financeAccountRepository;
        this.emiRecordRepository = emiRecordRepository;
        this.financeService = financeService;
    }

    @GetMapping
    public String listPayments(Model model) {
        model.addAttribute("payments", paymentRepository.findAll());
        model.addAttribute("activeTab", "payments");
        return "payments";
    }

    @GetMapping("/add")
    public String showAddPaymentForm(@RequestParam(value = "financeId", required = false) Integer financeId,
                                     @RequestParam(value = "emiId", required = false) Integer emiId,
                                     Model model) {
        Payment payment = new Payment();
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentMode("Cash");

        if (financeId != null && financeId > 0) {
            FinanceAccount finance = financeAccountRepository.findById(financeId).orElse(null);
            if (finance != null) {
                payment.setFinanceAccount(finance);
                payment.setCustomer(finance.getCustomer());
                payment.setBike(finance.getBike());
                payment.setAmountPaid(finance.getEmiAmount());
            }
        }

        if (emiId != null && emiId > 0) {
            EmiRecord emi = emiRecordRepository.findById(emiId).orElse(null);
            if (emi != null) {
                payment.setEmiRecord(emi);
                payment.setFinanceAccount(emi.getFinanceAccount());
                payment.setCustomer(emi.getCustomer());
                payment.setBike(emi.getBike());
                payment.setAmountPaid(emi.getRemainingAmount());
            }
        }

        model.addAttribute("payment", payment);
        model.addAttribute("financeAccounts", financeAccountRepository.findByStatus("Active"));
        model.addAttribute("activeTab", "payments");
        return "add-payment";
    }

    @PostMapping("/save")
    public String savePayment(@ModelAttribute Payment payment,
                              @RequestParam("financeId") Integer financeId,
                              @RequestParam(value = "emiId", required = false) Integer emiId,
                              RedirectAttributes redirectAttributes) {
        try {
            FinanceAccount finance = financeAccountRepository.findById(financeId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Finance Account selected"));

            payment.setFinanceAccount(finance);

            if (emiId != null && emiId > 0) {
                EmiRecord emi = emiRecordRepository.findById(emiId).orElse(null);
                payment.setEmiRecord(emi);
            }

            financeService.recordPayment(payment);
            redirectAttributes.addFlashAttribute("successMessage", "Payment recorded successfully! Total outstanding updated.");
            return "redirect:/finance/detail/" + financeId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error recording payment: " + e.getMessage());
            return "redirect:/payments/add";
        }
    }
}
