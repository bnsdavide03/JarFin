package it.unibg.jarfin.web.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import it.unibg.jarfin.web.dto.FinancialReport;
import it.unibg.jarfin.web.dto.ParsedTransaction;

@WebMvcTest(HomeController.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestTemplate restTemplate;

    private ParsedTransaction t1;
    private ParsedTransaction t2;
    private FinancialReport report;

    @BeforeEach
    void setUp() {
        t1 = new ParsedTransaction();
        t1.setId(10L);
        t1.setDescription("Vecchia");
        t1.setDate(java.time.LocalDate.now());

        t2 = new ParsedTransaction();
        t2.setId(20L);
        t2.setDescription("Nuova");
        t2.setDate(java.time.LocalDate.now());

        report = new FinancialReport();
        report.setBalance(new BigDecimal("1000.00"));
    }

    @Test
    @DisplayName("HOME: Caricamento corretto e vista index")
    void home_ShouldReturnIndexAndViewReport() throws Exception {
        when(restTemplate.getForObject(anyString(), eq(FinancialReport.class)))
            .thenReturn(report);

        when(restTemplate.exchange(
                anyString(), 
                eq(HttpMethod.GET), 
                eq(null), 
                any(ParameterizedTypeReference.class)))
            .thenReturn(ResponseEntity.ok(new ArrayList<>()));

        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(view().name("index"))
            .andExpect(model().attributeExists("report"))
            .andExpect(model().attribute("report", report))
            .andExpect(model().attributeExists("transactions"));
    }

    @Test
    @DisplayName("HOME: Gestione errore Backend Down")
    void home_ShouldHandleBackendError() throws Exception {

        when(restTemplate.getForObject(anyString(), eq(FinancialReport.class)))
            .thenThrow(new RestClientException("Connection refused"));

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null), any(ParameterizedTypeReference.class)))
            .thenThrow(new RestClientException("Connection refused"));

        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(view().name("index"))
            .andExpect(model().attributeExists("error"))
            .andExpect(model().attributeExists("report"));
    }

    @Test
    @DisplayName("TRANSACTIONS: Lista ordinata per ID decrescente")
    void allTransactions_ShouldSortAndReturnList() throws Exception {
        List<ParsedTransaction> unsortedList = new ArrayList<>();
        unsortedList.add(t1);
        unsortedList.add(t2);

        when(restTemplate.exchange(
                anyString(), 
                eq(HttpMethod.GET), 
                eq(null), 
                any(ParameterizedTypeReference.class)))
            .thenReturn(ResponseEntity.ok(unsortedList));

        MvcResult result = mockMvc.perform(get("/transactions"))
            .andExpect(status().isOk())
            .andExpect(view().name("transactions"))
            .andExpect(model().attributeExists("transactions"))
            .andReturn();
        
        @SuppressWarnings("unchecked")
        List<ParsedTransaction> transactions = (List<ParsedTransaction>) result.getModelAndView().getModel().get("transactions");
        
        assertEquals(2, transactions.size());
        assertEquals(20L, transactions.get(0).getId()); 
        assertEquals(10L, transactions.get(1).getId());
    }

    @Test
    @DisplayName("DELETE: Redirect dopo eliminazione")
    void deleteTransaction_ShouldCallDeleteAndRedirect() throws Exception {
        Long id = 5L;

        mockMvc.perform(get("/delete/{id}", id))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/transactions"));

        verify(restTemplate).delete(anyString());
    }
    
    @Test
    @DisplayName("DELETE: Gestione eccezione backend")
    void deleteTransaction_ShouldHandleException() throws Exception {
        Long id = 5L;
        org.mockito.Mockito.doThrow(new RestClientException("Errore")).when(restTemplate).delete(anyString());

        mockMvc.perform(get("/delete/{id}", id))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/transactions"));
    }

    @Test
    @DisplayName("UPDATE: Post e Redirect")
    void updateTransaction_ShouldCallPutAndRedirect() throws Exception {
        mockMvc.perform(post("/update")
                .flashAttr("parsedTransaction", t1))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/transactions"));

        verify(restTemplate).put(anyString(), any(ParsedTransaction.class));
    }
}