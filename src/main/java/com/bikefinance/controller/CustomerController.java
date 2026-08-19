package com.bikefinance.controller;

import com.bikefinance.*;
import com.bikefinance.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerRepository customerRepository;
    private final BikeRepository bikeRepository;
    private final FinanceAccountRepository financeAccountRepository;
    private final EmiRecordRepository emiRecordRepository;
    private final PaymentRepository paymentRepository;

    public CustomerController(CustomerRepository customerRepository,
                              BikeRepository bikeRepository,
                              FinanceAccountRepository financeAccountRepository,
                              EmiRecordRepository emiRecordRepository,
                              PaymentRepository paymentRepository) {
        this.customerRepository = customerRepository;
        this.bikeRepository = bikeRepository;
        this.financeAccountRepository = financeAccountRepository;
        this.emiRecordRepository = emiRecordRepository;
        this.paymentRepository = paymentRepository;
    }

    @GetMapping
    public String listCustomers(Model model) {
        model.addAttribute("customers", customerRepository.findAll());
        model.addAttribute("activeTab", "customers");
        return "customers";
    }

    @GetMapping("/add")
    public String showAddCustomerForm(Model model) {
        model.addAttribute("customer", new Customer());
        model.addAttribute("activeTab", "customers");
        return "add-customer";
    }

    @PostMapping("/save")
    public String saveCustomer(@ModelAttribute Customer customer, RedirectAttributes redirectAttributes) {
        try {
            customerRepository.save(customer);
            redirectAttributes.addFlashAttribute("successMessage", "Customer saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving customer: " + e.getMessage());
        }
        return "redirect:/customers";
    }

    @GetMapping("/detail/{id}")
    public String viewCustomerDetail(@PathVariable("id") Integer id, Model model) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Customer ID: " + id));

        List<Bike> bikes = bikeRepository.findByCustomerCustomerId(id);
        List<FinanceAccount> financeAccounts = financeAccountRepository.findByCustomerCustomerId(id);
        List<EmiRecord> emis = emiRecordRepository.findByCustomerCustomerId(id);
        List<Payment> payments = paymentRepository.findByCustomerCustomerId(id);

        model.addAttribute("customer", customer);
        model.addAttribute("bikes", bikes);
        model.addAttribute("financeAccounts", financeAccounts);
        model.addAttribute("emis", emis);
        model.addAttribute("payments", payments);
        model.addAttribute("activeTab", "customers");

        return "customer-detail";
    }

    @GetMapping("/edit/{id}")
    public String showEditCustomerForm(@PathVariable("id") Integer id, Model model) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Customer ID: " + id));
        model.addAttribute("customer", customer);
        model.addAttribute("activeTab", "customers");
        return "edit-customer";
    }

    @PostMapping("/update")
    public String updateCustomer(@ModelAttribute Customer customer, RedirectAttributes redirectAttributes) {
        try {
            Customer existing = customerRepository.findById(customer.getCustomerId()).orElseThrow();
            existing.setName(customer.getName());
            existing.setPhone(customer.getPhone());
            existing.setAlternatePhone(customer.getAlternatePhone());
            existing.setAddress(customer.getAddress());
            existing.setCityVillage(customer.getCityVillage());
            existing.setDistrict(customer.getDistrict());
            existing.setState(customer.getState());
            existing.setPincode(customer.getPincode());
            existing.setGuardianName(customer.getGuardianName());
            existing.setIdProofDetails(customer.getIdProofDetails());
            existing.setNotes(customer.getNotes());

            customerRepository.save(existing);
            redirectAttributes.addFlashAttribute("successMessage", "Customer updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating customer: " + e.getMessage());
        }
        return "redirect:/customers";
    }

    @PostMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            customerRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Customer deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete customer with associated bikes or loans.");
        }
        return "redirect:/customers";
    }
}