package it.unibg.jarfin.web.controller;

import it.unibg.jarfin.web.dto.ParsedTransaction;
import it.unibg.jarfin.web.service.NaturalLanguageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/voice")
@Slf4j
public class CommandController {

    private final NaturalLanguageService nluService;
    private final RestTemplate restTemplate;

    @Value("${api.gateway.url:http://localhost:8080}")
    private String gatewayUrl;

    public CommandController(NaturalLanguageService nluService, RestTemplate restTemplate) {
        this.nluService = nluService;
        this.restTemplate = restTemplate;
    }

    @PostMapping("/process")
    public ResponseEntity<?> processVoiceCommand(@RequestBody Map<String, String> payload) {
        String text = payload.get("text");
        
        log.info("Processing voice command: {}", text); 

        try {
            ParsedTransaction parsed = nluService.parse(text);

            // Se l'NLU restituisce null (es. comando "stop", "nulla" o senza importo)
            // non inviamo nulla al database e rispondiamo con uno status "ignored"
            if (parsed == null || parsed.getAmount() == null) {
                log.info("Voice command ignored (null or no amount detected)");
                return ResponseEntity.ok(Map.of("status", "ignored", "message", "Comando non riconosciuto o annullato"));
            }

            String accountingUrl = gatewayUrl + "/api/transactions";
            
            // RISOLUZIONE ERRORE 415: Specifichiamo esplicitamente che mandiamo un JSON
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<ParsedTransaction> request = new HttpEntity<>(parsed, headers);
            
            restTemplate.postForEntity(accountingUrl, request, Object.class);
            
            log.info("Transaction saved successfully: {} - {}€", parsed.getCategory(), parsed.getAmount());

            return ResponseEntity.ok(parsed);

        } catch (Exception e) {
            log.error("Error processing voice command: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Errore nell'elaborazione: " + e.getMessage());
        }
    }
}