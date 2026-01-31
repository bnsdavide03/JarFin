package it.unibg.jarfin.accounting_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.unibg.jarfin.accounting_service.model.Transaction;
import it.unibg.jarfin.accounting_service.repository.TransactionRepository;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository repository;

    // Salva una transazione (Create)
    public Transaction saveTransaction(Transaction transaction) {
        return repository.save(transaction);
    }

    // Recupera tutte le transazioni (Read)
    public List<Transaction> getAllTransactions() {
        return repository.findAll();
    }

    // Cancella una transazione (Delete)
    public void deleteTransaction(Long id) {
        repository.deleteById(id);
    }
}