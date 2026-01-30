package it.unibg.jarfin.accounting_service.controller;

import it.unibg.jarfin.accounting_service.model.Transaction;
import it.unibg.jarfin.accounting_service.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService service;

    // CREATE
    @PostMapping
    public Transaction addTransaction(@RequestBody Transaction transaction) {
        return service.saveTransaction(transaction);
    }

    // READ ALL
    @GetMapping
    public List<Transaction> getTransactions() {
        return service.getAllTransactions();
    }

    // DELETE (Aggiunta per completare il CRUD)
    @DeleteMapping("/{id}")
    public String deleteTransaction(@PathVariable Long id) {
        service.deleteTransaction(id);
        return "Transazione eliminata con successo: " + id;
    }
}