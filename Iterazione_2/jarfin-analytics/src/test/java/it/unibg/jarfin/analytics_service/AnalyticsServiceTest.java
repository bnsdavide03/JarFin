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
import it.unibg.jarfin.analytics_service.service.AnalyticsService;

@ExtendWith(MockitoExtension.class)
public class AnalyticsServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AnalyticsService service;

    @BeforeEach
    public void setup() {
        // Simuliamo l'iniezione dell'URL (che normalmente fa @Value)
        ReflectionTestUtils.setField(service, "accountingUrl", "http://mock-url");
    }

    @Test
    public void testGenerateReport_Success() {
        // 1. PREPARIAMO I DATI FINTI CHE ARRIVANO DALL'ACCOUNTING
        TransactionDTO t1 = new TransactionDTO();
        t1.setAmount(new BigDecimal("1000.00")); // Stipendio
        t1.setCategory("Stipendio");
        t1.setDate(LocalDate.now());

        TransactionDTO t2 = new TransactionDTO();
        t2.setAmount(new BigDecimal("-200.00")); // Spesa
        t2.setCategory("Cibo");
        t2.setDate(LocalDate.now());

        TransactionDTO[] mockResponse = {t1, t2};

        // 2. ISTRUIAMO IL MOCK: "Quando ti chiamano, rispondi con questi dati"
        when(restTemplate.getForObject(anyString(), eq(TransactionDTO[].class)))
                .thenReturn(mockResponse);

        // 3. ESEGUIAMO IL METODO DA TESTARE
        FinancialReportDTO report = service.generateReport();

        // 4. VERIFICHIAMO I RISULTATI MATEMATICI
        assertNotNull(report);
        
        // Entrate: 1000
        assertEquals(new BigDecimal("1000.00"), report.getTotalIncomes());
        
        // Spese: 200 (valore assoluto)
        assertEquals(new BigDecimal("200.00"), report.getTotalExpenses());
        
        // Bilancio: 1000 - 200 = 800
        assertEquals(new BigDecimal("800.00"), report.getTotalBalance());
        
        // Risparmio: (800 / 1000) * 100 = 80%
        // Nota: BigDecimal compareTo restituisce 0 se sono uguali (ignorando la scala esatta)
        assertEquals(0, new BigDecimal("80.0000").compareTo(report.getSavingsRate()));
    }
}