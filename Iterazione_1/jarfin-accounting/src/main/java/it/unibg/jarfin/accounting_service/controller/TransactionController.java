package it.unibg.jarfin.accounting_service.controller;

import java.util.List;
import java.util.stream.Collectors;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import it.unibg.jarfin.accounting_service.dto.TransactionRequest;
import it.unibg.jarfin.accounting_service.dto.TransactionResponse;
import it.unibg.jarfin.accounting_service.mapper.TransactionMapper;
import it.unibg.jarfin.accounting_service.model.Transaction;
import it.unibg.jarfin.accounting_service.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService service;
    private final TransactionMapper mapper;

    @PostMapping
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody TransactionRequest request) {
        Transaction entity = mapper.toEntity(request);
        
        Transaction saved = service.saveTransaction(entity);
        
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        
        return ResponseEntity.created(location)
        		.body(mapper.toResponse(saved));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> findAll() {
        List<TransactionResponse> response = service.getAllTransactions().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getById(@PathVariable Long id) {
        Transaction transaction = service.getTransactionById(id);
        return ResponseEntity.ok(mapper.toResponse(transaction));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> update(
            @PathVariable Long id, 
            @Valid @RequestBody TransactionRequest request) {
        
        Transaction transactionDetails = mapper.toEntity(request);

        Transaction updatedTransaction = service.updateTransaction(id, transactionDetails);

        return ResponseEntity.ok(mapper.toResponse(updatedTransaction));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}