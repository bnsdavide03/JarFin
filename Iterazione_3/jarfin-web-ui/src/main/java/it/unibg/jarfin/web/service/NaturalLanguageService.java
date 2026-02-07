package it.unibg.jarfin.web.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import it.unibg.jarfin.web.dto.ParsedTransaction;
import it.unibg.jarfin.web.dto.CommandType;

@Service
public class NaturalLanguageService {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("(\\d+([.,]\\d{1,2})?)");
    private static final Pattern DELETE_CMD = Pattern.compile("(elimina|cancella|rimuovi|togli)", Pattern.CASE_INSENSITIVE);
    private static final Pattern UPDATE_CMD = Pattern.compile("(modifica|cambia|aggiorna|correggi)", Pattern.CASE_INSENSITIVE);
    private static final Pattern ID_PATTERN = Pattern.compile("(?:id|numero|codice|transazione)\\s+(\\d+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern EXPLICIT_DESC_PATTERN = Pattern.compile("descrizione\\s+(.*)", Pattern.CASE_INSENSITIVE);

    private static final Map<String, String> CATEGORY_MAP = new HashMap<>();

    static {
        CATEGORY_MAP.put("mcdonald", "Ristorante");
        CATEGORY_MAP.put("burger", "Ristorante");
        CATEGORY_MAP.put("pizza", "Ristorante");
        CATEGORY_MAP.put("sushi", "Ristorante");
        CATEGORY_MAP.put("ristorante", "Ristorante");
        CATEGORY_MAP.put("trattoria", "Ristorante");
        CATEGORY_MAP.put("bar", "Bar/Colazione");
        CATEGORY_MAP.put("colazione", "Bar/Colazione");
        CATEGORY_MAP.put("aperitivo", "Bar/Colazione");
        CATEGORY_MAP.put("caffè", "Bar/Colazione");
        
        CATEGORY_MAP.put("esselunga", "Supermercato");
        CATEGORY_MAP.put("coop", "Supermercato");
        CATEGORY_MAP.put("lidl", "Supermercato");
        CATEGORY_MAP.put("conad", "Supermercato");
        CATEGORY_MAP.put("carrefour", "Supermercato");
        CATEGORY_MAP.put("eurospin", "Supermercato");
        CATEGORY_MAP.put("spesa", "Supermercato");
        CATEGORY_MAP.put("supermercato", "Supermercato");
        CATEGORY_MAP.put("alimentari", "Supermercato");
        CATEGORY_MAP.put("ortofrutta", "Supermercato");

        CATEGORY_MAP.put("benzina", "Trasporti");
        CATEGORY_MAP.put("diesel", "Trasporti");
        CATEGORY_MAP.put("treno", "Trasporti");
        CATEGORY_MAP.put("trenitalia", "Trasporti");
        CATEGORY_MAP.put("italo", "Trasporti");
        CATEGORY_MAP.put("bus", "Trasporti");
        CATEGORY_MAP.put("autostrada", "Trasporti");
        CATEGORY_MAP.put("pedaggio", "Trasporti");
        CATEGORY_MAP.put("parcheggio", "Trasporti");
        CATEGORY_MAP.put("uber", "Trasporti");
        CATEGORY_MAP.put("taxi", "Trasporti");
        CATEGORY_MAP.put("aereo", "Trasporti");
        CATEGORY_MAP.put("ryanair", "Trasporti");

        CATEGORY_MAP.put("luce", "Bollette");
        CATEGORY_MAP.put("gas", "Bollette");
        CATEGORY_MAP.put("enel", "Bollette");
        CATEGORY_MAP.put("a2a", "Bollette");
        CATEGORY_MAP.put("internet", "Bollette");
        CATEGORY_MAP.put("wifi", "Bollette");
        CATEGORY_MAP.put("vodafone", "Bollette");
        CATEGORY_MAP.put("tim", "Bollette");
        CATEGORY_MAP.put("affitto", "Casa");
        CATEGORY_MAP.put("mutuo", "Casa");
        CATEGORY_MAP.put("ikea", "Casa");
        CATEGORY_MAP.put("leroy", "Casa");
        
        CATEGORY_MAP.put("netflix", "Svago");
        CATEGORY_MAP.put("spotify", "Svago");
        CATEGORY_MAP.put("cinema", "Svago");
        CATEGORY_MAP.put("amazon", "Shopping");
        CATEGORY_MAP.put("zalando", "Shopping");
        CATEGORY_MAP.put("vinted", "Shopping");
        CATEGORY_MAP.put("shein", "Shopping");
        CATEGORY_MAP.put("zara", "Shopping");
        CATEGORY_MAP.put("h&m", "Shopping");
        CATEGORY_MAP.put("palestra", "Salute/Sport");
        CATEGORY_MAP.put("padel", "Salute/Sport");
        
        CATEGORY_MAP.put("farmacia", "Salute");
        CATEGORY_MAP.put("medico", "Salute");
        CATEGORY_MAP.put("dentista", "Salute");
        CATEGORY_MAP.put("visita", "Salute");
        
        CATEGORY_MAP.put("stipendio", "Stipendio");
        CATEGORY_MAP.put("bonifico", "Bonifico");
        CATEGORY_MAP.put("rimborso", "Entrata");
        CATEGORY_MAP.put("regalo", "Entrata");
        CATEGORY_MAP.put("vendita", "Entrata");
    }

    public ParsedTransaction parse(String text) {
        if (text == null || text.trim().length() < 2) return null;
        
        String[] stopWords = {"stop", "nulla", "niente", "annulla", "chiudi", "jar", "giar"};
        for (String w : stopWords) if (text.toLowerCase().equals(w)) return null;

        ParsedTransaction result = new ParsedTransaction();
        String lowerCaseText = text.toLowerCase();

        if (DELETE_CMD.matcher(text).find()) {
            result.setCommandType(CommandType.DELETE);
            result.setTargetId(extractId(text)); 
        } 
        else if (UPDATE_CMD.matcher(text).find()) {
            result.setCommandType(CommandType.UPDATE);
            result.setTargetId(extractId(text));
        } 
        else {
            result.setCommandType(CommandType.CREATE);
        }

        if (result.getCommandType() == CommandType.CREATE) result.setDate(LocalDate.now());
        else result.setDate(null);

        String textForAmount = text;
        Matcher idMatcher = ID_PATTERN.matcher(text);
        if (idMatcher.find()) {
            textForAmount = text.replace(idMatcher.group(0), ""); 
        }
        
        Matcher matcher = NUMBER_PATTERN.matcher(textForAmount);
        if (matcher.find()) {
            String numStr = matcher.group(1).replace(",", ".");
            result.setAmount(new BigDecimal(numStr));
        } else {
            result.setAmount(null); 
        }

        if (containsAny(lowerCaseText, "ricevuto", "stipendio", "entrata", "guadagnato", "bonifico", "accreditato", "accredito", "incassato")) {
            result.setType("INCOME");
        } 
        else if (containsAny(lowerCaseText, "speso", "pagato", "uscita", "perso", "costo")) {
            result.setType("EXPENSE");
        }

        String foundCategory = null;
        int maxMatchLength = 0;

        for (Map.Entry<String, String> entry : CATEGORY_MAP.entrySet()) {
            if (lowerCaseText.contains(entry.getKey())) {
                if (entry.getKey().length() > maxMatchLength) {
                    foundCategory = entry.getValue();
                    maxMatchLength = entry.getKey().length();
                }
            }
        }
        result.setCategory(foundCategory);

        if (result.getCommandType() == CommandType.UPDATE) {
            Matcher descMatcher = EXPLICIT_DESC_PATTERN.matcher(text);
            if (descMatcher.find()) {
                result.setDescription(StringUtils.capitalize(descMatcher.group(1).trim()));
            } else {
                result.setDescription(null);
            }
        } else {
            String cleanedDesc = cleanText(lowerCaseText);
            
            if (cleanedDesc.isEmpty() && foundCategory != null) {
                result.setDescription(foundCategory); 
            } else if (!cleanedDesc.isEmpty()) {
                result.setDescription(StringUtils.capitalize(cleanedDesc));
            } else {
                result.setDescription("Generale");
            }
        }

        return result;
    }

    private Long extractId(String text) {
        Matcher m = ID_PATTERN.matcher(text);
        if (m.find()) return Long.parseLong(m.group(1));
        return null;
    }

    private String cleanText(String text) {
        String cleaned = text.replaceAll("(\\d+([.,]\\d{1,2})?)", "");
        cleaned = cleaned.replaceAll("[€$]|euro", "");
        
        cleaned = cleaned.replaceAll("(?:id|numero|codice|transazione)\\s+\\d+", "");
        
        cleaned = cleaned.replaceAll("(modifica|cambia|aggiorna|elimina|cancella|rimuovi)", "");

        String regex = "\\b(" +
            "johnny|jonny|gionni|gianni|joni|jarfin|" + 
            "aggiungi|inserisci|crea|nuova|nuovo|registra|segna|metti|aggiung|" + 
            "ho|hai|ha|abbiamo|avete|hanno|" + 
            "speso|pagato|comprato|preso|ricevuto|accreditato|accredito|uscito|entrato|" +
            "spesa|spes|costo|uscita|entrata|importo|prezzo|valore|soldi|denaro|credito|debito|" + 
            "per|di|a|in|il|lo|la|i|gli|le|un|una|uno|delle|dei|del|al|allo|alla|agli|alle|da|con|su|m|" + 
            "mi|ti|ci|vi|si|me|te|ce|ve|se|mio|tuo|suo|nostro|vostro|loro|" + 
            "rega|ragazzi|raga" + 
            ")\\b";

        cleaned = cleaned.replaceAll(regex, " ");
        
        return cleaned.trim().replaceAll("\\s+", " ");
    }

    private boolean containsAny(String text, String... keywords) {
        for (String k : keywords) if (text.contains(k)) return true;
        return false;
    }
}