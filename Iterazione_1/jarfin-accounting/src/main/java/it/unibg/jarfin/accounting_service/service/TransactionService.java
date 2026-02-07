package it.unibg.jarfin.accounting_service.service;

import it.unibg.jarfin.accounting_service.model.Transaction;
import it.unibg.jarfin.accounting_service.repository.TransactionRepository;
import it.unibg.jarfin.accounting_service.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;

    public Transaction saveTransaction(Transaction transaction) {
        log.info("Salvataggio nuova transazione: {}", transaction.getDescription());
        return repository.save(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return repository.findAll();
    }

    @Transactional
    public Transaction updateTransaction(Long id, Transaction transactionDetails) {
        log.info("Richiesta modifica transazione ID: {}", id);

        Transaction existingTransaction = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("Transazione con ID {} non trovata per la modifica", id);
                    return new EntityNotFoundException("Transazione non trovata con ID: " + id);
                });

        existingTransaction.setAmount(transactionDetails.getAmount());
        existingTransaction.setCategory(transactionDetails.getCategory());
        existingTransaction.setDescription(transactionDetails.getDescription());
        existingTransaction.setDate(transactionDetails.getDate());
        existingTransaction.setType(transactionDetails.getType());

        return repository.save(existingTransaction);
    }

    public void deleteTransaction(Long id) {
        if (!repository.existsById(id)) {
            log.error("Tentativo di eliminazione fallito. ID non trovato: {}", id);
            throw new EntityNotFoundException("Transazione non trovata con ID: " + id);
        }
        repository.deleteById(id);
        log.info("Transazione eliminata: {}", id);
    }
    
    public Transaction getTransactionById (Long id) {
    	return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Transazione non trovata con ID: " + id));
    }
}