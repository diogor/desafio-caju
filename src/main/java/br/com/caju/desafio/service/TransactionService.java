package br.com.caju.desafio.service;

import br.com.caju.desafio.entities.dtos.CreateTransactionDTO;
import br.com.caju.desafio.entities.enums.MerchantCategory;
import br.com.caju.desafio.entities.enums.TransactionResultCode;
import br.com.caju.desafio.entities.models.Balance;
import br.com.caju.desafio.entities.models.Transaction;
import br.com.caju.desafio.repositories.AccountRepository;
import br.com.caju.desafio.repositories.BalanceRepository;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    private final AccountRepository accountRepository;
    private final BalanceRepository balanceRepository;

    public TransactionService(AccountRepository accountRepository, BalanceRepository balanceRepository) {
        this.accountRepository = accountRepository;
        this.balanceRepository = balanceRepository;
    }

    private Transaction convertToTransaction(CreateTransactionDTO createTransactionDTO) {
        Transaction transaction = new Transaction();
        transaction.setAccount(accountRepository.findByAccountId(createTransactionDTO.getAccount()).get());
        transaction.setAmount(createTransactionDTO.getTotalAmount().longValue());
        transaction.setMcc(MerchantCategory.values()[createTransactionDTO.getMcc()]);
        transaction.setMerchant(createTransactionDTO.getMerchant());
        return transaction;
    }

    public TransactionResultCode processTransaction(CreateTransactionDTO createTransactionDTO) {
        Transaction transaction = convertToTransaction(createTransactionDTO);
        Long balance = balanceRepository
                .findByAccountAndMcc(transaction.getAccount(), transaction.getMcc())
                .map(Balance::getAmount)
                .orElse(0L);
        if (balance < transaction.getAmount()) {
            return TransactionResultCode.INSUFFICIENT_FUNDS;
        }
        return TransactionResultCode.APPROVED;
    }
}
