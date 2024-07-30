package br.com.caju.desafio.web.controller;

import br.com.caju.desafio.core.entities.dtos.CreateTransactionDTO;
import br.com.caju.desafio.core.entities.enums.TransactionResultCode;
import br.com.caju.desafio.core.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Map<String, String>> processTransaction(@Valid @RequestBody CreateTransactionDTO createTransactionDTO) {
        try {
            return transactionResponse(transactionService.processTransaction(createTransactionDTO));
        } catch (Exception _) {
            return transactionResponse(TransactionResultCode.ERROR);
        }
    }
}
