package it.unibg.jarfin.analytics_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.unibg.jarfin.analytics_service.dto.FinancialReportDTO;
import it.unibg.jarfin.analytics_service.service.AnalyticsService;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService service;

    @GetMapping("/report")
    public FinancialReportDTO getReport() {
        return service.generateReport();
    }
}