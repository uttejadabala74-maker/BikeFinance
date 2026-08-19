package com.bikefinance.controller;

import com.bikefinance.*;
import com.bikefinance.repository.*;
import com.bikefinance.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;
    private final BikeRepository bikeRepository;
    private final CustomerRepository customerRepository;
    private final FinanceAccountRepository financeAccountRepository;
    private final EmiRecordRepository emiRecordRepository;
    private final PaymentRepository paymentRepository;
    private final ChallanRepository challanRepository;

    public ReportController(ReportService reportService,
                            BikeRepository bikeRepository,
                            CustomerRepository customerRepository,
                            FinanceAccountRepository financeAccountRepository,
                            EmiRecordRepository emiRecordRepository,
                            PaymentRepository paymentRepository,
                            ChallanRepository challanRepository) {
        this.reportService = reportService;
        this.bikeRepository = bikeRepository;
        this.customerRepository = customerRepository;
        this.financeAccountRepository = financeAccountRepository;
        this.emiRecordRepository = emiRecordRepository;
        this.paymentRepository = paymentRepository;
        this.challanRepository = challanRepository;
    }

    @GetMapping
    public String showReports(Model model) {
        model.addAllAttributes(reportService.getDashboardStats());
        model.addAttribute("bikes", bikeRepository.findAll());
        model.addAttribute("customers", customerRepository.findAll());
        model.addAttribute("financeAccounts", financeAccountRepository.findAll());
        model.addAttribute("payments", paymentRepository.findAll());
        model.addAttribute("challans", challanRepository.findAll());
        model.addAttribute("activeTab", "reports");
        return "reports";
    }

    @GetMapping("/export-finance-csv")
    public void exportFinanceCsv(HttpServletResponse response) throws Exception {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"finance_report_" + LocalDate.now() + ".csv\"");

        PrintWriter writer = response.getWriter();
        writer.println("Finance ID,Loan Account No,Finance Company,Customer Name,Phone,Registration No,Bike Model,Actual Price,Down Payment,Principal,Total Repayment,Total Paid,Outstanding,Status");

        List<FinanceAccount> list = financeAccountRepository.findAll();
        for (FinanceAccount f : list) {
            writer.printf("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,\"%s\"\n",
                    f.getFinanceId(),
                    f.getLoanAccountNumber() != null ? f.getLoanAccountNumber() : "",
                    f.getFinanceCompany() != null ? f.getFinanceCompany() : "",
                    f.getCustomer() != null ? f.getCustomer().getName() : "",
                    f.getCustomer() != null ? f.getCustomer().getPhone() : "",
                    f.getBike() != null ? f.getBike().getRegistrationNumber() : "",
                    f.getBike() != null ? f.getBike().getCompany() + " " + f.getBike().getModel() : "",
                    f.getBikeActualPrice(),
                    f.getDownPayment(),
                    f.getFinancePrincipal(),
                    f.getTotalRepayment(),
                    f.getTotalPaid(),
                    f.getTotalOutstanding(),
                    f.getStatus()
            );
        }
        writer.flush();
    }
}
