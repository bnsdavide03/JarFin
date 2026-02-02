package it.unibg.jarfin.web.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class ParsedTransaction {
    private BigDecimal amount;
    private String type;
    private String category;
    private String description;
}