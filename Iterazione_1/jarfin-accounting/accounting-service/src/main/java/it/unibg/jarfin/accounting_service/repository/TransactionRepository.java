package it.unibg.jarfin.accounting_service.repository;

import it.unibg.jarfin.accounting_service.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}