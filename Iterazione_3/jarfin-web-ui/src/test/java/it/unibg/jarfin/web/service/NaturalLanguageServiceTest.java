package it.unibg.jarfin.web.service;

import it.unibg.jarfin.web.dto.CommandType;
import it.unibg.jarfin.web.dto.ParsedTransaction;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class NaturalLanguageServiceTest {

    private final NaturalLanguageService service = new NaturalLanguageService();

    @Test
    void testMillionsAndThousands() {
        ParsedTransaction t = service.parse("spesa di 186 milioni");
        assertAmount("186000000", t);
    }

    @Test
    void testComplexCompoundNumber() {
        ParsedTransaction t = service.parse("ho pagato duecentomila e cinquecento euro");
        assertAmount("200500", t);
    }

    @Test
    void testMixedDigitsAndWords() {
        ParsedTransaction t = service.parse("ho speso 10 mila euro");
        assertAmount("10000", t);
    }

    @Test
    void testCentsInWords() {
        ParsedTransaction t = service.parse("venti euro e cinquanta centesimi");
        assertAmount("20.50", t);
    }

    @Test
    void testMixedDigitsAndCents() {
        ParsedTransaction t = service.parse("pagato 47 e 83 centesimi");
        assertAmount("47.83", t);
    }

    @Test
    void testOnlyCents() {
        ParsedTransaction t = service.parse("solo ottanta centesimi");
        assertAmount("0.80", t);
    }

    @Test
    void testSmartDescriptionSpecific() {
        ParsedTransaction t = service.parse("pranzo al mcdonald");
        assertEquals("Ristorante", t.getCategory());
        assertEquals("Mcdonald", t.getDescription());
    }

    @Test
    void testSmartDescriptionGeneric() {
        ParsedTransaction t = service.parse("ho pagato al ristorante");
        assertEquals("Ristorante", t.getCategory());
        assertEquals("Pranzo/Cena", t.getDescription());
    }

    @Test
    void testUtilityBills() {
        ParsedTransaction t = service.parse("pagata bolletta enel");
        assertEquals("Bollette", t.getCategory());
        assertEquals("Enel", t.getDescription());
    }

    @Test
    void testDeleteCommand() {
        ParsedTransaction t = service.parse("elimina transazione 55");
        assertEquals(CommandType.DELETE, t.getCommandType());
        assertEquals(55L, t.getTargetId());
    }

    @Test
    void testUpdateCommand() {
        ParsedTransaction t = service.parse("modifica id 102");
        assertEquals(CommandType.UPDATE, t.getCommandType());
        assertEquals(102L, t.getTargetId());
    }

    @Test
    void testIncomeRecognition() {
        ParsedTransaction t = service.parse("ricevuto stipendio");
        assertEquals("INCOME", t.getType());
        assertEquals("Stipendio", t.getCategory());
    }

    @Test
    void testArticleAmbiguity() {
        ParsedTransaction t = service.parse("aggiungi una spesa di 50 euro");
        assertAmount("50", t);
    }

    @Test
    void testNullOrEmpty() {
        assertNull(service.parse(null));
        assertNull(service.parse(""));
        assertNull(service.parse("a"));
    }

    @Test
    void testStopWords() {
        assertNull(service.parse("stop"));
        assertNull(service.parse("jarfin"));
        assertNull(service.parse("niente"));
    }

    @Test
    void testNumericFormats() {
        assertAmount("10.50", service.parse("speso 10.50"));
        assertAmount("10.50", service.parse("speso 10,50"));
        assertAmount("1000", service.parse("speso 1.000"));
    }

    private void assertAmount(String expected, ParsedTransaction t) {
        assertNotNull(t, "La transazione non dovrebbe essere null");
        assertNotNull(t.getAmount(), "L'importo non dovrebbe essere null");
        assertEquals(0, new BigDecimal(expected).compareTo(t.getAmount()),
                "Importo errato: atteso " + expected + ", ottenuto " + t.getAmount());
    }
}