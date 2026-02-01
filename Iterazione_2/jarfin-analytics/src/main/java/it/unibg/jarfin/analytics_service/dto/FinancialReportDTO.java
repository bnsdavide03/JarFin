package it.unibg.jarfin.analytics_service.dto;

import java.math.BigDecimal;
import java.util.Map;
import lombok.Data;

@Data
public class FinancialReportDTO {
    private BigDecimal totalBalance;
    private BigDecimal totalIncomes;
    private BigDecimal totalExpenses;
    
    // Mappa Categoria -> Totale Speso
    private Map<String, BigDecimal> breakdownByCategory;
    
    private BigDecimal projectedMonthlyExpenses; // Proiezione fine mese
    private BigDecimal savingsRate;              // % Risparmio
    
    private String financialAdvice;
    private String alertLevel;
}