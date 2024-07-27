package br.com.caju.desafio.core.service;

import br.com.caju.desafio.core.entities.dtos.CreateTransactionDTO;
import br.com.caju.desafio.core.entities.enums.MerchantCategory;
import br.com.caju.desafio.core.entities.enums.TransactionResultCode;
import br.com.caju.desafio.core.entities.models.Account;
import br.com.caju.desafio.core.entities.models.Balance;
import br.com.caju.desafio.core.entities.models.Transaction;
import br.com.caju.desafio.core.repositories.AccountRepository;
import br.com.caju.desafio.core.repositories.BalanceRepository;
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
        Account account = accountRepository.findByAccountId(createTransactionDTO.getAccount()).get();
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
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
