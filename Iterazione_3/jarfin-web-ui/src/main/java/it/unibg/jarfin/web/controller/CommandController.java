package it.unibg.jarfin.web.controller;

import it.unibg.jarfin.web.dto.ParsedTransaction;
import it.unibg.jarfin.web.service.NaturalLanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/voice")
@RequiredArgsConstructor
public class CommandController {

    private final NaturalLanguageService nluService;
    private final RestTemplate restTemplate;

    private final String ACCOUNTING_API = "http://localhost:8080/api/transactions";

    @PostMapping("/process")
    public ResponseEntity<?> processVoiceCommand(@RequestBody Map<String, String> payload) {
        String text = payload.get("text");
        System.out.println("Processing: " + text);

        ParsedTransaction parsed = nluService.parse(text);

        Map<String, Object> transactionRequest = new HashMap<>();
        transactionRequest.put("amount", parsed.getAmount());
        transactionRequest.put("type", parsed.getType());
        transactionRequest.put("category", parsed.getCategory());
        transactionRequest.put("date", LocalDate.now());
        
        try {
            restTemplate.postForEntity(ACCOUNTING_API, transactionRequest, Object.class);
            return ResponseEntity.ok(parsed);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Errore nel salvataggio: " + e.getMessage());
        }
    }
}