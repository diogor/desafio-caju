package br.com.caju.desafio.core.service;

import br.com.caju.desafio.core.entities.enums.MerchantCategory;
import br.com.caju.desafio.core.entities.enums.TransactionResultCode;
import br.com.caju.desafio.core.entities.models.Balance;
import br.com.caju.desafio.core.entities.models.Transaction;
import br.com.caju.desafio.core.repositories.AccountRepository;
import br.com.caju.desafio.core.repositories.BalanceRepository;
import br.com.caju.desafio.core.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final BalanceRepository balanceRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public Long getAccountBalance(String account, MerchantCategory mcc) {
        return balanceRepository
                .findByAccountAndMcc(accountRepository.findByAccountId(account).get(), mcc)
                .map(Balance::getAmount)
                .orElse(0L);
    }

    @Transactional
    public TransactionResultCode debitAccount(Transaction transaction, MerchantCategory mcc) {
        transaction.setMcc(mcc);
        Long balance = getAccountBalance(transaction.getAccount().getAccountId(), mcc);
        if (balance < transaction.getAmount()) return TransactionResultCode.INSUFFICIENT_FUNDS;
        balanceRepository.debitAccount(transaction.getAccount(), mcc, transaction.getAmount());
        transactionRepository.save(transaction);
        return TransactionResultCode.APPROVED;
    }
}
