package it.unibg.jarfin.web.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.LocalDate;
import lombok.Data;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import it.unibg.jarfin.web.dto.ParsedTransaction;

@Service
public class NaturalLanguageService {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("(\\d+([.,]\\d{1,2})?)");
    
    private static final Map<String, String> CATEGORY_MAP = new HashMap<>();

    static {
        // TRASPORTI
        String catTrans = "Trasporti";
        CATEGORY_MAP.put("benzina", catTrans); CATEGORY_MAP.put("diesel", catTrans);
        CATEGORY_MAP.put("gasolio", catTrans); CATEGORY_MAP.put("autostrada", catTrans);
        CATEGORY_MAP.put("treno", catTrans); CATEGORY_MAP.put("biglietto", catTrans);
        CATEGORY_MAP.put("parcheggio", catTrans); CATEGORY_MAP.put("taxi", catTrans);
    
        // CIBO & RISTORAZIONE
        CATEGORY_MAP.put("pizza", "Ristorante");
        CATEGORY_MAP.put("sushi", "Ristorante");
        CATEGORY_MAP.put("ristorante", "Ristorante");
        CATEGORY_MAP.put("cena", "Ristorante");
        CATEGORY_MAP.put("pranzo", "Ristorante");
        CATEGORY_MAP.put("aperitivo", "Bar/Colazione");
        CATEGORY_MAP.put("bar", "Bar/Colazione");
        CATEGORY_MAP.put("caffè", "Bar/Colazione");
        CATEGORY_MAP.put("colazione", "Bar/Colazione");
    
        // SPESA
        String catSpesa = "Supermercato";
        CATEGORY_MAP.put("spesa", catSpesa); CATEGORY_MAP.put("esselunga", catSpesa);
        CATEGORY_MAP.put("coop", catSpesa); CATEGORY_MAP.put("conad", catSpesa);
        CATEGORY_MAP.put("lidl", catSpesa); CATEGORY_MAP.put("alimentari", catSpesa);
    
        // SVAGO & CULTURA
        CATEGORY_MAP.put("cinema", "Svago");
        CATEGORY_MAP.put("bowling", "Svago");
        CATEGORY_MAP.put("calcetto", "Svago");
        CATEGORY_MAP.put("palestra", "Salute/Sport");
        CATEGORY_MAP.put("farmacia", "Salute/Sport");
        CATEGORY_MAP.put("medicine", "Salute/Sport");
        CATEGORY_MAP.put("libro", "Cultura");
        
        // CASA & BOLLETTE
        CATEGORY_MAP.put("bolletta", "Bollette");
        CATEGORY_MAP.put("luce", "Bollette");
        CATEGORY_MAP.put("gas", "Bollette");
        CATEGORY_MAP.put("affitto", "Casa");
        CATEGORY_MAP.put("amazon", "Shopping");
        CATEGORY_MAP.put("vestiti", "Shopping");

        CATEGORY_MAP.put("scarpe", "Shopping");
        CATEGORY_MAP.put("bowling", "Svago");
        CATEGORY_MAP.put("slot", "Svago");
        CATEGORY_MAP.put("spesa", "Supermercato");
        CATEGORY_MAP.put("amazon", "Shopping");
        CATEGORY_MAP.put("vestiti", "Shopping");
        CATEGORY_MAP.put("maglia", "Shopping");
    }

    public ParsedTransaction parse(String text) {
        if (text == null || text.trim().length() < 2) {
            return null;
        }

        String lowerCaseText = text.toLowerCase().trim();

        if (isIgnorable(lowerCaseText)) {
            return null;
        }

        ParsedTransaction result = new ParsedTransaction();
        result.setDescription(text);
        result.setDate(LocalDate.now());

        Matcher matcher = NUMBER_PATTERN.matcher(text);
        if (matcher.find()) {
            String numStr = matcher.group(1).replace(",", ".");
            result.setAmount(new BigDecimal(numStr));
        } else {
            // Se non c'è un numero, è un comando invalido
            return null; 
        }

        if (containsAny(lowerCaseText, "ricevuto", "stipendio", "entrata", "guadagnato", "bonifico")) {
            result.setType("INCOME");
        } else {
            result.setType("EXPENSE");
        }

        String finalCategory = "Generale";

        for (Map.Entry<String, String> entry : CATEGORY_MAP.entrySet()) {
            if (lowerCaseText.contains(entry.getKey())) {
                finalCategory = entry.getValue();
                break;
            }
        }

        if (finalCategory.equals("Generale")) {
            finalCategory = cleanText(lowerCaseText);
        }

        result.setCategory(StringUtils.capitalize(finalCategory != null && !finalCategory.isEmpty() ? finalCategory : "Generale"));

        return result;
    }

    private boolean isIgnorable(String text) {
        String[] stopWords = {"stop", "nulla", "niente", "annulla", "chiudi", "jar", "giar", "darwin", "jarwin"};
        for (String word : stopWords) {
            if (text.equals(word)) return true;
        }
        return false;
    }

    private String cleanText(String text) {
        // Rimuove numeri e simboli valuta
        String cleaned = text.toLowerCase().replaceAll("(\\d+([.,]\\d{1,2})?)", "");
        cleaned = cleaned.replace("€", "").replace("$", "").replace("euro", "");
        
        // Lista estesa di parole da eliminare assolutamente
        String[] stopWords = {
            "johnny", "jonny", "jarfin", "allora", "senti", "quindi", "vabbè", 
            "ho ", "speso", "pagato", "comprato", "preso", "ricevuto", 
            "per ", "di ", "a ", "in ", "il ", "lo ", "la ", "i ", "gli ", "le ", "un ", "una ",
            "al ", "del ", "ai ", "degli ", "alle ", "sulla ", "sull' ", "aggiungi ", "categoria "
        };
    
        for (String word : stopWords) {
            // Usiamo il regex per eliminare solo parole intere e non pezzi di parole
            cleaned = cleaned.replaceAll("\\b" + word.trim() + "\\b", "");
        }
        
        return cleaned.trim().replaceAll("\\s+", " ");
    }

    private boolean containsAny(String text, String... keywords) {
        for (String k : keywords) {
            if (text.contains(k)) return true;
        }
        return false;
    }
}