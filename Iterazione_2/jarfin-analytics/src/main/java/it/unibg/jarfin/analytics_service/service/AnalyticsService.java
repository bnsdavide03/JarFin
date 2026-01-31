package it.unibg.jarfin.analytics_service.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import it.unibg.jarfin.analytics_service.dto.FinancialReportDTO;

@Service
public class AnalyticsService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String ACCOUNTING_URL = "http://localhost:8080/api/transactions";

    public FinancialReportDTO generateReport() {
        List<Map<String, Object>> transactions = restTemplate.getForObject(ACCOUNTING_URL, List.class);
        if (transactions == null) return new FinancialReportDTO();

        double incomes = 0;
        double expenses = 0;
        Map<String, Double> categories = new HashMap<>();

        for (Map<String, Object> t : transactions) {
            double amount = Double.parseDouble(t.get("amount").toString());
            String cat = (String) t.get("category");
            if (amount > 0) incomes += amount;
            else {
                double absAmount = Math.abs(amount);
                expenses += absAmount;
                categories.merge(cat, absAmount, Double::sum);
            }
        }

        FinancialReportDTO report = new FinancialReportDTO();
        report.setTotalIncomes(incomes);
        report.setTotalExpenses(expenses);
        report.setTotalBalance(incomes - expenses);
        report.setBreakdownByCategory(categories);

        int giornoCorrente = java.time.LocalDate.now().getDayOfMonth();
        // Stima lineare: se ho speso X in 15 giorni, in 30 spenderò (X/15)*30
        double projected = (expenses / giornoCorrente) * 30;
        report.setProjectedMonthlyExpenses(projected);

        double rate = (incomes > 0) ? ((incomes - expenses) / incomes) * 100 : 0;
        report.setSavingsRate(rate);
        
        if (rate > 20) report.setAlertLevel("GREEN - Ottimo risparmio");
        else if (rate > 0) report.setAlertLevel("YELLOW - Attenzione alle spese");
        else report.setAlertLevel("RED - Bilancio in negativo!");

        return report;
    }
}