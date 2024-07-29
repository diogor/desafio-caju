package br.com.caju.desafio.web.controller;

import br.com.caju.desafio.core.entities.dtos.CreateTransactionDTO;
import br.com.caju.desafio.core.entities.enums.TransactionResultCode;
import br.com.caju.desafio.core.service.TransactionService;
import br.com.caju.desafio.web.http.exceptions.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
      this.transactionService = transactionService;
    }

    private ResponseEntity<Map<String, String>> transactionResponse(TransactionResultCode code) {
        return new ResponseEntity<>(Map.of("code", code.getCode()), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity processTransaction(@Valid @RequestBody CreateTransactionDTO createTransactionDTO) {
        try {
            return transactionResponse(transactionService.processTransaction(createTransactionDTO));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
