package it.unibg.jarfin.web.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import it.unibg.jarfin.web.dto.CommandType;
import it.unibg.jarfin.web.dto.ParsedTransaction;

class NaturalLanguageServiceTest {

    private final NaturalLanguageService service = new NaturalLanguageService();

    @Test
    @DisplayName("CREATE: Spesa semplice al ristorante")
    void parse_SimpleExpense() {
        String input = "Ho speso 50 euro per una pizza";
        
        ParsedTransaction result = service.parse(input);

        assertNotNull(result);
        assertEquals(CommandType.CREATE, result.getCommandType());
        assertEquals(new BigDecimal("50"), result.getAmount());
        assertEquals("EXPENSE", result.getType());
        assertEquals("Ristorante", result.getCategory());
        assertTrue(result.getDescription().contains("Pizza")); 
    }

    @Test
    @DisplayName("CREATE: Entrata (Stipendio) con decimali e virgola")
    void parse_IncomeWithDecimals() {
        String input = "Ricevuto bonifico stipendio di 1500,50";
        
        ParsedTransaction result = service.parse(input);

        assertNotNull(result);
        assertEquals(CommandType.CREATE, result.getCommandType());
        assertEquals(new BigDecimal("1500.50"), result.getAmount());
        assertEquals("INCOME", result.getType());
        assertEquals("Stipendio", result.getCategory());
    }

    @Test
    @DisplayName("DELETE: Riconoscimento comando elimina per ID")
    void parse_DeleteCommand() {
        String input = "Elimina transazione id 42";
        
        ParsedTransaction result = service.parse(input);

        assertNotNull(result);
        assertEquals(CommandType.DELETE, result.getCommandType());
        assertEquals(42L, result.getTargetId());
    }

    @Test
    @DisplayName("UPDATE: Modifica importo su ID specifico")
    void parse_UpdateCommand() {
        String input = "Modifica id 10 importo 25 euro";
        
        ParsedTransaction result = service.parse(input);

        assertNotNull(result);
        assertEquals(CommandType.UPDATE, result.getCommandType());
        assertEquals(10L, result.getTargetId());
        assertEquals(new BigDecimal("25"), result.getAmount());
    }
    
    @Test
    @DisplayName("UPDATE: Modifica descrizione esplicita")
    void parse_UpdateDescription() {
        // Il pattern EXPLICIT_DESC_PATTERN cerca "descrizione ..."
        String input = "Modifica id 5 descrizione Cena aziendale";
        
        ParsedTransaction result = service.parse(input);
        
        assertEquals(CommandType.UPDATE, result.getCommandType());
        assertEquals(5L, result.getTargetId());
        assertEquals("Cena aziendale", result.getDescription());
    }

    @Test
    @DisplayName("EDGE CASE: Input nullo o troppo corto")
    void parse_InvalidInput() {
        assertNull(service.parse(null));
        assertNull(service.parse(""));
        assertNull(service.parse("a"));
    }

    @Test
    @DisplayName("EDGE CASE: Stop Words (annulla comando)")
    void parse_StopWords() {
        assertNull(service.parse("annulla tutto"));
        assertNull(service.parse("stop"));
        assertNull(service.parse("niente"));
    }

    @Test
    @DisplayName("LOGIC: Mapping Categorie non banali")
    void parse_CategoryMapping() {
        ParsedTransaction t1 = service.parse("Ho preso un uber da 15 euro");
        assertEquals("Trasporti", t1.getCategory());

        ParsedTransaction t2 = service.parse("Spesa ikea 100");
        assertEquals("Casa", t2.getCategory());
        
        ParsedTransaction t3 = service.parse("Ordine zalando 50");
        assertEquals("Shopping", t3.getCategory());
    }
    
    @ParameterizedTest
    @CsvSource({
        "benzina 50, Trasporti",
        "farmacia 12, Salute",
        "netflix 10, Svago",
        "palestra 40, Salute/Sport"
    })
    @DisplayName("Data-Driven Test: Verifica Mapping Categorie Rapido")
    void parse_ParameterizedCategories(String input, String expectedCategory) {
        ParsedTransaction result = service.parse(input);
        assertEquals(expectedCategory, result.getCategory());
    }
}