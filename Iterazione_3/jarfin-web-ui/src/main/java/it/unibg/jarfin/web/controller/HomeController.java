package it.unibg.jarfin.web.controller;

import it.unibg.jarfin.web.dto.FinancialReport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final RestTemplate restTemplate;
    private final String ANALYTICS_API = "http://localhost:8080/api/analytics/report";

    @GetMapping("/")
    public String home(Model model) {
        try {
            FinancialReport report = restTemplate.getForObject(ANALYTICS_API, FinancialReport.class);
            
            model.addAttribute("report", report);
            
        } catch (Exception e) {
            System.err.println("Impossibile recuperare Analytics: " + e.getMessage());
            model.addAttribute("report", new FinancialReport());
        }
        
        return "index";
    }
}