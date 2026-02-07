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
    private static final Map<String, String> CATEGORY_DEFAULT_DESC = new HashMap<>();
    private static final Map<String, String> VERB_TO_NOUN = new HashMap<>();

    static {
        VERB_TO_NOUN.put("stipendiato", "stipendio");
        VERB_TO_NOUN.put("regalato", "regalo");
        VERB_TO_NOUN.put("comprato", "acquisto");
        VERB_TO_NOUN.put("mangiato", "pranzo");
        VERB_TO_NOUN.put("bevuto", "bevanda");
        VERB_TO_NOUN.put("viaggiato", "viaggio");
        VERB_TO_NOUN.put("pagato", "pagamento");
        VERB_TO_NOUN.put("speso", "spesa");
        VERB_TO_NOUN.put("ricevuto", "entrata");
        VERB_TO_NOUN.put("accreditato", "accredito");
        VERB_TO_NOUN.put("bonifico", "bonifico");
        
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
        
        CATEGORY_MAP.put("parrucchiere", "Cura Personale");
        CATEGORY_MAP.put("barbiere", "Cura Personale");
        CATEGORY_MAP.put("estetista", "Cura Personale");
        CATEGORY_MAP.put("trucco", "Cura Personale");
        CATEGORY_MAP.put("makeup", "Cura Personale");
        CATEGORY_MAP.put("manicure", "Cura Personale");
        CATEGORY_MAP.put("pedicure", "Cura Personale");
        CATEGORY_MAP.put("massaggio", "Cura Personale");
        CATEGORY_MAP.put("spa", "Cura Personale");
        CATEGORY_MAP.put("centro estetico", "Cura Personale");
        CATEGORY_MAP.put("salone", "Cura Personale");
        CATEGORY_MAP.put("profumeria", "Cura Personale");
        CATEGORY_MAP.put("cosmetica", "Cura Personale");
        
        CATEGORY_MAP.put("stipendio", "Stipendio");
        CATEGORY_MAP.put("bonifico", "Bonifico");
        CATEGORY_MAP.put("rimborso", "Entrata");
        CATEGORY_MAP.put("regalo", "Entrata");
        CATEGORY_MAP.put("vendita", "Entrata");
        
        CATEGORY_DEFAULT_DESC.put("Ristorante", "Pranzo/Cena");
        CATEGORY_DEFAULT_DESC.put("Bar/Colazione", "Caffè");
        CATEGORY_DEFAULT_DESC.put("Supermercato", "Spesa");
        CATEGORY_DEFAULT_DESC.put("Trasporti", "Trasporto");
        CATEGORY_DEFAULT_DESC.put("Bollette", "Utenze");
        CATEGORY_DEFAULT_DESC.put("Casa", "Casa");
        CATEGORY_DEFAULT_DESC.put("Svago", "Intrattenimento");
        CATEGORY_DEFAULT_DESC.put("Shopping", "Acquisto");
        CATEGORY_DEFAULT_DESC.put("Salute", "Salute");
        CATEGORY_DEFAULT_DESC.put("Salute/Sport", "Sport");
        CATEGORY_DEFAULT_DESC.put("Cura Personale", "Estetica");
        CATEGORY_DEFAULT_DESC.put("Stipendio", "Stipendio");
        CATEGORY_DEFAULT_DESC.put("Bonifico", "Bonifico");
        CATEGORY_DEFAULT_DESC.put("Entrata", "Entrata");
    }

    public ParsedTransaction parse(String text) {
        if (text == null || text.trim().length() < 2) return null;
        
        String[] stopWords = {"stop", "nulla", "niente", "annulla", "chiudi", "jar", "giar"};
        for (String w : stopWords) if (text.toLowerCase().equals(w)) return null;

        text = convertVerbsToNouns(text);

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
        String matchedKeyword = null;
        int maxMatchLength = 0;

        for (Map.Entry<String, String> entry : CATEGORY_MAP.entrySet()) {
            if (lowerCaseText.contains(entry.getKey())) {
                if (entry.getKey().length() > maxMatchLength) {
                    foundCategory = entry.getValue();
                    matchedKeyword = entry.getKey();
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
            String description = extractSmartDescription(lowerCaseText, foundCategory, matchedKeyword);
            result.setDescription(description);
        }

        return result;
    }

    private String convertVerbsToNouns(String text) {
        String converted = text;
        for (Map.Entry<String, String> entry : VERB_TO_NOUN.entrySet()) {
            converted = converted.replaceAll("(?i)\\b" + entry.getKey() + "\\b", entry.getValue());
        }
        return converted;
    }

    private Long extractId(String text) {
        Matcher m = ID_PATTERN.matcher(text);
        if (m.find()) return Long.parseLong(m.group(1));
        return null;
    }

    private String extractSmartDescription(String text, String category, String matchedKeyword) {
        String specificNoun = extractSpecificNoun(text);
        
        if (specificNoun != null && !specificNoun.isEmpty()) {
            return StringUtils.capitalize(specificNoun);
        }
        
        String cleaned = cleanText(text);
        
        String[] words = cleaned.split("\\s+");
        StringBuilder result = new StringBuilder();
        int count = 0;
        
        for (String word : words) {
            if (word.length() > 2 && count < 2) {  // Max 2 parole
                if (result.length() > 0) result.append(" ");
                result.append(word);
                count++;
            }
        }
        
        String finalDesc = result.toString().trim();
        
        if (finalDesc.isEmpty() || finalDesc.length() < 3) {
            if (category != null && CATEGORY_DEFAULT_DESC.containsKey(category)) {
                return CATEGORY_DEFAULT_DESC.get(category);
            }
            return category != null ? category : "Generale";
        }
        
        finalDesc = StringUtils.capitalize(finalDesc);
        if (finalDesc.length() > 30) {
            finalDesc = finalDesc.substring(0, 27) + "...";
        }
        
        return finalDesc;
    }
    
    private String extractSpecificNoun(String text) {
        String lowerText = text.toLowerCase();
        
        String[] specificNouns = {
            "stipendio", "regalo", "bonifico", "pagamento", "acquisto",
            "netflix", "spotify", "amazon", "prime", "disney",
            "cinema", "teatro", "concerto", "museo",
            "pizza", "sushi", "hamburger", "panino", "kebab", "pranzo", "cena", "colazione",
            "mcdonald", "burger king", "kfc",
            "esselunga", "coop", "lidl", "conad", "carrefour", "eurospin",
            "benzina", "diesel", "carburante", "metano", "gpl",
            "treno", "bus", "metro", "taxi", "uber", "aereo", "viaggio",
            "farmacia", "parafarmacia", "ospedale",
            "palestra", "piscina", "padel", "tennis", "calcio",
            "parrucchiere", "barbiere", "estetista", "manicure", "pedicure", 
            "massaggio", "spa", "trucco", "makeup", "profumeria",
            "enel", "tim", "vodafone", "wind", "fastweb",
            "affitto", "mutuo", "condominio",
            "zalando", "zara", "h&m", "shein", "vinted",
            "ikea", "leroy", "obi",
            "libro", "libri", "rivista", "giornale",
            "abbonamento", "biglietto", "biglietti",
            "regalo", "regali", "fiori",
            "parrucchiere", "barbiere", "estetista",
            "cambio", "riparazione", "manutenzione"
        };
        
        for (String noun : specificNouns) {
            if (lowerText.contains(noun)) {
                return noun;
            }
        }
        
        return null;
    }

    private String cleanText(String text) {
        String cleaned = text.replaceAll("(\\d+([.,]\\d{1,2})?)", "");
        cleaned = cleaned.replaceAll("[€$]|euro", "");
        
        cleaned = cleaned.replaceAll("(?:id|numero|codice|transazione)\\s+\\d+", "");
        cleaned = cleaned.replaceAll("(modifica|cambia|aggiorna|elimina|cancella|rimuovi)", "");

        String regex = "\\b(" +
            "johnny|jonny|gionni|gianni|joni|jarfin|jar|" + 
            "aggiungi|inserisci|crea|nuova|nuovo|registra|segna|metti|aggiung|" +
            
            "ho|hai|ha|abbiamo|avete|hanno|" +
            "sono|sei|è|siamo|siete|" +
            "sto|stai|sta|stiamo|state|stanno|" +
            "speso|pagato|comprato|preso|ricevuto|accreditato|accredito|uscito|entrato|" +
            "fatto|faccio|fai|fa|facciamo|fate|fanno|" +
            "dato|do|dai|da|diamo|date|danno|" +
            "andato|vado|vai|va|andiamo|andate|vanno|" +
            "venuto|vengo|vieni|viene|veniamo|venite|vengono|" +
            "devo|devi|deve|dobbiamo|dovete|devono|" +
            "posso|puoi|può|possiamo|potete|possono|" +
            "voglio|vuoi|vuole|vogliamo|volete|vogliono|" +
            "arrivo|arrivato|arriva|arrivano|" +
            "dire|detto|dico|dici|dice|diciamo|dicono|" +
            "stavo|stavi|stava|stavamo|stavate|stavano|" +
            
            "spesa|spes|costo|uscita|entrata|importo|prezzo|valore|soldi|denaro|credito|debito|" +
            "cosa|cose|tipo|roba|" +
            
            "il|lo|la|i|gli|le|un|una|uno|" +
            
            "per|di|a|in|con|su|da|fra|tra|" +
            "del|dello|della|dei|degli|delle|" +
            "al|allo|alla|ai|agli|alle|" +
            "dal|dallo|dalla|dai|dagli|dalle|" +
            "nel|nello|nella|nei|negli|nelle|" +
            "sul|sullo|sulla|sui|sugli|sulle|" +
            
            "mi|ti|ci|vi|si|me|te|ce|ve|se|" +
            "mio|tuo|suo|nostro|vostro|loro|mia|tua|sua|nostra|vostra|" +
            
            "e|o|ma|però|quindi|allora|anche|ancora|" +
            "non|no|sì|si|" +
            "molto|poco|tanto|troppo|più|meno|" +
            "bene|male|meglio|peggio|" +
            "sempre|mai|spesso|raramente|" +
            "qui|qua|lì|là|" +
            "oggi|ieri|domani|ora|adesso|" +
            
            "oh|ah|eh|uhm|" +
            "solo|solamente|soltanto|" +
            "amore|finanziario|ragazzo|ragazza|" +
            "rega|ragazzi|raga|" +
            "giusto|giusti|giusta|giuste|" +
            "niente|nulla|" +
            "specie|specialmente|" +
            "schiavo|" +
            
            "zero|uno|due|tre|quattro|cinque|sei|sette|otto|nove|dieci" +
            ")\\b";

        cleaned = cleaned.replaceAll(regex, " ");
        
        return cleaned.trim().replaceAll("\\s+", " ");
    }

    private boolean containsAny(String text, String... keywords) {
        for (String k : keywords) if (text.contains(k)) return true;
        return false;
    }
}