package br.com.caju.desafio.core.service;

import br.com.caju.desafio.core.entities.enums.MerchantCategory;
import br.com.caju.desafio.core.entities.enums.TransactionResultCode;
import br.com.caju.desafio.core.entities.models.Balance;
import br.com.caju.desafio.core.entities.models.Transaction;
import br.com.caju.desafio.core.repositories.AccountRepository;
import br.com.caju.desafio.core.repositories.BalanceRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final BalanceRepository balanceRepository;

    public AccountService(AccountRepository accountRepository, BalanceRepository balanceRepository) {
        this.accountRepository = accountRepository;
        this.balanceRepository = balanceRepository;
    }

    @Transactional
    protected Long getAccountBalance(String account, MerchantCategory mcc) {
        return balanceRepository
                .findByAccountAndMcc(accountRepository.findByAccountId(account).get(), mcc)
                .map(Balance::getAmount)
                .orElse(0L);
    }

    @Transactional
    protected TransactionResultCode debitAccount(Transaction transaction, MerchantCategory mcc) {
        Long balance = getAccountBalance(transaction.getAccount().getAccountId(), mcc);
        if (balance < transaction.getAmount()) return TransactionResultCode.INSUFFICIENT_FUNDS;
        balanceRepository.debitAccount(transaction.getAccount(), mcc, transaction.getAmount());
        return TransactionResultCode.APPROVED;
    }
}
