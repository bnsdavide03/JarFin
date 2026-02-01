package it.unibg.jarfin.accounting_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class TransactionRequest {
    
    @NotNull(message = "L'importo è obbligatorio")
    @DecimalMin(value = "0.01", message = "L'importo deve essere positivo") // Risolve il problema del Double/Negative
    private BigDecimal amount;

    @NotNull(message = "La data è obbligatoria")
    private LocalDate date;

    private String description;

    @NotBlank(message = "La categoria non può essere vuota")
    private String category;
}