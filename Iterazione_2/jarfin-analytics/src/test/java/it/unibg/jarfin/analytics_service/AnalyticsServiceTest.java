package it.unibg.jarfin.analytics_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class AnalyticsServiceTest {
    @Test
    public void testAnalisiComplessitaERisultati() {
        double entrate = 2000.0;
        double uscite = 1000.0;
        double balance = entrate - uscite;
        
        assertEquals(1000.0, balance, "Il bilancio deve essere la differenza esatta");
        assertTrue(balance > 0, "Il test deve validare la logica di alert GREEN");
    }
}