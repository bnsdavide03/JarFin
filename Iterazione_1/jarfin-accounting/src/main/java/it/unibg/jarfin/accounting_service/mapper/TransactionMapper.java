package it.unibg.jarfin.accounting_service.mapper;

import it.unibg.jarfin.accounting_service.dto.TransactionRequest;
import it.unibg.jarfin.accounting_service.dto.TransactionResponse;
import it.unibg.jarfin.accounting_service.model.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public Transaction toEntity(TransactionRequest request) {
        Transaction t = new Transaction();
        t.setAmount(request.getAmount());
        t.setDate(request.getDate());
        t.setCategory(request.getCategory());
        t.setDescription(request.getDescription());
        return t;
    }

    public TransactionResponse toResponse(Transaction entity) {
        TransactionResponse r = new TransactionResponse();
        r.setId(entity.getId());
        r.setAmount(entity.getAmount());
        r.setDate(entity.getDate());
        r.setCategory(entity.getCategory());
        r.setDescription(entity.getDescription());
        return r;
    }
}