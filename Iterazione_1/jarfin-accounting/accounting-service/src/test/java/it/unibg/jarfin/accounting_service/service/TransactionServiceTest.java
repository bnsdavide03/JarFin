package it.unibg.jarfin.accounting_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

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
        // 1. Arrange (Preparo i dati)
        Transaction t = new Transaction();
        t.setAmount(100.0);
        t.setDescription("Spesa Test");
        
        when(repository.save(any(Transaction.class))).thenReturn(t);

        // 2. Act (Eseguo il metodo del Service)
        Transaction saved = service.saveTransaction(t);

        // 3. Assert (Verifico che il risultato sia corretto)
        assertNotNull(saved);
        assertEquals(100.0, saved.getAmount());
        verify(repository, times(1)).save(t);
    }

    @Test
    public void testGetAllTransactions() {
        // Arrange
        Transaction t1 = new Transaction();
        Transaction t2 = new Transaction();
        when(repository.findAll()).thenReturn(Arrays.asList(t1, t2));

        // Act
        List<Transaction> list = service.getAllTransactions();

        // Assert
        assertEquals(2, list.size());
        verify(repository, times(1)).findAll();
    }
}