package it.unibg.jarfin.web.controller;

import it.unibg.jarfin.web.dto.FinancialReport;
import it.unibg.jarfin.web.dto.ParsedTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final RestTemplate restTemplate;

    @Value("${api.gateway.url:http://localhost:8080}")
    private String gatewayUrl;

    @GetMapping("/")
    public String home(Model model) {
        // CORREZIONE: Carichiamo sia il report che la lista delle transazioni
        loadFinancialReport(model);
        loadTransactionsList(model); 
        return "index";
    }

    @GetMapping("/transactions")
    public String allTransactions(Model model) {
        loadTransactionsList(model);
        return "transactions";
    }

    @GetMapping("/delete/{id}")
    public String deleteTransaction(@PathVariable Long id) {
        try {
            restTemplate.delete(gatewayUrl + "/api/transactions/" + id);
        } catch (RestClientException e) {
            log.error("Errore delete: {}", e.getMessage());
        }
        return "redirect:/transactions";
    }

    @PostMapping("/update")
    public String updateTransaction(@ModelAttribute ParsedTransaction transaction) {
        try {
            restTemplate.put(gatewayUrl + "/api/transactions/" + transaction.getId(), transaction);
        } catch (RestClientException e) {
            log.error("Errore update: {}", e.getMessage());
        }
        return "redirect:/transactions";
    }

    private void loadFinancialReport(Model model) {
        try {
            FinancialReport report = restTemplate.getForObject(gatewayUrl + "/api/analytics/report", FinancialReport.class);
            model.addAttribute("report", report != null ? report : new FinancialReport());
        } catch (RestClientException e) {
            model.addAttribute("report", new FinancialReport());
            model.addAttribute("error", "Servizio Analytics non disponibile.");
        }
    }

    private void loadTransactionsList(Model model) {
        try {
            ResponseEntity<List<ParsedTransaction>> response = restTemplate.exchange(
                    gatewayUrl + "/api/transactions",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ParsedTransaction>>() {}
            );
            
            List<ParsedTransaction> list = response.getBody() != null ? response.getBody() : new ArrayList<>();

            // Ordiniamo per ID decrescente (più recenti prima)
            list.sort((t1, t2) -> {
                if (t1.getId() == null || t2.getId() == null) return 0;
                return t2.getId().compareTo(t1.getId());
            });

            model.addAttribute("transactions", list);

        } catch (RestClientException e) {
            model.addAttribute("transactions", Collections.emptyList());
        }
    }
}