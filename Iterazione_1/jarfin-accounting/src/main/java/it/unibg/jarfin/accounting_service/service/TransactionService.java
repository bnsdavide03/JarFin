package it.unibg.jarfin.accounting_service.service;

import it.unibg.jarfin.accounting_service.model.Transaction;
import it.unibg.jarfin.accounting_service.repository.TransactionRepository;
import it.unibg.jarfin.accounting_service.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j // Aggiunge logging
@Service
@RequiredArgsConstructor // Genera costruttore automaticamente per i campi final
public class TransactionService {

    private final TransactionRepository repository;

    public Transaction saveTransaction(Transaction transaction) {
        log.info("Salvataggio nuova transazione: {}", transaction.getDescription());
        return repository.save(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return repository.findAll();
    }

    public void deleteTransaction(Long id) {
        if (!repository.existsById(id)) {
            log.error("Tentativo di eliminazione fallito. ID non trovato: {}", id);
            throw new EntityNotFoundException("Transazione non trovata con ID: " + id);
        }
        repository.deleteById(id);
        log.info("Transazione eliminata: {}", id);
    }
}