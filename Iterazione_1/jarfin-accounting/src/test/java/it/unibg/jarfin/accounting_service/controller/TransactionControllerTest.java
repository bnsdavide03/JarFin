package it.unibg.jarfin.accounting_service.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.unibg.jarfin.accounting_service.dto.TransactionRequest;
import it.unibg.jarfin.accounting_service.dto.TransactionResponse;
import it.unibg.jarfin.accounting_service.mapper.TransactionMapper;
import it.unibg.jarfin.accounting_service.model.Transaction;
import it.unibg.jarfin.accounting_service.model.TransactionType;
import it.unibg.jarfin.accounting_service.service.TransactionService;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService service;

    @MockBean
    private TransactionMapper mapper;

    // Oggetti di test riutilizzabili
    private TransactionRequest requestDto;
    private TransactionResponse responseDto;
    private Transaction entity;

    @BeforeEach
    void setUp() {
        requestDto = new TransactionRequest();
        requestDto.setAmount(new BigDecimal("150.00"));
        requestDto.setCategory("Spesa");
        requestDto.setDescription("Test JUnit");
        requestDto.setDate(LocalDate.now());
        requestDto.setType(TransactionType.EXPENSE);

        entity = new Transaction();
        entity.setAmount(new BigDecimal("150.00"));
        entity.setCategory("Spesa");
        entity.setDescription("Test JUnit");

        responseDto = new TransactionResponse();
        responseDto.setId(1L);
        responseDto.setAmount(new BigDecimal("150.00"));
        responseDto.setCategory("Spesa");
        responseDto.setDescription("Test JUnit");
    }

    @Test
    @DisplayName("POST /api/transactions - Dovrebbe ritornare 201 Created e Location Header")
    void create_ShouldReturnCreatedAndLocationHeader() throws Exception {
        // Mocking
        when(mapper.toEntity(any(TransactionRequest.class))).thenReturn(entity);
        when(service.saveTransaction(any(Transaction.class))).thenReturn(entity);
        when(mapper.toResponse(any(Transaction.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                
                .andExpect(status().isCreated())
                
                .andExpect(header().string("Location", containsString("/api/transactions/1")))
                
                // Verifica Body JSON
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Test JUnit"));
    }

    @Test
    @DisplayName("GET /api/transactions - Dovrebbe ritornare lista di transazioni")
    void findAll_ShouldReturnList() throws Exception {
        List<Transaction> transactions = Collections.singletonList(entity);
        
        when(service.getAllTransactions()).thenReturn(transactions);
        when(mapper.toResponse(any(Transaction.class))).thenReturn(responseDto);

        mockMvc.perform(get("/api/transactions")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].amount").value(150.00));
    }

    @Test
    @DisplayName("GET /api/transactions/{id} - Dovrebbe ritornare singola transazione")
    void getById_ShouldReturnTransaction() throws Exception {
        Long id = 1L;
        when(service.getTransactionById(id)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(responseDto);

        mockMvc.perform(get("/api/transactions/{id}", id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("PUT /api/transactions/{id} - Dovrebbe aggiornare e ritornare 200 OK")
    void update_ShouldReturnUpdatedTransaction() throws Exception {
        Long id = 1L;
        
        when(mapper.toEntity(any(TransactionRequest.class))).thenReturn(entity);
        when(service.updateTransaction(eq(id), any(Transaction.class))).thenReturn(entity);
        when(mapper.toResponse(any(Transaction.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/transactions/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Test JUnit"));
    }

    @Test
    @DisplayName("DELETE /api/transactions/{id} - Dovrebbe ritornare 204 No Content")
    void delete_ShouldReturnNoContent() throws Exception {
        Long id = 1L;
        doNothing().when(service).deleteTransaction(id);

        mockMvc.perform(delete("/api/transactions/{id}", id))
                .andExpect(status().isNoContent());
        
        verify(service).deleteTransaction(id);
    }
    
    @Test
    @DisplayName("POST /api/transactions - Validazione input errato (Amount null)")
    void create_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        requestDto.setAmount(null);

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }
}