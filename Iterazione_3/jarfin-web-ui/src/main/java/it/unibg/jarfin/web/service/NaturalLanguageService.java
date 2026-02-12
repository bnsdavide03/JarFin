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
	
public static final String RISTORANTE = "Ristorante";
    public static final String BAR_COLAZIONE = "Bar/Colazione";
    public static final String SUPERMERCATO = "Supermercato";
    public static final String TRASPORTI = "Trasporti";
    public static final String BOLLETTE = "Bollette";
    public static final String CASA = "Casa";
    public static final String SVAGO = "Svago";
    public static final String SHOPPING = "Shopping";
    public static final String SALUTE = "Salute";
    public static final String SALUTE_SPORT = "Salute/Sport";
    public static final String CURA_PERSONALE = "Cura Personale";
    public static final String STIPENDIO = "Stipendio";
    public static final String BONIFICO = "Bonifico";
    public static final String ENTRATA = "Entrata";
	

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

        CATEGORY_MAP.put("mcdonald", RISTORANTE);
        CATEGORY_MAP.put("burger", RISTORANTE);
        CATEGORY_MAP.put("pizza", RISTORANTE);
        CATEGORY_MAP.put("sushi", RISTORANTE);
        CATEGORY_MAP.put("ristorante", RISTORANTE);
        CATEGORY_MAP.put("trattoria", RISTORANTE);
        CATEGORY_MAP.put("bar", BAR_COLAZIONE);
        CATEGORY_MAP.put("colazione", BAR_COLAZIONE);
        CATEGORY_MAP.put("aperitivo", BAR_COLAZIONE);
        CATEGORY_MAP.put("caffè", BAR_COLAZIONE);

        CATEGORY_MAP.put("esselunga", SUPERMERCATO);
        CATEGORY_MAP.put("coop", SUPERMERCATO);
        CATEGORY_MAP.put("lidl", SUPERMERCATO);
        CATEGORY_MAP.put("conad", SUPERMERCATO);
        CATEGORY_MAP.put("carrefour", SUPERMERCATO);
        CATEGORY_MAP.put("eurospin", SUPERMERCATO);
        CATEGORY_MAP.put("spesa", SUPERMERCATO);
        CATEGORY_MAP.put("supermercato", SUPERMERCATO);
        CATEGORY_MAP.put("alimentari", SUPERMERCATO);
        CATEGORY_MAP.put("ortofrutta", SUPERMERCATO);

        CATEGORY_MAP.put("benzina", TRASPORTI);
        CATEGORY_MAP.put("diesel", TRASPORTI);
        CATEGORY_MAP.put("treno", TRASPORTI);
        CATEGORY_MAP.put("trenitalia", TRASPORTI);
        CATEGORY_MAP.put("italo", TRASPORTI);
        CATEGORY_MAP.put("bus", TRASPORTI);
        CATEGORY_MAP.put("autostrada", TRASPORTI);
        CATEGORY_MAP.put("pedaggio", TRASPORTI);
        CATEGORY_MAP.put("parcheggio", TRASPORTI);
        CATEGORY_MAP.put("uber", TRASPORTI);
        CATEGORY_MAP.put("taxi", TRASPORTI);
        CATEGORY_MAP.put("aereo", TRASPORTI);
        CATEGORY_MAP.put("ryanair", TRASPORTI);

        CATEGORY_MAP.put("luce", BOLLETTE);
        CATEGORY_MAP.put("gas", BOLLETTE);
        CATEGORY_MAP.put("enel", BOLLETTE);
        CATEGORY_MAP.put("a2a", BOLLETTE);
        CATEGORY_MAP.put("internet", BOLLETTE);
        CATEGORY_MAP.put("wifi", BOLLETTE);
        CATEGORY_MAP.put("vodafone", BOLLETTE);
        CATEGORY_MAP.put("tim", BOLLETTE);
        CATEGORY_MAP.put("affitto", CASA);
        CATEGORY_MAP.put("mutuo", CASA);
        CATEGORY_MAP.put("ikea", CASA);
        CATEGORY_MAP.put("leroy", CASA);

        CATEGORY_MAP.put("netflix", SVAGO);
        CATEGORY_MAP.put("spotify", SVAGO);
        CATEGORY_MAP.put("cinema", SVAGO);
        CATEGORY_MAP.put("amazon", SHOPPING);
        CATEGORY_MAP.put("zalando", SHOPPING);
        CATEGORY_MAP.put("vinted", SHOPPING);
        CATEGORY_MAP.put("shein", SHOPPING);
        CATEGORY_MAP.put("zara", SHOPPING);
        CATEGORY_MAP.put("h&m", SHOPPING);

        CATEGORY_MAP.put("palestra", SALUTE_SPORT);
        CATEGORY_MAP.put("padel", SALUTE_SPORT);
        CATEGORY_MAP.put("farmacia", SALUTE);
        CATEGORY_MAP.put("medico", SALUTE);
        CATEGORY_MAP.put("dentista", SALUTE);
        CATEGORY_MAP.put("visita", SALUTE);

        CATEGORY_MAP.put("parrucchiere", CURA_PERSONALE);
        CATEGORY_MAP.put("barbiere", CURA_PERSONALE);
        CATEGORY_MAP.put("estetista", CURA_PERSONALE);
        CATEGORY_MAP.put("trucco", CURA_PERSONALE);
        CATEGORY_MAP.put("makeup", CURA_PERSONALE);
        CATEGORY_MAP.put("manicure", CURA_PERSONALE);
        CATEGORY_MAP.put("pedicure", CURA_PERSONALE);
        CATEGORY_MAP.put("massaggio", CURA_PERSONALE);
        CATEGORY_MAP.put("spa", CURA_PERSONALE);
        CATEGORY_MAP.put("centro estetico", CURA_PERSONALE);
        CATEGORY_MAP.put("salone", CURA_PERSONALE);
        CATEGORY_MAP.put("profumeria", CURA_PERSONALE);
        CATEGORY_MAP.put("cosmetica", CURA_PERSONALE);

        CATEGORY_MAP.put("stipendio", STIPENDIO);
        CATEGORY_MAP.put("bonifico", BONIFICO);
        CATEGORY_MAP.put("rimborso", ENTRATA);
        CATEGORY_MAP.put("regalo", ENTRATA);
        CATEGORY_MAP.put("vendita", ENTRATA);

        CATEGORY_DEFAULT_DESC.put(RISTORANTE, "Pranzo/Cena");
        CATEGORY_DEFAULT_DESC.put(BAR_COLAZIONE, "Caffè");
        CATEGORY_DEFAULT_DESC.put(SUPERMERCATO, "Spesa");
        CATEGORY_DEFAULT_DESC.put(TRASPORTI, "Trasporto");
        CATEGORY_DEFAULT_DESC.put(BOLLETTE, "Utenze");
        CATEGORY_DEFAULT_DESC.put(CASA, "Casa");
        CATEGORY_DEFAULT_DESC.put(SVAGO, "Intrattenimento");
        CATEGORY_DEFAULT_DESC.put(SHOPPING, "Acquisto");
        CATEGORY_DEFAULT_DESC.put(SALUTE, "Salute");
        CATEGORY_DEFAULT_DESC.put(SALUTE_SPORT, "Sport");
        CATEGORY_DEFAULT_DESC.put(CURA_PERSONALE, "Estetica");
        CATEGORY_DEFAULT_DESC.put(STIPENDIO, "Stipendio");
        CATEGORY_DEFAULT_DESC.put(BONIFICO, "Bonifico");
        CATEGORY_DEFAULT_DESC.put(ENTRATA, "Entrata");
    }

    public ParsedTransaction parse(String text) {
        if (text == null || text.trim().length() < 2) return null;
        
        String lowerCaseText = text.trim().toLowerCase();
        
        String[] stopWords = {"stop", "nulla", "niente", "annulla", "chiudi", "jar", "giar"};
        for (String w : stopWords) {
        	if (lowerCaseText.equals(w) || lowerCaseText.startsWith(w + " ")){
        		return null;
        	}
        }

        text = convertVerbsToNouns(text);

        ParsedTransaction result = new ParsedTransaction();
        
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
            if (lowerCaseText.contains(entry.getKey()) && entry.getKey().length() > maxMatchLength) {
                foundCategory = entry.getValue();
                matchedKeyword = entry.getKey();
                maxMatchLength = entry.getKey().length();
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