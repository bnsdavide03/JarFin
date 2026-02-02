package it.unibg.jarfin.accounting_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.unibg.jarfin.accounting_service.exception.EntityNotFoundException;
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
        Transaction inputTransaction = new Transaction();
        inputTransaction.setAmount(new BigDecimal("100.00"));
        inputTransaction.setDescription("Spesa Test");
        inputTransaction.setCategory("Cibo");
        inputTransaction.setDate(LocalDate.now());

        Transaction savedTransaction = new Transaction();
        savedTransaction.setId(1L);
        savedTransaction.setAmount(new BigDecimal("100.00"));
        
        when(repository.save(any(Transaction.class))).thenReturn(savedTransaction);

        Transaction result = service.saveTransaction(inputTransaction);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(new BigDecimal("100.00"), result.getAmount());
        verify(repository, times(1)).save(inputTransaction);
    }

    @Test
    public void testDeleteTransaction() {
        Long idToDelete = 1L;
        when(repository.existsById(idToDelete)).thenReturn(true);

        service.deleteTransaction(idToDelete);

        verify(repository, times(1)).deleteById(idToDelete);
    }
    
    @Test
    public void testGetAllTransactions() {
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

        when(repository.findAll()).thenReturn(mockList);

        List<Transaction> result = service.getAllTransactions();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(new BigDecimal("50.00"), result.get(0).getAmount());
        
        verify(repository, times(1)).findAll();
    }
    
    @Test
    public void testDeleteTransaction_NotFound() {
        Long idNonEsistente = 99L;
        when(repository.existsById(idNonEsistente)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> {
            service.deleteTransaction(idNonEsistente);
        });

        verify(repository, never()).deleteById(any());
    }
}