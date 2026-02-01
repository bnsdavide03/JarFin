package it.unibg.jarfin.accounting_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.unibg.jarfin.accounting_service.model.Transaction;
import it.unibg.jarfin.accounting_service.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository repository;

    @InjectMocks
    private TransactionService service;

    @Test
    public void testSaveTransaction() {
        // 1. Arrange
        Transaction inputTransaction = new Transaction();
        inputTransaction.setAmount(new BigDecimal("100.00")); // Nota: Stringa nel costruttore per precisione
        inputTransaction.setDescription("Spesa Test");
        inputTransaction.setCategory("Cibo");
        inputTransaction.setDate(LocalDate.now());

        Transaction savedTransaction = new Transaction();
        savedTransaction.setId(1L); // Simulo che il DB abbia dato un ID
        savedTransaction.setAmount(new BigDecimal("100.00"));
        
        when(repository.save(any(Transaction.class))).thenReturn(savedTransaction);

        // 2. Act
        Transaction result = service.saveTransaction(inputTransaction);

        // 3. Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(new BigDecimal("100.00"), result.getAmount());
        verify(repository, times(1)).save(inputTransaction);
    }

    @Test
    public void testDeleteTransaction() {
        // 1. Arrange
        Long idToDelete = 1L;
        when(repository.existsById(idToDelete)).thenReturn(true);

        // 2. Act
        service.deleteTransaction(idToDelete);

        // 3. Assert
        verify(repository, times(1)).deleteById(idToDelete);
    }
    
    @Test
    public void testGetAllTransactions() {
        // 1. Arrange (Preparo i dati finti)
        Transaction t1 = new Transaction();
        t1.setId(1L);
        t1.setAmount(new BigDecimal("50.00"));
        t1.setCategory("Svago");
        t1.setDate(LocalDate.now());

        Transaction t2 = new Transaction();
        t2.setId(2L);
        t2.setAmount(new BigDecimal("120.00"));
        t2.setCategory("Spesa");
        t2.setDate(LocalDate.now());

        List<Transaction> mockList = Arrays.asList(t1, t2);

        // Quando il repository viene chiamato, restituisci la lista finta
        when(repository.findAll()).thenReturn(mockList);

        // 2. Act (Chiamo il service)
        List<Transaction> result = service.getAllTransactions();

        // 3. Assert (Verifico i risultati)
        assertNotNull(result);
        assertEquals(2, result.size()); // Mi aspetto 2 transazioni
        assertEquals(new BigDecimal("50.00"), result.get(0).getAmount()); // Verifico il dato della prima
        
        // Verifico che il metodo findAll del repository sia stato chiamato 1 volta
        verify(repository, times(1)).findAll();
    }
}