package it.unibg.jarfin.analytics_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class TransactionDTO {
    // Mappiamo solo i campi che ci servono per le statistiche
    private BigDecimal amount;
    private String category;
    private LocalDate date;
}