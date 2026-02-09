package it.unibg.jarfin.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.unibg.jarfin.web.dto.CommandType;
import it.unibg.jarfin.web.dto.ParsedTransaction;
import it.unibg.jarfin.web.service.NaturalLanguageService;

@WebMvcTest(CommandController.class)
class CommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NaturalLanguageService nluService;

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private Map<String, String> payload;
    private ParsedTransaction parsedTx;

    @BeforeEach
    void setUp() {
        payload = new HashMap<>();
        payload.put("text", "Comando di prova");

        parsedTx = new ParsedTransaction();
        parsedTx.setDescription("Test");
        parsedTx.setAmount(new BigDecimal("10.00"));
        parsedTx.setDate(LocalDate.now());
    }

    @Test
    @DisplayName("CREATE: Dovrebbe inviare una POST al Gateway")
    void process_Create_Success() throws Exception {
        parsedTx.setCommandType(CommandType.CREATE);
        when(nluService.parse(anyString())).thenReturn(parsedTx);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(post("/api/voice/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Salvato"));
    }

    @Test
    @DisplayName("UPDATE: Dovrebbe fare GET (check) e poi PUT")
    void process_Update_Success() throws Exception {
        // 1. Mock NLU
        parsedTx.setCommandType(CommandType.UPDATE);
        parsedTx.setTargetId(1L);
        when(nluService.parse(anyString())).thenReturn(parsedTx);

        ParsedTransaction existing = new ParsedTransaction();
        existing.setId(1L);
        existing.setAmount(new BigDecimal("5.00"));
        
        when(restTemplate.getForObject(anyString(), eq(ParsedTransaction.class)))
                .thenReturn(existing);


        mockMvc.perform(post("/api/voice/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Aggiornato ID 1"));

        verify(restTemplate).put(anyString(), any(HttpEntity.class));
    }

    @Test
    @DisplayName("UPDATE: Fallisce se ID non esiste")
    void process_Update_NotFound() throws Exception {
        parsedTx.setCommandType(CommandType.UPDATE);
        parsedTx.setTargetId(99L);
        when(nluService.parse(anyString())).thenReturn(parsedTx);

        when(restTemplate.getForObject(anyString(), eq(ParsedTransaction.class)))
                .thenReturn(null);

        mockMvc.perform(post("/api/voice/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("ID non trovato."));
    }

    @Test
    @DisplayName("DELETE: Elimina per ID diretto")
    void process_Delete_ById_Success() throws Exception {
        parsedTx.setCommandType(CommandType.DELETE);
        parsedTx.setTargetId(5L);
        when(nluService.parse(anyString())).thenReturn(parsedTx);

        mockMvc.perform(post("/api/voice/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Eliminato ID 5"));

        verify(restTemplate).delete(anyString());
    }

    @Test
    @DisplayName("DELETE: Elimina cercando per Descrizione (Logica complessa)")
    void process_Delete_ByDescription_Success() throws Exception {
        parsedTx.setCommandType(CommandType.DELETE);
        parsedTx.setTargetId(null);
        parsedTx.setDescription("pizza");
        when(nluService.parse(anyString())).thenReturn(parsedTx);

        ParsedTransaction t1 = new ParsedTransaction();
        t1.setId(10L);
        t1.setDescription("Pizza margherita");
        t1.setDate(LocalDate.now().minusDays(1));

        ParsedTransaction t2 = new ParsedTransaction();
        t2.setId(20L);
        t2.setDescription("Pizza diavola");
        t2.setDate(LocalDate.now());

        List<ParsedTransaction> mockList = new ArrayList<>();
        mockList.add(t1);
        mockList.add(t2);

        when(restTemplate.exchange(
                anyString(), 
                eq(HttpMethod.GET), 
                eq(null), 
                any(ParameterizedTypeReference.class)))
            .thenReturn(ResponseEntity.ok(mockList));

        mockMvc.perform(post("/api/voice/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Eliminato ID 20"));
        
        verify(restTemplate).delete(anyString()); 
    }

    @Test
    @DisplayName("IGNORED: Se il parser restituisce null o comando incompleto")
    void process_Ignored() throws Exception {
        when(nluService.parse(anyString())).thenReturn(null);

        mockMvc.perform(post("/api/voice/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ignored"));
    }
}