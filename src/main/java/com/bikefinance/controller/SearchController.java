package com.bikefinance.controller;

import com.bikefinance.*;
import com.bikefinance.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class SearchController {

    private final CustomerRepository customerRepository;
    private final BikeRepository bikeRepository;
    private final FinanceAccountRepository financeAccountRepository;

    public SearchController(CustomerRepository customerRepository,
                            BikeRepository bikeRepository,
                            FinanceAccountRepository financeAccountRepository) {
        this.customerRepository = customerRepository;
        this.bikeRepository = bikeRepository;
        this.financeAccountRepository = financeAccountRepository;
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "q", required = false) String query, Model model) {
        if (query != null && !query.trim().isEmpty()) {
            String q = query.trim();
            List<Customer> matchingCustomers = customerRepository.searchCustomers(q);
            List<Bike> matchingBikes = bikeRepository.searchBikes(q);
            List<FinanceAccount> matchingFinance = financeAccountRepository.searchFinanceAccounts(q);

            model.addAttribute("query", q);
            model.addAttribute("matchingCustomers", matchingCustomers);
            model.addAttribute("matchingBikes", matchingBikes);
            model.addAttribute("matchingFinance", matchingFinance);
        } else {
            model.addAttribute("query", "");
        }
        model.addAttribute("activeTab", "search");
        return "search";
    }
}
