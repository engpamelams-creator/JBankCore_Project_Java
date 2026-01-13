package com.james.jbank.modules.transactions.controller;

import com.james.jbank.modules.transactions.domain.Transaction;
import com.james.jbank.modules.transactions.dto.CreateTransferDTO;
import com.james.jbank.modules.transactions.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<Transaction> transfer(@RequestBody @Valid CreateTransferDTO dto) {
        Transaction transaction = transactionService.performTransfer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }
}
