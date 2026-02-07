package it.unibg.jarfin.web.controller;

import it.unibg.jarfin.web.dto.ParsedTransaction;
import it.unibg.jarfin.web.dto.CommandType;
import it.unibg.jarfin.web.service.NaturalLanguageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
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
        log.info("Comando vocale: {}", text);

        try {
            ParsedTransaction parsed = nluService.parse(text);
            
            if (parsed == null || (parsed.getAmount() == null && parsed.getCommandType() == CommandType.CREATE)) {
                 return ResponseEntity.ok(Map.of("status", "ignored", "message", "Ignorato"));
            }

            String baseUrl = gatewayUrl + "/api/transactions";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            switch (parsed.getCommandType()) {
                case CREATE:
                    if (parsed.getType() == null) parsed.setType("EXPENSE");
                    if (parsed.getCategory() == null) parsed.setCategory("Altro");
                    if (parsed.getDescription() == null || parsed.getDescription().isEmpty()) parsed.setDescription("Transazione Vocale");
                    if (parsed.getAmount() == null) parsed.setAmount(BigDecimal.ZERO);

                    HttpEntity<ParsedTransaction> request = new HttpEntity<>(parsed, headers);
                    restTemplate.postForEntity(baseUrl, request, Object.class);
                    
                    return ResponseEntity.ok(Map.of("message", "Salvato", "amount", parsed.getAmount(), "category", parsed.getCategory()));

                case UPDATE:
                    if (parsed.getTargetId() == null) return ResponseEntity.badRequest().body("Specifica l'ID");
                    
                    String resourceUrl = baseUrl + "/" + parsed.getTargetId();
                    ParsedTransaction existing;
                    try {
                        existing = restTemplate.getForObject(resourceUrl, ParsedTransaction.class);
                    } catch (Exception e) {
                        return ResponseEntity.badRequest().body("ID non trovato.");
                    }
                    if (existing == null) return ResponseEntity.badRequest().body("ID non trovato.");

                    if (parsed.getAmount() != null) existing.setAmount(parsed.getAmount());
                    if (parsed.getDescription() != null && !parsed.getDescription().isEmpty()) existing.setDescription(parsed.getDescription());
                    if (parsed.getCategory() != null) existing.setCategory(parsed.getCategory());
                    if (parsed.getType() != null) existing.setType(parsed.getType());
                    existing.setId(parsed.getTargetId());

                    HttpEntity<ParsedTransaction> requestUp = new HttpEntity<>(existing, headers);
                    restTemplate.put(resourceUrl, requestUp);
                    
                    return ResponseEntity.ok(Map.of("message", "Aggiornato ID " + parsed.getTargetId(), "amount", existing.getAmount(), "category", "Modifica salvata"));

                case DELETE:
                    Long idToDelete = parsed.getTargetId();
                    if (idToDelete == null && parsed.getDescription() != null) {
                        idToDelete = findIdByDescription(parsed.getDescription());
                    }
                    if (idToDelete == null) return ResponseEntity.badRequest().body("Cosa elimino?");

                    restTemplate.delete(baseUrl + "/" + idToDelete);
                    return ResponseEntity.ok(Map.of("message", "Eliminato ID " + idToDelete));

                default:
                    return ResponseEntity.badRequest().body("Comando sconosciuto");
            }

        } catch (Exception e) {
            log.error("Errore: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Errore: " + e.getMessage());
        }
    }

    private Long findIdByDescription(String keyword) {
        try {
            ResponseEntity<List<ParsedTransaction>> response = restTemplate.exchange(
                gatewayUrl + "/api/transactions",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ParsedTransaction>>() {}
            );
            List<ParsedTransaction> list = response.getBody();
            if (list == null) return null;
            return list.stream()
                .filter(t -> t.getDescription() != null && t.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                .sorted((t1, t2) -> t2.getDate().compareTo(t1.getDate()))
                .map(ParsedTransaction::getId)
                .findFirst().orElse(null);
        } catch (Exception e) { return null; }
    }
}