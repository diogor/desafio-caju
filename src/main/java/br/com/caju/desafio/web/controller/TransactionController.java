package br.com.caju.desafio.web.controller;

import br.com.caju.desafio.core.entities.dtos.CreateTransactionDTO;
import br.com.caju.desafio.core.entities.enums.TransactionResultCode;
import br.com.caju.desafio.core.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
      this.transactionService = transactionService;
    }

    @PostMapping
    public TransactionResultCode processTransaction(@Valid @RequestBody CreateTransactionDTO createTransactionDTO) {
      return transactionService.processTransaction(createTransactionDTO);
    }
}
