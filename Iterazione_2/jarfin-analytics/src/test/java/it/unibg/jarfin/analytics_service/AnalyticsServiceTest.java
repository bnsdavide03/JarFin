package it.unibg.jarfin.analytics_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;

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
import it.unibg.jarfin.analytics_service.service.AnalyticsService;

@ExtendWith(MockitoExtension.class)
public class AnalyticsServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AnalyticsService service;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(service, "accountingUrl", "http://mock-url");
    }

    @Test
    public void testGenerateReport_Success() {
        TransactionDTO t1 = new TransactionDTO();
        t1.setAmount(new BigDecimal("1000.00"));
        t1.setCategory("Stipendio");
        t1.setType(TransactionType.INCOME);
        t1.setDate(LocalDate.now());

        TransactionDTO t2 = new TransactionDTO();
        t2.setAmount(new BigDecimal("200.00"));
        t2.setCategory("Cibo");
        t2.setType(TransactionType.EXPENSE);
        t2.setDate(LocalDate.now());

        TransactionDTO[] mockResponse = {t1, t2};

        when(restTemplate.getForObject(anyString(), eq(TransactionDTO[].class)))
                .thenReturn(mockResponse);

        FinancialReportDTO report = service.generateReport();

        assertNotNull(report);
        assertEquals(new BigDecimal("1000.00"), report.getTotalIncomes());
        assertEquals(new BigDecimal("200.00"), report.getTotalExpenses());
        assertEquals(new BigDecimal("800.00"), report.getTotalBalance());
    }
}