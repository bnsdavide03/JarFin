package it.unibg.jarfin.analytics_service.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import it.unibg.jarfin.analytics_service.dto.FinancialReportDTO;
import it.unibg.jarfin.analytics_service.dto.TransactionDTO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    @Value("${accounting.service.url}")
    private String accountingUrl;

    private final RestTemplate restTemplate;

    public FinancialReportDTO generateReport() {
        // FIX Copilot: Usa DTO tipizzato invece di List<Map> raw
        TransactionDTO[] response = restTemplate.getForObject(accountingUrl, TransactionDTO[].class);
        
        if (response == null || response.length == 0) {
            return new FinancialReportDTO(); 
        }

        List<TransactionDTO> transactions = Arrays.asList(response);

        BigDecimal incomes = BigDecimal.ZERO;
        BigDecimal expenses = BigDecimal.ZERO;
        Map<String, BigDecimal> categories = new HashMap<>();

        for (TransactionDTO t : transactions) {
            // FIX Copilot: Uso BigDecimal per evitare errori di precisione Double
            BigDecimal amount = t.getAmount();

            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                incomes = incomes.add(amount);
            } else {
                BigDecimal absAmount = amount.abs();
                expenses = expenses.add(absAmount);
                categories.merge(t.getCategory(), absAmount, BigDecimal::add);
            }
        }

        FinancialReportDTO report = new FinancialReportDTO();
        report.setTotalIncomes(incomes);
        report.setTotalExpenses(expenses);
        report.setTotalBalance(incomes.subtract(expenses));
        report.setBreakdownByCategory(categories);

        calculateProjections(report, incomes, expenses);

        return report;
    }

    private void calculateProjections(FinancialReportDTO report, BigDecimal incomes, BigDecimal expenses) {
        LocalDate today = LocalDate.now();
        int dayOfMonth = today.getDayOfMonth();
        int daysInMonth = today.lengthOfMonth(); // FIX Copilot: Usa i giorni reali del mese (es. 28, 30, 31)
        
        if (dayOfMonth > 0) {
            BigDecimal dailyAverage = expenses.divide(BigDecimal.valueOf(dayOfMonth), 2, RoundingMode.HALF_UP);
            BigDecimal projected = dailyAverage.multiply(BigDecimal.valueOf(daysInMonth));
            report.setProjectedMonthlyExpenses(projected);
        }

        // FIX Copilot: Gestione divisione per zero e alert coerenti
        if (incomes.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal balance = incomes.subtract(expenses);
            BigDecimal rate = balance.divide(incomes, 4, RoundingMode.HALF_UP)
                                     .multiply(BigDecimal.valueOf(100));
            report.setSavingsRate(rate);

            if (rate.compareTo(new BigDecimal("20")) > 0) {
                report.setAlertLevel("GREEN - Ottimo risparmio");
            } else if (rate.compareTo(BigDecimal.ZERO) > 0) {
                report.setAlertLevel("YELLOW - Attenzione alle spese");
            } else {
                report.setAlertLevel("RED - Bilancio in negativo!");
            }
        } else {
            // Caso limite: Nessuna entrata
            report.setSavingsRate(BigDecimal.ZERO);
            report.setAlertLevel("RED - Nessuna entrata rilevata");
        }
    }
}