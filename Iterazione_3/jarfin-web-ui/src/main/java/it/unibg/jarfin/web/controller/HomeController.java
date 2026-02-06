package it.unibg.jarfin.web.controller;

import it.unibg.jarfin.web.dto.FinancialReport;
import it.unibg.jarfin.web.dto.ParsedTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final RestTemplate restTemplate;

    @Value("${api.gateway.url:http://localhost:8080}")
    private String gatewayUrl;

    @GetMapping("/")
    public String home(Model model) {
        try {
            String analyticsUrl = gatewayUrl + "/api/analytics/report";
            
            FinancialReport report = restTemplate.getForObject(analyticsUrl, FinancialReport.class);
            model.addAttribute("report", report);
            
        } catch (Exception e) {
            log.error("Impossibile recuperare Analytics: {}", e.getMessage());
            
            model.addAttribute("report", new FinancialReport());
            model.addAttribute("error", "Backend non raggiungibile");
        }
        
        return "index";
    }

    @GetMapping("/transactions")
public String allTransactions(Model model) {
    try {
        // Chiamata al microservizio di accounting (porta 8080)
        String transactionsUrl = gatewayUrl + "/api/transactions";
        
        // Riceviamo la lista delle transazioni
        // Nota: Assicurati che il backend restituisca ParsedTransaction[] o una lista di oggetti simili
        ParsedTransaction[] transactions = restTemplate.getForObject(transactionsUrl, ParsedTransaction[].class);
        
        model.addAttribute("transactions", transactions);
    } catch (Exception e) {
        log.error("Errore recupero transazioni: {}", e.getMessage());
        model.addAttribute("transactions", List.of());
    }
    return "transactions"; // Nome del file .html
}
}