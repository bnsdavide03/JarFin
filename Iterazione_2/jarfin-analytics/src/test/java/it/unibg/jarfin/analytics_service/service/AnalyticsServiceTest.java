package it.unibg.jarfin.analytics_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import it.unibg.jarfin.analytics_service.dto.FinancialReportDTO;
import it.unibg.jarfin.analytics_service.dto.TransactionDTO;
import it.unibg.jarfin.analytics_service.dto.TransactionType;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AnalyticsService analyticsService;

    private static final String ACCOUNTING_URL = "http://accounting-service/transactions";

    @BeforeEach
    void setUp() {
        // Simula @Value
        ReflectionTestUtils.setField(analyticsService, "accountingUrl", ACCOUNTING_URL);
    }

    @Test
    void generateReport_withIncomesAndExpenses_shouldCalculateCorrectly() {
        TransactionDTO income = new TransactionDTO();
        income.setAmount(new BigDecimal("1000"));
        income.setType(TransactionType.INCOME);

        TransactionDTO expense1 = new TransactionDTO();
        expense1.setAmount(new BigDecimal("200"));
        expense1.setType(TransactionType.EXPENSE);
        expense1.setCategory("Food");

        TransactionDTO expense2 = new TransactionDTO();
        expense2.setAmount(new BigDecimal("300"));
        expense2.setType(TransactionType.EXPENSE);
        expense2.setCategory("Rent");

        when(restTemplate.getForObject(ACCOUNTING_URL, TransactionDTO[].class))
                .thenReturn(new TransactionDTO[]{ income, expense1, expense2 });

        FinancialReportDTO report = analyticsService.generateReport();

        assertEquals(new BigDecimal("1000"), report.getTotalIncomes());
        assertEquals(new BigDecimal("500"), report.getTotalExpenses());
        assertEquals(new BigDecimal("500"), report.getTotalBalance());

        Map<String, BigDecimal> categories = report.getBreakdownByCategory();
        assertEquals(new BigDecimal("200"), categories.get("Food"));
        assertEquals(new BigDecimal("300"), categories.get("Rent"));

        assertNotNull(report.getSavingsRate());
        assertTrue(report.getSavingsRate().compareTo(BigDecimal.ZERO) > 0);
        assertNotNull(report.getAlertLevel());
        assertNotNull(report.getFinancialAdvice());
    }

    @Test
    void generateReport_noTransactions_shouldReturnEmptyReport() {
        when(restTemplate.getForObject(ACCOUNTING_URL, TransactionDTO[].class))
                .thenReturn(new TransactionDTO[0]);

        FinancialReportDTO report = analyticsService.generateReport();

        assertEquals(BigDecimal.ZERO, report.getTotalIncomes());
        assertEquals(BigDecimal.ZERO, report.getTotalExpenses());
        assertEquals(BigDecimal.ZERO, report.getTotalBalance());
        assertTrue(report.getBreakdownByCategory().isEmpty());
    }

    @Test
    void generateReport_onlyExpenses_shouldTriggerRedAlert() {
        TransactionDTO expense = new TransactionDTO();
        expense.setAmount(new BigDecimal("150"));
        expense.setType(TransactionType.EXPENSE);
        expense.setCategory("Food");

        when(restTemplate.getForObject(ACCOUNTING_URL, TransactionDTO[].class))
                .thenReturn(new TransactionDTO[]{ expense });

        FinancialReportDTO report = analyticsService.generateReport();

        assertEquals(BigDecimal.ZERO, report.getTotalIncomes());
        assertEquals(new BigDecimal("150"), report.getTotalExpenses());
        assertEquals(new BigDecimal("-150"), report.getTotalBalance());

        assertEquals("RED - Critical", report.getAlertLevel());
        assertEquals(new BigDecimal("-100"), report.getSavingsRate());
    }
}
