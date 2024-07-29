package br.com.caju.desafio.core.service;

import br.com.caju.desafio.core.definitions.MerchantCategories;
import br.com.caju.desafio.core.entities.dtos.CreateTransactionDTO;
import br.com.caju.desafio.core.entities.enums.MerchantCategory;
import br.com.caju.desafio.core.entities.enums.TransactionResultCode;
import br.com.caju.desafio.core.entities.models.Account;
import br.com.caju.desafio.core.entities.models.Transaction;
import br.com.caju.desafio.core.repositories.AccountRepository;
import br.com.caju.desafio.web.http.exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class TransactionService {
    private final AccountRepository accountRepository;
    private final MerchantCategories merchantCategories;
    private final AccountService accountService;

    public TransactionService(AccountRepository accountRepository, AccountService accountService, MerchantCategories merchantCategories) {
        this.accountRepository = accountRepository;
        this.merchantCategories = merchantCategories;
        this.accountService = accountService;
    }

    private MerchantCategory getMccByValue(int value) {
        try {
            return merchantCategories.mccs().stream().filter(mcc -> mcc.value() == value).findFirst().get().category();
        } catch (NoSuchElementException _) {
            return MerchantCategory.CASH;
        }
    }

    private Transaction convertToTransaction(CreateTransactionDTO createTransactionDTO) throws NotFoundException {
        Optional<Account> account = accountRepository.findByAccountId(createTransactionDTO.getAccount());

        if (account.isEmpty()) {
            throw new NotFoundException("Account not found");
        }

        Transaction transaction = new Transaction();
        transaction.setAccount(account.get());
        transaction.setAmount(createTransactionDTO.getTotalAmount().longValue());
        transaction.setMcc(this.getMccByValue(createTransactionDTO.getMcc()));
        transaction.setMerchant(createTransactionDTO.getMerchant());
        return transaction;
    }


    @Transactional
    public TransactionResultCode processTransaction(CreateTransactionDTO createTransactionDTO) throws NotFoundException {
        Transaction transaction = convertToTransaction(createTransactionDTO);
        Long balance = accountService.getAccountBalance(transaction.getAccount().getAccountId(), transaction.getMcc());

        if (transaction.getMcc() != MerchantCategory.CASH) {
            if (balance < transaction.getAmount()) return accountService.debitAccount(transaction, MerchantCategory.CASH);
        }

        return accountService.debitAccount(transaction, transaction.getMcc());
    }
}
