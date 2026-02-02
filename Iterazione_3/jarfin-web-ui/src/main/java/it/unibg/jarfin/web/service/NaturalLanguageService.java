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
    	
        CATEGORY_MAP.put("benzina", "Trasporti");
        CATEGORY_MAP.put("diesel", "Trasporti");
        CATEGORY_MAP.put("gasolio", "Trasporti");
        CATEGORY_MAP.put("treno", "Trasporti");
        CATEGORY_MAP.put("bus", "Trasporti");
        CATEGORY_MAP.put("uber", "Trasporti");
        CATEGORY_MAP.put("taxi", "Trasporti");

        CATEGORY_MAP.put("pizza", "Ristorante");
        CATEGORY_MAP.put("sushi", "Ristorante");
        CATEGORY_MAP.put("mcdonald", "Ristorante");
        CATEGORY_MAP.put("burger", "Ristorante");
        CATEGORY_MAP.put("ristorante", "Ristorante");
        CATEGORY_MAP.put("bar", "Bar/Colazione");
        CATEGORY_MAP.put("caffè", "Bar/Colazione");
        CATEGORY_MAP.put("spesa", "Supermercato");
        CATEGORY_MAP.put("supermercato", "Supermercato");
        CATEGORY_MAP.put("coop", "Supermercato");
        CATEGORY_MAP.put("esselunga", "Supermercato");

        CATEGORY_MAP.put("luce", "Bollette");
        CATEGORY_MAP.put("gas", "Bollette");
        CATEGORY_MAP.put("internet", "Bollette");
        CATEGORY_MAP.put("wifi", "Bollette");
        CATEGORY_MAP.put("affitto", "Casa");
        CATEGORY_MAP.put("mutuo", "Casa");

        CATEGORY_MAP.put("netflix", "Svago");
        CATEGORY_MAP.put("spotify", "Svago");
        CATEGORY_MAP.put("cinema", "Svago");
        CATEGORY_MAP.put("libro", "Cultura");
        
        CATEGORY_MAP.put("stipendio", "Stipendio");
        CATEGORY_MAP.put("bonifico", "Bonifico");
    }

    public ParsedTransaction parse(String text) {
        ParsedTransaction result = new ParsedTransaction();
        result.setDescription(text);

        result.setDate(java.time.LocalDate.now());
        
        String lowerCaseText = text.toLowerCase();

        Matcher matcher = NUMBER_PATTERN.matcher(text);
        if (matcher.find()) {
            String numStr = matcher.group(1).replace(",", ".");
            result.setAmount(new BigDecimal(numStr));
        } else {
            result.setAmount(BigDecimal.ZERO);
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

        if (finalCategory != null && !finalCategory.isEmpty()) {
            result.setCategory(StringUtils.capitalize(finalCategory));
        } else {
        	result.setCategory("Generale");
        }

        return result;
    }

    private String cleanText(String text) {
        String cleaned = text.replaceAll("(\\d+([.,]\\d{1,2})?)", "");
        cleaned = cleaned.replace("€", "").replace("$", "");
        
        String[] stopWords = {
            "allora", "senti", "guarda", "quindi", "poi", "vabbè", 
            "ho ", "speso", "pagato", "comprato", "preso", "ricevuto", 
            "per ", "di ", "a ", "in ", "il ", "lo ", "la ", "i ", "gli ", "le ", "un ", "una "
        };

        for (String word : stopWords) {
            cleaned = cleaned.replace(word, " ");
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