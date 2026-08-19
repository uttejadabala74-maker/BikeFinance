package com.bikefinance.controller;

import com.bikefinance.*;
import com.bikefinance.repository.*;
import com.bikefinance.service.FinanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/finance")
public class FinanceController {

    private final FinanceAccountRepository financeAccountRepository;
    private final BikeRepository bikeRepository;
    private final CustomerRepository customerRepository;
    private final EmiRecordRepository emiRecordRepository;
    private final PaymentRepository paymentRepository;
    private final FinanceService financeService;

    public FinanceController(FinanceAccountRepository financeAccountRepository,
                             BikeRepository bikeRepository,
                             CustomerRepository customerRepository,
                             EmiRecordRepository emiRecordRepository,
                             PaymentRepository paymentRepository,
                             FinanceService financeService) {
        this.financeAccountRepository = financeAccountRepository;
        this.bikeRepository = bikeRepository;
        this.customerRepository = customerRepository;
        this.emiRecordRepository = emiRecordRepository;
        this.paymentRepository = paymentRepository;
        this.financeService = financeService;
    }

    @GetMapping
    public String listFinanceAccounts(Model model) {
        List<FinanceAccount> accounts = financeAccountRepository.findAll();
        for (FinanceAccount acc : accounts) {
            financeService.recalculateAccountTotals(acc.getFinanceId());
        }
        model.addAttribute("financeAccounts", accounts);
        model.addAttribute("activeTab", "finance");
        return "finance";
    }

    @GetMapping("/add")
    public String showAddFinanceForm(@RequestParam(value = "bikeId", required = false) Integer bikeId,
                                    @RequestParam(value = "customerId", required = false) Integer customerId,
                                    Model model) {
        FinanceAccount account = new FinanceAccount();
        account.setFinanceStartDate(LocalDate.now());
        account.setFirstEmiDate(LocalDate.now().plusMonths(1));
        account.setInterestRate(1.8);
        account.setTenure(10);
        account.setNumberOfEmis(10);
        account.setFinanceCompany("Sri Sai Teja Finance");

        if (bikeId != null && bikeId > 0) {
            Bike bike = bikeRepository.findById(bikeId).orElse(null);
            if (bike != null) {
                account.setBike(bike);
                account.setBikeActualPrice(bike.getBikeActualPrice());
                account.setDownPayment(bike.getDownPayment());
                if (bike.getCustomer() != null) {
                    account.setCustomer(bike.getCustomer());
                }
            }
        }

        if (customerId != null && customerId > 0 && account.getCustomer() == null) {
            Customer customer = customerRepository.findById(customerId).orElse(null);
            account.setCustomer(customer);
        }

        model.addAttribute("account", account);
        model.addAttribute("bikes", bikeRepository.findAll());
        model.addAttribute("customers", customerRepository.findAll());
        model.addAttribute("activeTab", "finance");
        return "add-finance";
    }

    @PostMapping("/save")
    public String saveFinanceAccount(@ModelAttribute FinanceAccount account,
                                     @RequestParam("bikeId") Integer bikeId,
                                     @RequestParam("customerId") Integer customerId,
                                     RedirectAttributes redirectAttributes) {
        try {
            Bike bike = bikeRepository.findById(bikeId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Bike selected"));
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Customer selected"));

            account.setBike(bike);
            account.setCustomer(customer);

            financeService.createFinanceAccount(account);
            redirectAttributes.addFlashAttribute("successMessage", "Finance Account created & EMI Schedule generated successfully!");
            return "redirect:/finance/detail/" + account.getFinanceId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving finance account: " + e.getMessage());
            return "redirect:/finance/add";
        }
    }

    @GetMapping("/detail/{id}")
    public String viewFinanceDetail(@PathVariable("id") Integer id, Model model) {
        financeService.recalculateAccountTotals(id);
        FinanceAccount account = financeAccountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Finance ID: " + id));

        List<EmiRecord> emis = emiRecordRepository.findByFinanceAccountFinanceIdOrderByEmiNumberAsc(id);
        List<Payment> payments = paymentRepository.findByFinanceAccountFinanceIdOrderByPaymentDateDesc(id);

        model.addAttribute("account", account);
        model.addAttribute("emis", emis);
        model.addAttribute("payments", payments);
        model.addAttribute("activeTab", "finance");
        return "finance-detail";
    }

    @GetMapping("/edit/{id}")
    public String showEditFinanceForm(@PathVariable("id") Integer id, Model model) {
        FinanceAccount account = financeAccountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Finance ID: " + id));

        model.addAttribute("account", account);
        model.addAttribute("bikes", bikeRepository.findAll());
        model.addAttribute("customers", customerRepository.findAll());
        model.addAttribute("activeTab", "finance");
        return "edit-finance";
    }

    @PostMapping("/update")
    public String updateFinanceAccount(@ModelAttribute FinanceAccount account,
                                       RedirectAttributes redirectAttributes) {
        try {
            FinanceAccount existing = financeAccountRepository.findById(account.getFinanceId()).orElseThrow();
            existing.setLoanAccountNumber(account.getLoanAccountNumber());
            existing.setFinanceCompany(account.getFinanceCompany());
            existing.setStatus(account.getStatus());
            existing.setNotes(account.getNotes());

            financeAccountRepository.save(existing);
            financeService.recalculateAccountTotals(existing.getFinanceId());
            redirectAttributes.addFlashAttribute("successMessage", "Finance Account details updated successfully!");
            return "redirect:/finance/detail/" + existing.getFinanceId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating finance account: " + e.getMessage());
            return "redirect:/finance";
        }
    }

    @GetMapping("/calculate")
    @ResponseBody
    public ResponseEntity<FinanceService.CalculationResult> calculate(
            @RequestParam("price") Double price,
            @RequestParam("downPayment") Double downPayment,
            @RequestParam("agreement") Double agreement,
            @RequestParam("other") Double other,
            @RequestParam("rate") Double rate,
            @RequestParam("tenure") Integer tenure,
            @RequestParam(value = "emis", required = false) Integer emis) {

        int numEmis = (emis != null && emis > 0) ? emis : (tenure != null ? tenure : 1);
        FinanceService.CalculationResult res = financeService.calculateFinance(
                price, downPayment, agreement, other, rate, tenure, numEmis
        );

        return ResponseEntity.ok(res);
    }
}
