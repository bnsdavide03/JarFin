package it.unibg.jarfin.analytics_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import it.unibg.jarfin.analytics_service.dto.FinancialReportDTO;
import it.unibg.jarfin.analytics_service.service.AnalyticsService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService service;

    @GetMapping("/report")
    public FinancialReportDTO getReport() {
        return service.generateReport();
    }
}