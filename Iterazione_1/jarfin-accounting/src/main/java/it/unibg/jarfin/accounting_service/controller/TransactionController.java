package it.unibg.jarfin.accounting_service.controller;

import it.unibg.jarfin.accounting_service.dto.TransactionRequest;
import it.unibg.jarfin.accounting_service.dto.TransactionResponse;
import it.unibg.jarfin.accounting_service.mapper.TransactionMapper;
import it.unibg.jarfin.accounting_service.model.Transaction;
import it.unibg.jarfin.accounting_service.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
       
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(mapper.toResponse(saved));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> findAll() {
        List<TransactionResponse> response = service.getAllTransactions().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}